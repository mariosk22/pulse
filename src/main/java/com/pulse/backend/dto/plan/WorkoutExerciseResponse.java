package com.pulse.backend.dto.plan;
import com.pulse.backend.entity.WorkoutExercise;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
@Getter
public class WorkoutExerciseResponse {
    private final Long exerciseId;
    private  final  String exerciseName;
    private final Integer orderIndex;
    private final Integer sets;
    private final Integer reps;
    private final Integer durationSeconds;
    private  final Integer restSeconds;

    public WorkoutExerciseResponse(WorkoutExercise we){
        this.exerciseId = we.getExercise().getId();
        this.exerciseName = we.getExercise().getName();
        this.orderIndex = we.getOrderIndex();
        this.sets= we.getSets();
        this.reps=we.getReps();
        this.durationSeconds=we.getDurationSeconds();
        this.restSeconds = we.getRestSeconds();
    }
}
