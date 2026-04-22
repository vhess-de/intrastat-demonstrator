import {
  Component,
  EventEmitter,
  inject,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import { DecimalPipe } from '@angular/common';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';

import { Transaction, TransactionPatch } from '../../../core/models/transaction.model';
import { Country } from '../../../core/models/reference-data.model';
import { TransactionApiService } from '../../../core/services/transaction-api.service';

// ── Inline confirmation dialog ────────────────────────────────────────────────

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [MatButtonModule, MatDialogModule],
  template: `
    <h2 mat-dialog-title>Discard changes?</h2>
    <mat-dialog-content>Your unsaved changes will be lost.</mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancel</button>
      <button mat-button color="warn" [mat-dialog-close]="true">Discard</button>
    </mat-dialog-actions>
  `,
})
export class ConfirmDialogComponent {}

// ── Edit panel ────────────────────────────────────────────────────────────────

export const TRANSPORT_OPTIONS = [
  { value: 1, label: 'Sea' },
  { value: 3, label: 'Road' },
  { value: 4, label: 'Air' },
  { value: 5, label: 'Post' },
  { value: 7, label: 'Fixed installations' },
  { value: 9, label: 'Own propulsion' },
];

export const DELIVERY_TERMS = [
  'EXW', 'FCA', 'CPT', 'CIP', 'DAP', 'DDP', 'FAS', 'FOB', 'CFR', 'CIF',
];

@Component({
  selector: 'app-edit-panel',
  standalone: true,
  imports: [
    DecimalPipe,
    ReactiveFormsModule,
    MatButtonModule,
    MatDialogModule,
    MatDividerModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatTooltipModule,
  ],
  templateUrl: './edit-panel.component.html',
  styleUrl: './edit-panel.component.scss',
})
export class EditPanelComponent implements OnChanges {
  @Input() transaction!: Transaction;
  @Input() countries: Country[] = [];
  @Output() saved = new EventEmitter<Transaction>();
  @Output() cancelled = new EventEmitter<void>();

  saving = false;
  /** Unit mass in kg, derived from current netMassKg / quantity. */
  private unitMassKg = 0;

  readonly transportOptions = TRANSPORT_OPTIONS;
  readonly deliveryTerms = DELIVERY_TERMS;

  private readonly fb = inject(FormBuilder);
  private readonly api = inject(TransactionApiService);
  private readonly dialog = inject(MatDialog);

  form = this.fb.group({
    quantity: [0, [Validators.required, Validators.min(1)]],
    statisticalValueEur: [0, [Validators.required, Validators.min(0.01)]],
    invoiceValueEur: [0, [Validators.required, Validators.min(0.01)]],
    counterpartCountryCode: ['', Validators.required],
    modeOfTransport: [3, Validators.required],
    natureOfTransactionCode: [11, [Validators.required, Validators.min(11), Validators.max(99)]],
    deliveryTerms: ['', Validators.required],
  });

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['transaction'] && this.transaction) {
      this.unitMassKg = this.transaction.quantity > 0
        ? this.transaction.netMassKg / this.transaction.quantity
        : 0;

      this.form.reset({
        quantity: this.transaction.quantity,
        statisticalValueEur: this.transaction.statisticalValueEur,
        invoiceValueEur: this.transaction.invoiceValueEur,
        counterpartCountryCode: this.transaction.counterpartCountryCode,
        modeOfTransport: this.transaction.modeOfTransport,
        natureOfTransactionCode: this.transaction.natureOfTransactionCode,
        deliveryTerms: this.transaction.deliveryTerms,
      });
    }
  }

  get estimatedNetMass(): number {
    const qty = this.form.get('quantity')?.value ?? 0;
    return this.unitMassKg * (qty > 0 ? qty : 0);
  }

  save(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;

    this.saving = true;
    const patch: TransactionPatch = {
      quantity: this.form.value.quantity ?? undefined,
      statisticalValueEur: this.form.value.statisticalValueEur ?? undefined,
      invoiceValueEur: this.form.value.invoiceValueEur ?? undefined,
      counterpartCountryCode: this.form.value.counterpartCountryCode ?? undefined,
      modeOfTransport: this.form.value.modeOfTransport ?? undefined,
      natureOfTransactionCode: this.form.value.natureOfTransactionCode ?? undefined,
      deliveryTerms: this.form.value.deliveryTerms ?? undefined,
    };

    this.api.patch(this.transaction.id, patch).subscribe({
      next: updated => {
        this.saving = false;
        this.saved.emit(updated);
      },
      error: err => {
        this.saving = false;
        if (err.status === 422 && err.error?.errors) {
          for (const e of err.error.errors as Array<{ field: string; message: string }>) {
            const ctrl = this.form.get(e.field);
            if (ctrl) {
              ctrl.setErrors({ serverError: e.message });
            }
          }
        }
      },
    });
  }

  cancel(): void {
    if (this.form.dirty) {
      this.dialog.open(ConfirmDialogComponent).afterClosed().subscribe(confirmed => {
        if (confirmed) this.cancelled.emit();
      });
    } else {
      this.cancelled.emit();
    }
  }

  /** Helper for template: retrieve a named error from a control. */
  getError(field: string, error: string): string | null {
    const ctrl = this.form.get(field);
    return ctrl?.hasError(error) ? ctrl.getError(error) : null;
  }
}
