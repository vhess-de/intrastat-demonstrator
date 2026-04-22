import { Component, inject, OnInit } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';

import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';

import {
  GroupHeaderRow,
  ReportLine,
  ReportResponse,
  SkeletonRow,
  TableRow,
} from '../../core/models/report.model';
import { LegalEntity } from '../../core/models/reference-data.model';
import { ReportApiService } from '../../core/services/report-api.service';
import { ReferenceDataApiService } from '../../core/services/reference-data-api.service';

@Component({
  selector: 'app-report',
  standalone: true,
  imports: [
    DecimalPipe,
    FormsModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatTableModule,
    MatTooltipModule,
  ],
  templateUrl: './report.component.html',
  styleUrl: './report.component.scss',
})
export class ReportComponent implements OnInit {
  periods: string[] = [];
  legalEntities: LegalEntity[] = [];

  selectedPeriod = '';
  selectedEntityId: number | null = null;
  selectedFlow: 'ALL' | 'ARRIVAL' | 'DISPATCH' = 'ALL';

  initialLoading = true;
  loading = false;
  report: ReportResponse | null = null;
  tableRows: TableRow[] = [];

  readonly dataColumns = [
    'legalEntity', 'flow', 'counterpartCountry', 'cn8Code', 'partName',
    'qty', 'netMass', 'statValue', 'invValue',
    'transport', 'notc', 'terms', 'txnCount',
  ];

  // Row-type predicates for *matRowDef when:
  readonly isDataRow = (_: number, row: TableRow): boolean =>
    !('rowType' in row);
  readonly isGroupHeader = (_: number, row: TableRow): boolean =>
    'rowType' in row && (row as GroupHeaderRow | SkeletonRow).rowType === 'GROUP_HEADER';
  readonly isSkeleton = (_: number, row: TableRow): boolean =>
    'rowType' in row && (row as SkeletonRow).rowType === 'SKELETON';

  private readonly reportApi = inject(ReportApiService);
  private readonly refApi = inject(ReferenceDataApiService);

  ngOnInit(): void {
    forkJoin({
      periods: this.refApi.getPeriods(),
      entities: this.refApi.getLegalEntities(),
    }).subscribe({
      next: ({ periods, entities }) => {
        this.periods = periods;
        this.legalEntities = entities;
        this.selectedPeriod = periods[0] ?? '';
        this.initialLoading = false;
        if (this.selectedPeriod) this.loadReport();
      },
      error: () => {
        this.initialLoading = false;
      },
    });
  }

  loadReport(): void {
    if (!this.selectedPeriod) return;
    this.loading = true;
    this.tableRows = Array.from({ length: 5 }, () => ({ rowType: 'SKELETON' as const }));

    const flowType = this.selectedFlow === 'ALL' ? null : this.selectedFlow;

    this.reportApi
      .getReport(this.selectedPeriod, this.selectedEntityId, flowType)
      .subscribe({
        next: report => {
          this.report = report;
          this.tableRows = this.buildTableRows(report.lines);
          this.loading = false;
        },
        error: () => {
          this.loading = false;
          this.tableRows = [];
        },
      });
  }

  clearFilters(): void {
    this.selectedFlow = 'ALL';
    this.selectedEntityId = null;
    this.loadReport();
  }

  // ── Grand total footer values ───────────────────────────────────────────────

  get footerQty(): number {
    return this.report?.summary.totalQuantity ?? 0;
  }
  get footerNetMass(): number {
    return this.report?.summary.totalNetMassKg ?? 0;
  }
  get footerStatValue(): number {
    return this.report?.summary.totalStatisticalValueEur ?? 0;
  }
  get footerInvValue(): number {
    return this.report?.lines.reduce((s, l) => s + l.totalInvoiceValueEur, 0) ?? 0;
  }
  get footerTxnCount(): number {
    return this.report?.lines.reduce((s, l) => s + l.transactionCount, 0) ?? 0;
  }
  get showFooter(): boolean {
    return !this.loading && (this.report?.lines.length ?? 0) > 0;
  }

  // ── Helpers ────────────────────────────────────────────────────────────────

  countryFlag(code: string): string {
    if (!code || code.length !== 2) return '';
    return [...code.toUpperCase()]
      .map(c => String.fromCodePoint(0x1f1e6 + c.charCodeAt(0) - 65))
      .join('');
  }

  groupLabel(row: GroupHeaderRow): string {
    return row.flowType === 'ARRIVAL' ? 'ARRIVALS' : 'DISPATCHES';
  }

  // ── Private ────────────────────────────────────────────────────────────────

  private buildTableRows(lines: ReportLine[]): TableRow[] {
    const arrivals = lines.filter(l => l.flowType === 'ARRIVAL');
    const dispatches = lines.filter(l => l.flowType === 'DISPATCH');
    const rows: TableRow[] = [];

    if (arrivals.length > 0) {
      rows.push(this.makeGroupHeader('ARRIVAL', arrivals));
      rows.push(...arrivals);
    }
    if (dispatches.length > 0) {
      rows.push(this.makeGroupHeader('DISPATCH', dispatches));
      rows.push(...dispatches);
    }
    return rows;
  }

  private makeGroupHeader(
    flowType: 'ARRIVAL' | 'DISPATCH',
    lines: ReportLine[],
  ): GroupHeaderRow {
    return {
      rowType: 'GROUP_HEADER',
      flowType,
      lineCount: lines.length,
      subtotalQty: lines.reduce((s, l) => s + l.totalQuantity, 0),
      subtotalNetMassKg: lines.reduce((s, l) => s + l.totalNetMassKg, 0),
      subtotalStatValueEur: lines.reduce((s, l) => s + l.totalStatisticalValueEur, 0),
      subtotalInvValueEur: lines.reduce((s, l) => s + l.totalInvoiceValueEur, 0),
      subtotalTxnCount: lines.reduce((s, l) => s + l.transactionCount, 0),
    };
  }
}
