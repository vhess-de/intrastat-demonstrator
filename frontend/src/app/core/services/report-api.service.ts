import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ReportResponse } from '../models/report.model';

@Injectable({ providedIn: 'root' })
export class ReportApiService {
  private readonly baseUrl = `${environment.apiUrl}/report`;
  private readonly http = inject(HttpClient);

  getReport(
    period: string,
    legalEntityId?: number | null,
    flowType?: string | null,
  ): Observable<ReportResponse> {
    let params = new HttpParams().set('period', period);
    if (legalEntityId != null) params = params.set('legalEntityId', String(legalEntityId));
    if (flowType) params = params.set('flowType', flowType);
    return this.http.get<ReportResponse>(this.baseUrl, { params });
  }

  getPeriods(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/periods`);
  }
}
