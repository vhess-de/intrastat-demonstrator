import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Country, LegalEntity, Part } from '../models/reference-data.model';

@Injectable({ providedIn: 'root' })
export class ReferenceDataApiService {
  private readonly apiUrl = environment.apiUrl;
  private readonly http = inject(HttpClient);

  getLegalEntities(): Observable<LegalEntity[]> {
    return this.http.get<LegalEntity[]>(`${this.apiUrl}/legal-entities`);
  }

  getCountries(): Observable<Country[]> {
    return this.http.get<Country[]>(`${this.apiUrl}/countries`);
  }

  getParts(): Observable<Part[]> {
    return this.http.get<Part[]>(`${this.apiUrl}/parts`);
  }

  getPeriods(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/report/periods`);
  }
}
