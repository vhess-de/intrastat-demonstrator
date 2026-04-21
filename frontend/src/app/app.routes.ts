import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/transactions',
    pathMatch: 'full'
  },
  {
    path: 'transactions',
    loadComponent: () =>
      import('./features/transactions/transactions.component').then(m => m.TransactionsComponent)
  },
  {
    path: 'report',
    loadComponent: () =>
      import('./features/report/report.component').then(m => m.ReportComponent)
  },
  {
    path: '**',
    redirectTo: '/transactions'
  }
];
