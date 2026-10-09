package com.pulse.backend.service;


import com.pulse.backend.entity.*;
import com.pulse.backend.entity.enums.ExerciseType;
import com.pulse.backend.entity.enums.Goal;
import com.pulse.backend.entity.enums.Level;
import com.pulse.backend.exception.ApiException;
import com.pulse.backend.repository.ExerciseRepository;
import com.pulse.backend.repository.TrainingPlanRepository;
import com.pulse.backend.repository.UserRepository;
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

    private final ExerciseRepository exerciseRepository;
    private final TrainingPlanRepository trainingPlanRepository;
    private final UserRepository userRepository;

    @Transactional
    public TrainingPlan generate(User principalUser, int durationWeeks) {
        // principal.getUser() is detached (loaded in JwtAuthenticationFilter outside
        // the open-in-view session), so user.getSport().getId() would throw a
        // LazyInitializationException. Reload the user together with the sport.
        User user = userRepository.findWithSportById(principalUser.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User does not exist"));
        if (user.getSport() == null || user.getLevel() == null || user.getGoal() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Complete onboarding first (sport, level, goal)");
        }

        TrainingPlan plan = TrainingPlan.builder()
                .user(user)
                .sport(user.getSport())
                .level(user.getLevel())
                .goal(user.getGoal())
                .startDate(LocalDate.now())
                .durationWeeks(durationWeeks)
                .build();

        List<Integer> trainingDays = trainingDaysFor(user.getLevel());

        for (int week = 1; week <= durationWeeks; week++) {
            for (int dayOfWeek : trainingDays) {
                Workout workout = buildWorkout(user, week, dayOfWeek);
                workout.setTrainingPlan(plan);
                plan.getWorkouts().add(workout);
            }
        }

        return trainingPlanRepository.save(plan);
    }

    private List<Integer> trainingDaysFor(Level level) {
        return switch (level) {
            case BEGINNER -> List.of(1, 3, 5);
            case INTERMEDIATE -> List.of(1, 2, 3, 5, 6);
            case PRO -> List.of(1, 2, 3, 4, 5, 6);
        };
    }

    private Workout buildWorkout(User user, int week, int dayOfWeek) {
        ExerciseType primaryType = primaryExerciseTypeFor(user.getGoal());
        List<Level> allowedLevels = allowedLevelsUpTo(user.getLevel());

        List<Exercise> available = exerciseRepository.findAvailable(
                user.getSport().getId(), primaryType, allowedLevels);

        if (available.isEmpty()) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "There are not enough exercises in the catalog for this sport/level yet");
        }

        Workout workout = Workout.builder()
                .weekNumber(week)
                .dayOfWeek(dayOfWeek)
                .name(workoutNameFor(user.getGoal()))
                .build();

        int exercisesPerWorkout = Math.min(5, available.size());
        for (int i = 0; i < exercisesPerWorkout; i++) {
            Exercise exercise = available.get(i);

            WorkoutExercise we = WorkoutExercise.builder()
                    .workout(workout)
                    .exercise(exercise)
                    .orderIndex(i + 1)
                    .sets(setsFor(user.getGoal()))
                    .reps(repsFor(user.getGoal()))
                    .restSeconds(restSecondsFor(user.getGoal()))
                    .build();

            workout.getWorkoutExercises().add(we);
        }

        return workout;
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
            case MUSCLE_GAIN -> "Muscle gain";
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
        List<Level> all = List.of(Level.BEGINNER, Level.INTERMEDIATE, Level.PRO);
        List<Level> result = new ArrayList<>();
        for (Level l : all) {
            result.add(l);
            if (l == level) break;
        }
        return result;
    }
}