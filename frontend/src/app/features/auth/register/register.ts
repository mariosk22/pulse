import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { apiErrorMessage } from '../../../core/services/api-error';

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
      next: () => {
        this.submitting.set(false);
        void this.router.navigateByUrl('/onboarding');
      },
      error: (err) => {
        this.submitting.set(false);
        this.errorMessage.set(
          apiErrorMessage(err, 'Registration failed. Please try again.'),
        );
      },
    });
  }
}