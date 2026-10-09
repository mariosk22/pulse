import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

const NAV_ITEMS = [
  { path: '/dashboard', label: 'Dashboard' },
  { path: '/plan', label: 'Plan' },
  { path: '/nutrition', label: 'Nutrition' },
  { path: '/progress', label: 'Progress' },
];

@Component({
  selector: 'app-shell',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './shell.html',
  styleUrl: './shell.css',
})
export class Shell {
  private readonly auth = inject(AuthService);

  readonly navItems = NAV_ITEMS;
  readonly user = this.auth.user;

  onLogout(): void {
    this.auth.logout();
  }
}