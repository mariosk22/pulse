import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { ApiError } from '../../../core/models/user.model';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  readonly form = this.fb.nonNullable.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
  });

  readonly errorMessage = signal('');
  readonly submitting = signal(false);

  OnSubmit(): void {
    if (this.form.invalid || this.submitting()) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    this.errorMessage.set('');

    const { firstName, lastName, email, password } = this.form.getRawValue();
    const fullName = `${firstName} ${lastName}`.trim();

    this.authService.register({ email, password, fullName }).subscribe({
      next: () => this.router.navigateByUrl('/onboarding'),
      error: (err: HttpErrorResponse) => {
        this.submitting.set(false);
        this.errorMessage.set(this.readError(err, 'Registration failed. Please try again.'));
      },
    });
  }

  /** Surfaces the message from the backend's GlobalExceptionHandler body. */
  private readError(err: HttpErrorResponse, fallback: string): string {
    const body = err.error as ApiError | null;
    return body?.message ?? fallback;
  }
}
