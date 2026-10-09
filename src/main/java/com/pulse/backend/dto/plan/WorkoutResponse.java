package com.pulse.backend.dto.plan;
import com.pulse.backend.entity.Workout;
import com.pulse.backend.entity.WorkoutCompletion;
import lombok.Getter;


import java.time.Instant;
import java.util.List;

@Getter
public class WorkoutResponse{
    private final Long id;
    private final Integer weekNumber;
    private final Integer dayOfWeek;
    private final String name;
    private final boolean completed;
    private final Instant completedAt;
    private final List<WorkoutExerciseResponse>exercises;

    public WorkoutResponse(Workout workout){
        this(workout, null);
    }

    public WorkoutResponse(Workout workout, WorkoutCompletion completion){
        this.id = workout.getId();
        this.weekNumber = workout.getWeekNumber();
        this.dayOfWeek =workout.getDayOfWeek();
        this.name = workout.getName();
        this.completed = completion != null;
        this.completedAt = completion != null ? completion.getCompletedAt() : null;
        this.exercises = workout.getWorkoutExercises().stream()
                .map(WorkoutExerciseResponse::new)
                .toList();


    }
}
