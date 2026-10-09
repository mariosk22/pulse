import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ProgressService } from '../../core/services/progress.service';
import { TrainingPlanService } from '../../core/services/training-plan.service';
import { apiErrorMessage, isNotFound } from '../../core/services/api-error';
import { NutritionPlan, ProgressLog, TrainingPlan } from '../../core/models/plan.model';
import { Shell } from '../layout/shell/shell';

const WEEKDAY_LABELS = ['', 'Pondelok', 'Utorok', 'Streda', 'Štvrtok', 'Piatok', 'Sobota', 'Nedeľa'];

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink, Shell],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  private readonly plans = inject(TrainingPlanService);
  private readonly progressService = inject(ProgressService);
  private readonly auth = inject(AuthService);

  readonly user = this.auth.user;
  readonly weekdayLabels = WEEKDAY_LABELS;

  readonly plan = signal<TrainingPlan | null>(null);
  readonly nutrition = signal<NutritionPlan | null>(null);
  readonly logs = signal<ProgressLog[]>([]);
  readonly loading = signal(true);
  readonly errorMessage = signal('');

  readonly currentWeek = signal(1);

  readonly weeks = computed(() => {
    const plan = this.plan();
    return plan ? Array.from({ length: plan.durationWeeks }, (_, i) => i + 1) : [];
  });

  /** Workouts for the selected week, so the dashboard stays scannable. */
  readonly currentWeekWorkouts = computed(() => {
    const plan = this.plan();
    if (!plan) return [];
    return plan.workouts.filter((workout) => workout.weekNumber === this.currentWeek());
  });

  /** The backend returns logs newest first, which is what the card wants. */
  readonly latestLog = computed(() => this.logs()[0] ?? null);

  ngOnInit(): void {
    this.loading.set(true);

    // GET /active answers 404 until the user generates something, which is an
    // expected state here rather than an error worth surfacing.
    this.plans.getActive().subscribe({
      next: (plan) => this.plan.set(plan),
      error: (err) => {
        if (!isNotFound(err)) {
          this.errorMessage.set(apiErrorMessage(err, 'Tréningový plan sa nepodarilo načítať.'));
        }
      },
    });

    this.plans.getActiveNutrition().subscribe({
      next: (plan) => this.nutrition.set(plan),
      error: (err) => {
        if (!isNotFound(err)) {
          this.errorMessage.set(apiErrorMessage(err, 'Výživový plan sa nepodarilo načítať.'));
        }
      },
    });

    this.progressService.getLogs().subscribe({
      next: (logs) => this.logs.set(logs),
      error: (err) => {
        // A 401 means the interceptor already redirected to the login form.
        if (err.status !== 401 && !isNotFound(err)) {
          this.errorMessage.set(apiErrorMessage(err, 'Progress sa nepodarilo načítať.'));
        }
      },
      complete: () => this.loading.set(false),
    });
  }

  selectWeek(week: number): void {
    this.currentWeek.set(week);
  }
}