/**
 * Types mirroring the backend DTOs on the `backend` branch.
 *
 * Enum values marked CONFIRMED are copied verbatim from Java enums that are
 * already written. Values marked INFERRED are placeholders: the matching Java
 * enums (Role, Gender, Goal, Level) are currently empty classes, so these
 * need to be reconciled with the backend before the API can return them.
 */

export type Gender = 'MALE' | 'FEMALE' | 'OTHER'; // INFERRED
export type Goal =
  | 'FAT_LOSS'
  | 'MUSCLE_GAIN'
  | 'ENDURANCE'
  | 'GENERAL_FITNESS'; // INFERRED
export type Level = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED'; // INFERRED

export type Role = 'USER' | 'ADMIN'; // INFERRED

// CONFIRMED from com.pulse.backend.entity.enums
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

/**
 * Mirrors the body produced by exception/GlobalExceptionHandler.java.
 * Message-only field, so surfaces backend validation text to the user.
 */
export interface ApiError {
  timestamp?: string;
  status?: number;
  error?: string;
  message?: string;
}
