package com.pulse.backend.service;

import com.pulse.backend.dto.workout.WorkoutCompletionRequest;
import com.pulse.backend.dto.workout.WorkoutCompletionResponse;
import com.pulse.backend.entity.User;
import com.pulse.backend.entity.Workout;
import com.pulse.backend.entity.WorkoutCompletion;
import com.pulse.backend.exception.ApiException;
import com.pulse.backend.repository.WorkoutCompletionRepository;
import com.pulse.backend.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Tracks which workouts of a user's plans have been completed. Marking a workout
 * twice updates the existing row instead of stacking duplicates, and the whole
 * response mapping happens inside the transaction so the lazy workout
 * association is always available.
 */
@Service
@RequiredArgsConstructor
public class WorkoutCompletionService {

    private final WorkoutRepository workoutRepository;
    private final WorkoutCompletionRepository workoutCompletionRepository;

    @Transactional
    public WorkoutCompletionResponse complete(User user, WorkoutCompletionRequest request) {
        Workout workout = workoutRepository.findByIdAndTrainingPlanUser(request.getWorkoutId(), user)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Workout not found"));

        WorkoutCompletion completion = workoutCompletionRepository
                .findFirstByUserAndWorkoutOrderByCompletedAtDesc(user, workout)
                .orElseGet(() -> WorkoutCompletion.builder()
                        .user(user)
                        .workout(workout)
                        .build());

        completion.setCompletedAt(Instant.now());
        completion.setDurationMinutes(request.getDurationMinutes());
        completion.setNotes(request.getNotes());

        return new WorkoutCompletionResponse(workoutCompletionRepository.save(completion));
    }

    @Transactional
    public void uncomplete(User user, Long workoutId) {
        Workout workout = workoutRepository.findByIdAndTrainingPlanUser(workoutId, user)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Workout not found"));

        workoutCompletionRepository.findFirstByUserAndWorkoutOrderByCompletedAtDesc(user, workout)
                .ifPresent(workoutCompletionRepository::delete);
    }

    @Transactional(readOnly = true)
    public List<WorkoutCompletionResponse> getCompletions(User user) {
        return workoutCompletionRepository.findByUserOrderByCompletedAtDesc(user).stream()
                .map(WorkoutCompletionResponse::new)
                .toList();
    }
}
