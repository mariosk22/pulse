import { Component, OnInit, inject, signal } from '@angular/core';
import { TrainingPlanService } from '../../core/services/training-plan.service';
import { apiErrorMessage, isNotFound } from '../../core/services/api-error';
import { NutritionPlan } from '../../core/models/plan.model';
import { MealType } from '../../core/models/user.model';
import { Shell } from '../layout/shell/shell';

const MEAL_ORDER = ['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK'] as const;

const MEAL_LABELS: Record<MealType, string> = {
  BREAKFAST: 'RAŇAJKY',
  LUNCH: 'OBED',
  DINNER: 'VEČERA',
  SNACK: 'OLOVRANT',
};

@Component({
  selector: 'app-nutrition',
  imports: [Shell],
  templateUrl: './nutrition.html',
  styleUrl: './nutrition.css',
})
export class Nutrition implements OnInit {
  private readonly plans = inject(TrainingPlanService);

  readonly mealLabels = MEAL_LABELS;

  readonly plan = signal<NutritionPlan | null>(null);
  readonly loading = signal(true);
  readonly generating = signal(false);
  readonly errorMessage = signal('');

  ngOnInit(): void {
    this.plans.getActiveNutrition().subscribe({
      next: (plan) => this.plan.set(plan),
      error: (err) => {
        if (!isNotFound(err)) {
          this.errorMessage.set(apiErrorMessage(err, 'Výživový plan sa nepodarilo načítať.'));
        }
        this.loading.set(false);
      },
      complete: () => this.loading.set(false),
    });
  }

  onGenerate(): void {
    if (this.generating()) return;

    this.generating.set(true);
    this.errorMessage.set('');

    this.plans.generateNutrition().subscribe({
      next: (plan) => {
        this.plan.set(plan);
        this.generating.set(false);
      },
      error: (err) => {
        this.generating.set(false);
        // 400 when the profile is missing height, weight, age or gender.
        this.errorMessage.set(apiErrorMessage(err, 'Výživový plan sa nepodarilo vygenerovať.'));
      },
    });
  }

  /** The backend appends meals in order, but sort so the UI cannot drift. */
  get orderedMeals() {
    const plan = this.plan();
    if (!plan) return [];
    return [...plan.meals].sort(
      (a, b) => MEAL_ORDER.indexOf(a.mealType) - MEAL_ORDER.indexOf(b.mealType),
    );
  }

  /** Share of the daily calories that each meal accounts for. */
  caloriesShare(calories: number): number {
    const total = this.plan()?.dailyCalories ?? 0;
    return total ? Math.round((calories / total) * 100) : 0;
  }

  /** Bar width for a macro, given its calories and the daily total. */
  macroShare(kcal: number): number {
    return this.caloriesShare(kcal);
  }
}