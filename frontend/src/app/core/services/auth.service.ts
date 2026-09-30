import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { AuthResponse, LoginRequest, RegisterRequest, User } from '../models/user.model';

const TOKEN_KEY = 'pulse.token';
const USER_KEY = 'pulse.user';

/**
 * Endpoints under /api/auth/** are permitAll in SecurityConfig, so they do not
 * require a token. The controllers are not implemented on the backend branch
 * yet, so these calls will 404 until that lands.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  private readonly token = signal<string | null>(this.readStorage(TOKEN_KEY));
  private readonly currentUser = signal<User | null>(this.readUser());

  readonly isAuthenticated = computed(() => this.token() !== null);
  readonly user = this.currentUser.asReadonly();

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>('/api/auth/login', credentials)
      .pipe(tap((response) => this.persist(response)));
  }

  register(payload: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>('/api/auth/register', payload)
      .pipe(tap((response) => this.persist(response)));
  }

  /** Bearer token for the JWT interceptor to attach. */
  getToken(): string | null {
    return this.token();
  }

  logout(): void {
    this.token.set(null);
    this.currentUser.set(null);
    this.clearStorage(TOKEN_KEY);
    this.clearStorage(USER_KEY);
  }

  private persist(response: AuthResponse): void {
    this.token.set(response.token);
    const user: User = {
      id: response.userId,
      email: response.email,
      fullName: response.fullName,
      gender: null,
      age: null,
      heightCm: null,
      weightKg: null,
      sportName: null,
      level: null,
      goal: null,
    };
    this.currentUser.set(user);
    this.writeStorage(TOKEN_KEY, response.token);
    this.writeStorage(USER_KEY, user);
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
