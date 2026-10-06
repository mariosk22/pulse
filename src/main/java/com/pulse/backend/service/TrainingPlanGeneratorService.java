package com.pulse.backend.service;

import com.pulse.backend.entity.Exercise;
import com.pulse.backend.entity.TrainingPlan;
import com.pulse.backend.entity.User;
import com.pulse.backend.entity.Workout;
import com.pulse.backend.entity.WorkoutExercise;
import com.pulse.backend.entity.enums.ExerciseType;
import com.pulse.backend.entity.enums.Goal;
import com.pulse.backend.entity.enums.Level;
import com.pulse.backend.entity.enums.PlanStatus;
import com.pulse.backend.exception.ApiException;
import com.pulse.backend.repository.ExerciseRepository;
import com.pulse.backend.repository.TrainingPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingPlanGeneratorService {

    private static final int MAX_EXERCISES_PER_WORKOUT = 5;

    private final ExerciseRepository exerciseRepository;
    private final TrainingPlanRepository trainingPlanRepository;

    @Transactional
    public TrainingPlan generate(User user, int durationWeeks) {
        if (user.getSport() == null || user.getLevel() == null || user.getGoal() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Finish onboarding first (sport, level and goal are required)");
        }

        List<Exercise> pool = exerciseRepository.findAvailable(
                user.getSport().getId(),
                primaryExerciseTypeFor(user.getGoal()),
                allowedLevelsUpTo(user.getLevel()));
        if (pool.isEmpty()) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "The exercise catalog has no exercises for this sport and level yet");
        }

        trainingPlanRepository.findByUserAndStatus(user, PlanStatus.ACTIVE)
                .forEach(oldPlan -> oldPlan.setStatus(PlanStatus.ARCHIVED));

        TrainingPlan plan = TrainingPlan.builder()
                .user(user)
                .sport(user.getSport())
                .level(user.getLevel())
                .goal(user.getGoal())
                .startDate(LocalDate.now())
                .durationWeeks(durationWeeks)
                .build();

        List<Integer> trainingDays = trainingDaysFor(user.getLevel());
        int workoutIndex = 0;
        for (int week = 1; week <= durationWeeks; week++) {
            for (int dayOfWeek : trainingDays) {
                Workout workout = buildWorkout(user.getGoal(), pool, week, dayOfWeek, workoutIndex++);
                workout.setTrainingPlan(plan);
                plan.getWorkouts().add(workout);
            }
        }

        return trainingPlanRepository.save(plan);
    }

    private Workout buildWorkout(Goal goal, List<Exercise> pool, int week, int dayOfWeek, int workoutIndex) {
        Workout workout = Workout.builder()
                .weekNumber(week)
                .dayOfWeek(dayOfWeek)
                .name(workoutNameFor(goal))
                .build();

        int count = Math.min(MAX_EXERCISES_PER_WORKOUT, pool.size());
        int start = (workoutIndex * count) % pool.size();
        for (int i = 0; i < count; i++) {
            Exercise exercise = pool.get((start + i) % pool.size());
            workout.getWorkoutExercises().add(WorkoutExercise.builder()
                    .workout(workout)
                    .exercise(exercise)
                    .orderIndex(i + 1)
                    .sets(setsFor(goal))
                    .reps(repsFor(goal))
                    .restSeconds(restSecondsFor(goal))
                    .build());
        }
        return workout;
    }

    private List<Integer> trainingDaysFor(Level level) {
        return switch (level) {
            case BEGINNER -> List.of(1, 3, 5);
            case INTERMEDIATE -> List.of(1, 2, 3, 5, 6);
            case PRO -> List.of(1, 2, 3, 4, 5, 6);
        };
    }

    private ExerciseType primaryExerciseTypeFor(Goal goal) {
        return switch (goal) {
            case WEIGHT_LOSS, ENDURANCE -> ExerciseType.CARDIO;
            case MUSCLE_GAIN, STRENGTH, GENERAL_FITNESS -> ExerciseType.STRENGTH;
        };
    }

    private String workoutNameFor(Goal goal) {
        return switch (goal) {
            case WEIGHT_LOSS -> "Fat burning";
            case MUSCLE_GAIN -> "Muscle building";
            case ENDURANCE -> "Endurance training";
            case STRENGTH -> "Strength training";
            case GENERAL_FITNESS -> "General fitness";
        };
    }

    private int setsFor(Goal goal) {
        return switch (goal) {
            case STRENGTH -> 5;
            case MUSCLE_GAIN -> 4;
            case WEIGHT_LOSS, GENERAL_FITNESS, ENDURANCE -> 3;
        };
    }

    private int repsFor(Goal goal) {
        return switch (goal) {
            case STRENGTH -> 5;
            case MUSCLE_GAIN -> 10;
            case WEIGHT_LOSS -> 15;
            case GENERAL_FITNESS -> 12;
            case ENDURANCE -> 20;
        };
    }

    private int restSecondsFor(Goal goal) {
        return switch (goal) {
            case STRENGTH -> 120;
            case MUSCLE_GAIN -> 90;
            case WEIGHT_LOSS -> 45;
            case GENERAL_FITNESS -> 60;
            case ENDURANCE -> 30;
        };
    }

    private List<Level> allowedLevelsUpTo(Level level) {
        List<Level> allowed = new ArrayList<>();
        for (Level candidate : Level.values()) {
            allowed.add(candidate);
            if (candidate == level) {
                break;
            }
        }
        return allowed;
    }
}
