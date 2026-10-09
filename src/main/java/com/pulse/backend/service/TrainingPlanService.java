package com.pulse.backend.service;

import com.pulse.backend.dto.plan.TrainingPlanResponse;
import com.pulse.backend.dto.plan.TrainingPlanSummaryResponse;
import com.pulse.backend.entity.TrainingPlan;
import com.pulse.backend.entity.User;
import com.pulse.backend.entity.WorkoutCompletion;
import com.pulse.backend.entity.enums.PlanStatus;
import com.pulse.backend.exception.ApiException;
import com.pulse.backend.repository.TrainingPlanRepository;
import com.pulse.backend.repository.WorkoutCompletionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Read/write operations around a user's training plans. Generation itself is
 * delegated to {@link TrainingPlanGeneratorService}; this service owns lookups
 * (active plan, history and one plan by id) and the DTO mapping.
 */
@Service
@RequiredArgsConstructor
public class TrainingPlanService {

    private final TrainingPlanGeneratorService generatorService;
    private final TrainingPlanRepository trainingPlanRepository;
    private final WorkoutCompletionRepository workoutCompletionRepository;

    @Transactional
    public TrainingPlanResponse generate(User user, int durationWeeks) {
        TrainingPlan plan = generatorService.generate(user, durationWeeks);
        return toResponse(plan, user);
    }

    @Transactional(readOnly = true)
    public TrainingPlanResponse getActive(User user) {
        TrainingPlan plan = trainingPlanRepository
                .findFirstByUserAndStatusOrderByStartDateDescIdDesc(user, PlanStatus.ACTIVE)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "No active training plan yet"));
        return toResponse(plan, user);
    }

    @Transactional(readOnly = true)
    public List<TrainingPlanSummaryResponse> getHistory(User user) {
        return trainingPlanRepository.findByUserOrderByStartDateDescIdDesc(user).stream()
                .map(TrainingPlanSummaryResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public TrainingPlanResponse getById(User user, Long id) {
        TrainingPlan plan = trainingPlanRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Training plan not found"));
        return toResponse(plan, user);
    }

    /** Builds the plan DTO and merges in which of its workouts are completed. */
    private TrainingPlanResponse toResponse(TrainingPlan plan, User user) {
        Map<Long, WorkoutCompletion> completionsByWorkout = workoutCompletionRepository
                .findByUserAndWorkoutTrainingPlanOrderByCompletedAtDesc(user, plan).stream()
                .collect(Collectors.toMap(
                        completion -> completion.getWorkout().getId(),
                        Function.identity(),
                        (first, second) -> first));
        return new TrainingPlanResponse(plan, completionsByWorkout);
    }
}
