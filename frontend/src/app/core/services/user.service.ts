import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { OnboardingRequest, Sport, User } from '../models/user.model';

/**
 * Endpoints for the caller's own profile. Both require the JWT attached by
 * authInterceptor, so every call is authenticated.
 */
@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly http = inject(HttpClient);

  /** GET /api/users/me */
  getProfile(): Observable<User> {
    return this.http.get<User>('/api/users/me');
  }

  /** PUT /api/users/me - used by onboarding to store sport, level and goal. */
  updateOnboarding(payload: OnboardingRequest): Observable<User> {
    return this.http.put<User>('/api/users/me', payload);
  }

  /** GET /api/sports - permitAll in SecurityConfig, used before onboarding. */
  getSports(): Observable<Sport[]> {
    return this.http.get<Sport[]>('/api/sports');
  }
}