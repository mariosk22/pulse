package com.pulse.backend.dto.workout;

import com.pulse.backend.entity.WorkoutCompletion;
import lombok.Getter;

import java.time.Instant;

@Getter
public class WorkoutCompletionResponse {
    private final Long id;
    private final Long workoutId;
    private final String workoutName;
    private final Integer weekNumber;
    private final Integer dayOfWeek;
    private final Instant completedAt;
    private final Integer durationMinutes;
    private final String notes;

    public WorkoutCompletionResponse(WorkoutCompletion completion) {
        this.id = completion.getId();
        this.workoutId = completion.getWorkout().getId();
        this.workoutName = completion.getWorkout().getName();
        this.weekNumber = completion.getWorkout().getWeekNumber();
        this.dayOfWeek = completion.getWorkout().getDayOfWeek();
        this.completedAt = completion.getCompletedAt();
        this.durationMinutes = completion.getDurationMinutes();
        this.notes = completion.getNotes();
    }
}
