import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Observable } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { TrainingPlanService } from '../../core/services/training-plan.service';
import { apiErrorMessage, isNotFound } from '../../core/services/api-error';
import { TrainingPlan, TrainingPlanSummary, Workout } from '../../core/models/plan.model';
import { Shell } from '../layout/shell/shell';

const WEEKDAY_LABELS = [
  '',
  'Pondelok',
  'Utorok',
  'Streda',
  'Štvrtok',
  'Piatok',
  'Sobota',
  'Nedeľa',
];

@Component({
  selector: 'app-plan',
  imports: [FormsModule, Shell],
  templateUrl: './plan.html',
  styleUrl: './plan.css',
})
export class Plan implements OnInit {
  private readonly plans = inject(TrainingPlanService);
  private readonly auth = inject(AuthService);

  readonly weekdayLabels = WEEKDAY_LABELS;
  readonly user = this.auth.user;

  /** The plan the dashboard/generator treats as current. */
  readonly activePlan = signal<TrainingPlan | null>(null);
  /** What the week/workout viewer renders: the active plan or a past one. */
  readonly plan = signal<TrainingPlan | null>(null);
  /** Metadata rows from GET /api/training-plans, newest first. */
  readonly history = signal<TrainingPlanSummary[]>([]);

  readonly loading = signal(true);
  readonly loadingHistory = signal(true);
  readonly loadingDetail = signal(false);
  readonly generating = signal(false);
  /** Workout id whose completion toggle is in flight, to disable its button. */
  readonly completingId = signal<number | null>(null);
  readonly errorMessage = signal('');
  readonly currentWeek = signal(1);

  /** Id of the history row on screen; null means the active plan. */
  readonly selectedPlanId = signal<number | null>(null);

  /** Duration is editable before generating; the backend defaults to 4. */
  durationWeeks = 12;

  /** True while a plan that is not the active one is displayed. */
  readonly isViewingArchived = computed(() => {
    const selected = this.selectedPlanId();
    return selected !== null && selected !== this.activePlan()?.id;
  });

  readonly weeks = computed(() => {
    const plan = this.plan();
    return plan ? Array.from({ length: plan.durationWeeks }, (_, i) => i + 1) : [];
  });

  readonly currentWeekWorkouts = computed(() => {
    const plan = this.plan();
    if (!plan) return [];
    return plan.workouts.filter((workout) => workout.weekNumber === this.currentWeek());
  });

  /** How many of the workouts shown in the current week are completed. */
  readonly completedInWeek = computed(
    () => this.currentWeekWorkouts().filter((workout) => workout.completed).length,
  );

  ngOnInit(): void {
    this.loadActivePlan();
    this.loadHistory();
  }

  private loadActivePlan(): void {
    this.loading.set(true);
    this.plans.getActive().subscribe({
      next: (plan) => {
        this.activePlan.set(plan);
        // Do not clobber a past plan the user is already inspecting.
        if (this.selectedPlanId() === null) {
          this.plan.set(plan);
        }
      },
      error: (err) => {
        // No plan yet is a normal state, the empty view explains it.
        if (!isNotFound(err)) {
          this.errorMessage.set(apiErrorMessage(err, 'Plán sa nepodarilo načítať.'));
        }
      },
      complete: () => this.loading.set(false),
    });
  }

  private loadHistory(): void {
    this.loadingHistory.set(true);
    this.plans.getHistory().subscribe({
      next: (summaries) => this.history.set(summaries),
      error: (err) => {
        if (!isNotFound(err)) {
          this.errorMessage.set(apiErrorMessage(err, 'Históriu plánov sa nepodarilo načítať.'));
        }
      },
      complete: () => this.loadingHistory.set(false),
    });
  }

  onSelectPlan(summary: TrainingPlanSummary): void {
    if (summary.id === this.activePlan()?.id) {
      this.selectActive();
      return;
    }
    if (this.selectedPlanId() === summary.id) return;

    this.selectedPlanId.set(summary.id);
    this.loadingDetail.set(true);
    this.errorMessage.set('');

    this.plans.getById(summary.id).subscribe({
      next: (detail) => {
        this.plan.set(detail);
        this.currentWeek.set(1);
        this.loadingDetail.set(false);
      },
      error: (err) => {
        this.loadingDetail.set(false);
        this.selectedPlanId.set(null);
        this.errorMessage.set(apiErrorMessage(err, 'Plán sa nepodarilo načítať.'));
      },
    });
  }

  selectActive(): void {
    this.selectedPlanId.set(null);
    this.plan.set(this.activePlan());
    this.currentWeek.set(1);
  }

  /** Highlights the history row currently shown in the viewer. */
  isCurrent(summary: TrainingPlanSummary): boolean {
    if (this.selectedPlanId() !== null) {
      return this.selectedPlanId() === summary.id;
    }
    return summary.id === this.activePlan()?.id;
  }

  onGenerate(): void {
    if (this.generating()) return;

    this.generating.set(true);
    this.errorMessage.set('');

    this.plans.generate({ durationWeeks: this.durationWeeks }).subscribe({
      next: (plan) => {
        this.activePlan.set(plan);
        this.plan.set(plan);
        this.selectedPlanId.set(null);
        this.currentWeek.set(1);
        this.generating.set(false);
        // A new active plan archives the previous one, so refresh the list.
        this.loadHistory();
      },
      error: (err) => {
        this.generating.set(false);
        // The backend answers 409 when the exercise catalogue has nothing for
        // this sport/level combination, and 400 before onboarding is done.
        this.errorMessage.set(apiErrorMessage(err, 'Plán sa nepodarilo vygenerovať.'));
      },
    });
  }

  selectWeek(week: number): void {
    this.currentWeek.set(week);
  }

  /** Marks a workout done, or undoes it, then reflects the change in the view. */
  onToggleWorkout(workout: Workout): void {
    if (this.completingId() !== null || this.isViewingArchived()) return;

    const done = workout.completed;
    this.completingId.set(workout.id);
    this.errorMessage.set('');

    const request$: Observable<unknown> = done
      ? this.plans.uncompleteWorkout(workout.id)
      : this.plans.completeWorkout({ workoutId: workout.id });

    request$.subscribe({
      next: () => {
        this.markWorkout(workout.id, !done);
        this.completingId.set(null);
      },
      error: (err) => {
        this.completingId.set(null);
        this.errorMessage.set(apiErrorMessage(err, 'Stav treningu sa nepodarilo uložiť.'));
      },
    });
  }

  /** Replaces the workout in both the viewer and the active plan, if shown. */
  private markWorkout(workoutId: number, completed: boolean): void {
    const apply = (plan: TrainingPlan): TrainingPlan => ({
      ...plan,
      workouts: plan.workouts.map((workout) =>
        workout.id === workoutId
          ? { ...workout, completed, completedAt: completed ? new Date().toISOString() : null }
          : workout,
      ),
    });

    const current = this.plan();
    if (current) {
      const updated = apply(current);
      this.plan.set(updated);
      if (this.selectedPlanId() === null) {
        this.activePlan.set(updated);
      }
    }
  }
}
