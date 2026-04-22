import {
  AfterViewInit,
  Component,
  inject,
  OnInit,
  ViewChild,
} from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';

import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatSidenavModule, MatSidenav } from '@angular/material/sidenav';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatBadgeModule } from '@angular/material/badge';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCardModule } from '@angular/material/card';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBar } from '@angular/material/snack-bar';

import { Transaction } from '../../core/models/transaction.model';
import { Country, LegalEntity } from '../../core/models/reference-data.model';
import { TransactionApiService } from '../../core/services/transaction-api.service';
import { ReferenceDataApiService } from '../../core/services/reference-data-api.service';
import { EditPanelComponent } from './edit-panel/edit-panel.component';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [
    DecimalPipe,
    FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatSidenavModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatSelectModule,
    MatButtonToggleModule,
    MatBadgeModule,
    MatProgressSpinnerModule,
    MatCardModule,
    MatTooltipModule,
    EditPanelComponent,
  ],
  templateUrl: './transactions.component.html',
  styleUrl: './transactions.component.scss',
})
export class TransactionsComponent implements OnInit, AfterViewInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild('editSidenav') editSidenav!: MatSidenav;

  loading = true;
  allTransactions: Transaction[] = [];
  dataSource = new MatTableDataSource<Transaction>();

  periods: string[] = [];
  legalEntities: LegalEntity[] = [];
  countries: Country[] = [];

  filterPeriod = '';
  filterFlow: 'ALL' | 'ARRIVAL' | 'DISPATCH' = 'ALL';
  filterEntityId: number | null = null;
  filterCountry = '';

  selectedTransaction: Transaction | null = null;

  readonly displayedColumns = [
    'period', 'legalEntity', 'flow', 'counterpartCountry', 'part',
    'cn8Code', 'quantity', 'netMassKg', 'statisticalValueEur',
    'modeOfTransport', 'notc', 'deliveryTerms', 'actions',
  ];

  private readonly txApi = inject(TransactionApiService);
  private readonly refApi = inject(ReferenceDataApiService);
  private readonly snackBar = inject(MatSnackBar);

  ngOnInit(): void {
    forkJoin({
      transactions: this.txApi.listAll(),
      periods: this.refApi.getPeriods(),
      legalEntities: this.refApi.getLegalEntities(),
      countries: this.refApi.getCountries(),
    }).subscribe({
      next: ({ transactions, periods, legalEntities, countries }) => {
        this.periods = periods;
        this.legalEntities = legalEntities;
        this.countries = countries;
        this.allTransactions = [...transactions].sort((a, b) => {
          if (a.period !== b.period) return b.period.localeCompare(a.period);
          return b.statisticalValueEur - a.statisticalValueEur;
        });
        this.dataSource.data = this.allTransactions;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.dataSource.sortingDataAccessor = (item, property) => {
      switch (property) {
        case 'legalEntity': return item.legalEntity.name;
        case 'counterpartCountry': return item.counterpartCountryName;
        case 'part': return item.part.name;
        default: return (item as Record<string, unknown>)[property] as string | number;
      }
    };
  }

  applyFilters(): void {
    let result = this.allTransactions;
    if (this.filterPeriod) {
      result = result.filter(t => t.period === this.filterPeriod);
    }
    if (this.filterFlow !== 'ALL') {
      result = result.filter(t => t.flowType === this.filterFlow);
    }
    if (this.filterEntityId != null) {
      result = result.filter(t => t.legalEntity.id === this.filterEntityId);
    }
    if (this.filterCountry) {
      result = result.filter(t => t.counterpartCountryCode === this.filterCountry);
    }
    this.dataSource.data = result;
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  clearFilters(): void {
    this.filterPeriod = '';
    this.filterFlow = 'ALL';
    this.filterEntityId = null;
    this.filterCountry = '';
    this.dataSource.data = this.allTransactions;
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  get activeFilterCount(): number {
    let n = 0;
    if (this.filterPeriod) n++;
    if (this.filterFlow !== 'ALL') n++;
    if (this.filterEntityId != null) n++;
    if (this.filterCountry) n++;
    return n;
  }

  get filteredCount(): number {
    return this.dataSource.data.length;
  }

  get distinctCountries(): Array<{ code: string; name: string }> {
    const seen = new Set<string>();
    const list: Array<{ code: string; name: string }> = [];
    for (const t of this.allTransactions) {
      if (!seen.has(t.counterpartCountryCode)) {
        seen.add(t.counterpartCountryCode);
        list.push({ code: t.counterpartCountryCode, name: t.counterpartCountryName });
      }
    }
    return list.sort((a, b) => a.name.localeCompare(b.name));
  }

  openEdit(transaction: Transaction): void {
    this.selectedTransaction = transaction;
    this.editSidenav.open();
  }

  onTransactionSaved(updated: Transaction): void {
    const idx = this.allTransactions.findIndex(t => t.id === updated.id);
    if (idx !== -1) {
      this.allTransactions[idx] = updated;
      this.applyFilters();
    }
    this.editSidenav.close();
    this.snackBar.open('Transaction updated', 'Close', { duration: 3000 });
  }

  onEditCancelled(): void {
    this.editSidenav.close();
  }

  countryFlag(code: string): string {
    if (!code || code.length !== 2) return '';
    return [...code.toUpperCase()]
      .map(c => String.fromCodePoint(0x1f1e6 + c.charCodeAt(0) - 65))
      .join('');
  }

  transportLabel(code: number): string {
    const labels: Record<number, string> = {
      1: 'Sea', 3: 'Road', 4: 'Air', 5: 'Post', 7: 'Fixed', 9: 'Own',
    };
    return labels[code] ?? String(code);
  }
}
