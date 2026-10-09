import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  GeneratePlanRequest,
  NutritionPlan,
  TrainingPlan,
  TrainingPlanSummary,
  WorkoutCompletion,
  WorkoutCompletionRequest,
} from '../models/plan.model';

/** Endpoints for the generated training plan and the matching nutrition plan. */
@Injectable({ providedIn: 'root' })
export class TrainingPlanService {
  private readonly http = inject(HttpClient);

  /** POST /api/training-plans/generate */
  generate(request: GeneratePlanRequest = {}): Observable<TrainingPlan> {
    return this.http.post<TrainingPlan>('/api/training-plans/generate', request);
  }

  /** GET /api/training-plans/active - 404 when the user has no plan yet. */
  getActive(): Observable<TrainingPlan> {
    return this.http.get<TrainingPlan>('/api/training-plans/active');
  }

  /** GET /api/training-plans - every plan of the user, newest first. */
  getHistory(): Observable<TrainingPlanSummary[]> {
    return this.http.get<TrainingPlanSummary[]>('/api/training-plans');
  }

  /** GET /api/training-plans/{id} - full detail of one (e.g. archived) plan. */
  getById(id: number): Observable<TrainingPlan> {
    return this.http.get<TrainingPlan>(`/api/training-plans/${id}`);
  }

  /** GET /api/workout-completions - the user's completed workouts, newest first. */
  getCompletions(): Observable<WorkoutCompletion[]> {
    return this.http.get<WorkoutCompletion[]>('/api/workout-completions');
  }

  /** POST /api/workout-completions - marks a workout done (idempotent). */
  completeWorkout(request: WorkoutCompletionRequest): Observable<WorkoutCompletion> {
    return this.http.post<WorkoutCompletion>('/api/workout-completions', request);
  }

  /** DELETE /api/workout-completions/{workoutId} - undoes a completion. */
  uncompleteWorkout(workoutId: number): Observable<void> {
    return this.http.delete<void>(`/api/workout-completions/${workoutId}`);
  }

  /** POST /api/nutrition-plans/generate - derives macros from the profile. */
  generateNutrition(): Observable<NutritionPlan> {
    return this.http.post<NutritionPlan>('/api/nutrition-plans/generate', {});
  }

  /** GET /api/nutrition-plans/active - 404 when nothing was generated yet. */
  getActiveNutrition(): Observable<NutritionPlan> {
    return this.http.get<NutritionPlan>('/api/nutrition-plans/active');
  }
}
