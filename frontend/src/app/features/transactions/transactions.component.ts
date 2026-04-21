import { Component } from '@angular/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [MatProgressSpinnerModule],
  template: `
    <h2>Transactions</h2>
    <p>Transaction table will be implemented in Epic 5.</p>
  `
})
export class TransactionsComponent {}
