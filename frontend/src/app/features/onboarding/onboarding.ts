import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { apiErrorMessage } from '../../core/services/api-error';
import { Gender, Goal, Level, Sport } from '../../core/models/user.model';

/** Mirrors com.pulse.backend.entity.enums.Goal, with English copy for the UI. */
const GOALS: { value: Goal; label: string; hint: string }[] = [
  { value: 'WEIGHT_LOSS', label: 'Weight loss', hint: 'Cardio and a mild calorie deficit' },
  { value: 'MUSCLE_GAIN', label: 'Muscle gain', hint: 'Strength training and extra protein' },
  { value: 'ENDURANCE', label: 'Endurance', hint: 'Longer and more frequent workouts' },
  { value: 'STRENGTH', label: 'Strength', hint: 'Heavy load, fewer reps' },
  { value: 'GENERAL_FITNESS', label: 'General fitness', hint: 'Balanced training for everything' },
];

const LEVELS: { value: Level; label: string; hint: string }[] = [
  { value: 'BEGINNER', label: 'Beginner', hint: '3 workouts per week' },
  { value: 'INTERMEDIATE', label: 'Intermediate', hint: '5 workouts per week' },
  { value: 'PRO', label: 'Pro', hint: '6 workouts per week' },
];

@Component({
  selector: 'app-onboarding',
  imports: [ReactiveFormsModule],
  templateUrl: './onboarding.html',
  styleUrl: './onboarding.css',
})
export class Onboarding implements OnInit {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private authService = inject(AuthService);
  private router = inject(Router);

  readonly goals = GOALS;
  readonly levels = LEVELS;

  readonly sports = signal<Sport[]>([]);
  readonly loadingSports = signal(true);
  readonly sportsError = signal('');
  readonly errorMessage = signal('');
  readonly submitting = signal(false);

  readonly form = this.fb.nonNullable.group({
    sportId: [0, [Validators.required, Validators.min(1)]],
    level: ['BEGINNER' as Level, Validators.required],
    goal: ['GENERAL_FITNESS' as Goal, Validators.required],
    gender: this.fb.nonNullable.control<Gender | ''>(''),
    age: [25, [Validators.min(10), Validators.max(100)]],
    heightCm: [175, [Validators.min(100), Validators.max(250)]],
    weightKg: [70, [Validators.min(30), Validators.max(300)]],
  });

  ngOnInit(): void {
    this.userService.getSports().subscribe({
      next: (sports) => {
        this.sports.set(sports);
        this.loadingSports.set(false);
        // Preselect when the list is short enough to be unambiguous.
        if (sports.length === 1) this.form.controls.sportId.setValue(sports[0].id);
      },
      error: (err) => {
        this.sportsError.set(
          apiErrorMessage(err, 'Could not load sports. Check that the backend is running.'),
        );
        this.loadingSports.set(false);
      },
    });
  }

  selectGoal(goal: Goal): void {
    this.form.controls.goal.setValue(goal);
  }

  selectLevel(level: Level): void {
    this.form.controls.level.setValue(level);
  }

  selectSport(id: number): void {
    this.form.controls.sportId.setValue(id);
  }

  OnSubmit(): void {
    if (this.form.invalid || this.submitting()) {
      this.form.markAllAsTouched();
      return;
    }

    const { sportId, level, goal, gender, age, heightCm, weightKg } = this.form.getRawValue();

    this.submitting.set(true);
    this.errorMessage.set('');

    this.userService
      .updateOnboarding({
        sportId,
        level,
        goal,
        gender: gender === '' ? undefined : gender,
        age,
        heightCm,
        weightKg,
      })
      .subscribe({
        next: (user) => {
          // Cache the fresh profile so onboardedGuard lets the user through.
          this.authService.setUser(user);
          this.submitting.set(false);
          void this.router.navigateByUrl('/dashboard');
        },
        error: (err) => {
          this.submitting.set(false);
          this.errorMessage.set(
            apiErrorMessage(err, 'Could not save onboarding. Please try again.'),
          );
        },
      });
  }
}
