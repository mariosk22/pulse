package com.pulse.backend.dto.plan;
import com.pulse.backend.entity.Workout;
import com.pulse.backend.entity.WorkoutExercise;
import lombok.Getter;


import java.util.List;

@Getter
public class WorkoutResponse{
    private final Long id;
    private final Integer weekNumber;
    private final Integer dayOfWeek;
    private final String name;
    private final List<WorkoutExerciseResponse>exercises;

    public WorkoutResponse(Workout workout){
        this.id = workout.getId();
        this.weekNumber = workout.getWeekNumber();
        this.dayOfWeek =workout.getDayOfWeek();
        this.name = workout.getName();
        this.exercises = workout.getWorkoutExercises().stream()
                .map(WorkoutExerciseResponse::new)
                .toList();


    }
}
