import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ProgressLog, ProgressLogRequest } from '../models/plan.model';

/** Endpoints for weight and body-fat logs. */
@Injectable({ providedIn: 'root' })
export class ProgressService {
  private readonly http = inject(HttpClient);

  /** GET /api/progress - newest first, as ordered by the backend repository. */
  getLogs(): Observable<ProgressLog[]> {
    return this.http.get<ProgressLog[]>('/api/progress');
  }

  /** POST /api/progress - logDate defaults to today on the backend. */
  addLog(payload: ProgressLogRequest): Observable<ProgressLog> {
    return this.http.post<ProgressLog>('/api/progress', payload);
  }
}