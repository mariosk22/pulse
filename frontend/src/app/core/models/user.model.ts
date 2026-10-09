/**
 * Types mirroring the backend DTOs on the `backend` branch.
 *
 * Enum values are copied verbatim from the Java enums in
 * com.pulse.backend.entity.enums.
 */

export type Gender = 'MALE' | 'FEMALE';
export type Goal =
  | 'WEIGHT_LOSS'
  | 'MUSCLE_GAIN'
  | 'ENDURANCE'
  | 'STRENGTH'
  | 'GENERAL_FITNESS';
export type Level = 'BEGINNER' | 'INTERMEDIATE' | 'PRO';

export type Role = 'USER' | 'ADMIN';

export type PlanStatus = 'ACTIVE' | 'COMPLETED' | 'ARCHIVED';
export type ExerciseType = 'STRENGTH' | 'CARDIO' | 'MOBILITY' | 'SPORT_SPECIFIC';
export type MealType = 'BREAKFAST' | 'LUNCH' | 'DINNER' | 'SNACK';

/** Mirrors dto/user/UserResponse.java */
export interface User {
  id: number;
  email: string;
  fullName: string;
  gender: Gender | null;
  age: number | null;
  heightCm: number | null;
  weightKg: number | null;
  sportName: string | null;
  level: Level | null;
  goal: Goal | null;
}

/** Mirrors dto/auth/AuthResponse.java */
export interface AuthResponse {
  token: string;
  userId: number;
  email: string;
  fullName: string;
}

/** Mirrors dto/auth/LoginRequest.java */
export interface LoginRequest {
  email: string;
  password: string;
}

/** Mirrors dto/auth/RegisterRequest.java */
export interface RegisterRequest {
  email: string;
  password: string;
  fullName: string;
}

/** Mirrors dto/user/OnboardingRequest.java */
export interface OnboardingRequest {
  sportId: number;
  level: Level;
  goal: Goal;
  gender?: Gender;
  age?: number;
  heightCm?: number;
  weightKg?: number;
}

/** Mirrors entity/Sport.java, returned by GET /api/sports */
export interface Sport {
  id: number;
  name: string;
  description: string | null;
}

/** Body of the GlobalExceptionHandler error responses. */
export interface ApiError {
  timestamp?: string;
  status?: number;
  error?: string;
  message?: string;
}