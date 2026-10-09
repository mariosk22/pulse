import { inject } from '@angular/core';
import { CanActivateFn, Router, Routes } from '@angular/router';
import { AuthService } from './core/services/auth.service';

const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.isAuthenticated() ? true : router.createUrlTree(['/login']);
};

/** Sends already-authenticated users away from login/register. */
const guestGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.isAuthenticated() ? router.createUrlTree(['/dashboard']) : true;
};

/** Keeps users who already finished onboarding out of the onboarding form. */
const onboardingGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.needsOnboarding() ? true : router.createUrlTree(['/dashboard']);
};

/** Sends users who still owe onboarding data to onboarding first. */
const onboardedGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.needsOnboarding() ? router.createUrlTree(['/onboarding']) : true;
};

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  {
    path: 'login',
    canActivate: [guestGuard],
    loadComponent: () => import('./features/auth/login/login').then((m) => m.Login),
  },
  {
    path: 'register',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./features/auth/register/register').then((m) => m.Register),
  },
  {
    path: 'onboarding',
    canActivate: [authGuard, onboardingGuard],
    loadComponent: () =>
      import('./features/onboarding/onboarding').then((m) => m.Onboarding),
  },
  {
    path: 'dashboard',
    canActivate: [authGuard, onboardedGuard],
    loadComponent: () =>
      import('./features/dashboard/dashboard').then((m) => m.Dashboard),
  },
  {
    path: 'plan',
    canActivate: [authGuard, onboardedGuard],
    loadComponent: () => import('./features/plan/plan').then((m) => m.Plan),
  },
  {
    path: 'nutrition',
    canActivate: [authGuard, onboardedGuard],
    loadComponent: () => import('./features/nutrition/nutrition').then((m) => m.Nutrition),
  },
  {
    path: 'progress',
    canActivate: [authGuard, onboardedGuard],
    loadComponent: () => import('./features/progress/progress').then((m) => m.Progress),
  },
  { path: '**', redirectTo: 'login' },
];