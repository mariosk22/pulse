import { HttpErrorResponse } from '@angular/common/http';
import { ApiError } from '../models/user.model';

/**
 * Pulls the message out of the backend's GlobalExceptionHandler body, which
 * always responds with {timestamp, status, error, message}.
 */
export function apiErrorMessage(err: unknown, fallback: string): string {
  if (!(err instanceof HttpErrorResponse)) return fallback;

  const body = err.error as ApiError | string | null;

  if (typeof body === 'string' && body.trim()) return body;

  const message = (body as ApiError | null)?.message;
  if (message) return message;

  return err.status === 0
    ? 'Backend is not reachable. Is the API running on port 8080?'
    : fallback;
}

/** True when the backend reported that the resource does not exist yet. */
export function isNotFound(err: unknown): boolean {
  return err instanceof HttpErrorResponse && err.status === 404;
}