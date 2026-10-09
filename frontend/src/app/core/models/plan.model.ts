import { MealType, PlanStatus } from './user.model';

/** Mirrors dto/plan/WorkoutExerciseResponse.java */
export interface WorkoutExercise {
  exerciseId: number;
  exerciseName: string;
  orderIndex: number;
  sets: number | null;
  reps: number | null;
  durationSeconds: number | null;
  restSeconds: number | null;
}

/** Mirrors dto/plan/WorkoutResponse.java */
export interface Workout {
  id: number;
  weekNumber: number;
  dayOfWeek: number;
  name: string;
  /** True when the user has marked this workout as completed. */
  completed: boolean;
  /** ISO-8601 instant of the completion, null while not completed. */
  completedAt: string | null;
  exercises: WorkoutExercise[];
}

/** Mirrors dto/plan/TrainingPlanResponse.java */
export interface TrainingPlan {
  id: number;
  sportName: string;
  level: string;
  goal: string;
  startDate: string;
  durationWeeks: number;
  status: PlanStatus | string;
  workouts: Workout[];
}

/**
 * Mirrors dto/plan/TrainingPlanSummaryResponse.java, the lighter row returned
 * by GET /api/training-plans (history). It carries the plan metadata without the
 * nested workouts. Only the fields the backend is expected to expose are typed;
 * any extra properties are ignored by the UI.
 */
export interface TrainingPlanSummary {
  id: number;
  sportName: string;
  level: string;
  goal: string;
  startDate: string;
  durationWeeks: number;
  status: PlanStatus | string;
}

/** Mirrors dto/plan/GeneratePlanRequest.java */
export interface GeneratePlanRequest {
  durationWeeks?: number;
}

/** Mirrors dto/workout/WorkoutCompletionRequest.java */
export interface WorkoutCompletionRequest {
  workoutId: number;
  durationMinutes?: number | null;
  notes?: string | null;
}

/** Mirrors dto/workout/WorkoutCompletionResponse.java */
export interface WorkoutCompletion {
  id: number;
  workoutId: number;
  workoutName: string;
  weekNumber: number;
  dayOfWeek: number;
  completedAt: string;
  durationMinutes: number | null;
  notes: string | null;
}

/** Mirrors dto/nutrition/MealResponse.java */
export interface Meal {
  mealType: MealType;
  name: string;
  calories: number;
  description: string | null;
}

/** Mirrors dto/nutrition/NutritionPlanResponse.java */
export interface NutritionPlan {
  id: number;
  dailyCalories: number;
  proteinG: number;
  carbsG: number;
  fatG: number;
  meals: Meal[];
}

/** Mirrors dto/progress/ProgressLogRequest.java */
export interface ProgressLogRequest {
  logDate?: string;
  weightKg?: number;
  bodyFatPct?: number;
  notes?: string;
}

/** Mirrors dto/progress/ProgressLogResponse.java */
export interface ProgressLog {
  id: number;
  logDate: string;
  weightKg: number | null;
  bodyFatPct: number | null;
  notes: string | null;
}
