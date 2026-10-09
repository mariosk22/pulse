import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, forkJoin, of, switchMap, tap } from 'rxjs';
import { AuthResponse, LoginRequest, RegisterRequest, User } from '../models/user.model';

const TOKEN_KEY = 'pulse.token';
const USER_KEY = 'pulse.user';

/**
 * Owns the session: the JWT plus the cached profile from GET /api/users/me.
 *
 * AuthResponse only carries id, email and fullName, so login and register both
 * follow up with the profile endpoint to get sport, level and goal. Those decide
 * whether onboarding still has to run.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  private readonly token = signal<string | null>(this.readStorage(TOKEN_KEY));
  private readonly currentUser = signal<User | null>(this.readUser());

  readonly isAuthenticated = computed(() => this.token() !== null);
  readonly user = this.currentUser.asReadonly();

  /**
   * The backend refuses to generate a training plan or a nutrition plan until
   * sport, level and goal are set, so the router sends users without them to
   * onboarding.
   */
  readonly needsOnboarding = computed(() => {
    const user = this.currentUser();
    return user !== null && (user.sportName === null || user.level === null || user.goal === null);
  });

  login(credentials: LoginRequest): Observable<User> {
    return this.http.post<AuthResponse>('/api/auth/login', credentials).pipe(
      switchMap((response) => this.startSession(response)),
    );
  }

  register(payload: RegisterRequest): Observable<User> {
    return this.http.post<AuthResponse>('/api/auth/register', payload).pipe(
      switchMap((response) => this.startSession(response)),
    );
  }

  /** Bearer token for the JWT interceptor to attach. */
  getToken(): string | null {
    return this.token();
  }

  /** Re-reads the profile after onboarding or any other profile change. */
  refreshProfile(): Observable<User> {
    return this.http.get<User>('/api/users/me').pipe(tap((user) => this.setUser(user)));
  }

  /** Stores a profile the caller already has, e.g. the onboarding response. */
  setUser(user: User): void {
    this.currentUser.set(user);
    this.writeStorage(USER_KEY, user);
  }

  logout(): void {
    this.token.set(null);
    this.currentUser.set(null);
    this.clearStorage(TOKEN_KEY);
    this.clearStorage(USER_KEY);
  }

  /** Persists the token, then fills the cached profile from the backend. */
  private startSession(response: AuthResponse): Observable<User> {
    this.token.set(response.token);
    this.writeStorage(TOKEN_KEY, response.token);

    return this.refreshProfile();
  }

  private readStorage(key: string): string | null {
    try {
      return localStorage.getItem(key);
    } catch {
      return null;
    }
  }

  private readUser(): User | null {
    const raw = this.readStorage(USER_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw) as User;
    } catch {
      return null;
    }
  }

  private writeStorage(key: string, value: unknown): void {
    try {
      localStorage.setItem(key, typeof value === 'string' ? value : JSON.stringify(value));
    } catch {
      // Storage can be unavailable in private browsing; the in-memory signal
      // still holds the value for this session.
    }
  }

  private clearStorage(key: string): void {
    try {
      localStorage.removeItem(key);
    } catch {
      // Ignore: nothing to clean up if storage is unavailable.
    }
  }
}