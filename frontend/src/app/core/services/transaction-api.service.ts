import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { PagedResponse, Transaction, TransactionPatch } from '../models/transaction.model';

@Injectable({ providedIn: 'root' })
export class TransactionApiService {
  private readonly baseUrl = `${environment.apiUrl}/transactions`;
  private readonly http = inject(HttpClient);

  /** Load all transactions (no server-side pagination — demo dataset is 60 rows). */
  listAll(): Observable<Transaction[]> {
    return this.http
      .get<PagedResponse<Transaction>>(this.baseUrl, { params: { page: '0', size: '100' } })
      .pipe(map(r => r.content));
  }

  patch(id: number, data: TransactionPatch): Observable<Transaction> {
    return this.http.patch<Transaction>(`${this.baseUrl}/${id}`, data);
  }
}
