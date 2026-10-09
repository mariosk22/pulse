package com.pulse.backend.dto.workout;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkoutCompletionRequest {

    @NotNull(message = "is required")
    private Long workoutId;

    @Min(value = 1, message = "must be at least 1 minute")
    @Max(value = 1440, message = "must be at most 1440 minutes")
    private Integer durationMinutes;

    @Size(max = 500, message = "must be at most 500 characters")
    private String notes;
}
