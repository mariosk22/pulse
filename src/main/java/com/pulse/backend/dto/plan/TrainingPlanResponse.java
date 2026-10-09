package com.pulse.backend.dto.plan;
import com.pulse.backend.entity.TrainingPlan;
import com.pulse.backend.entity.WorkoutCompletion;
import lombok.Getter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
@Getter
public class TrainingPlanResponse {
    private final Long id;
    private final String sportName;
    private final String level;
    private final String goal;
    private final LocalDate startDate;
    private final Integer durationWeeks;
    private final String status;
    private final List<WorkoutResponse> workouts;

    public TrainingPlanResponse(TrainingPlan plan){
        this(plan, Map.of());
    }

    public TrainingPlanResponse(TrainingPlan plan, Map<Long, WorkoutCompletion> completionsByWorkout){
        this.id = plan.getId();
        this.sportName = plan.getSport().getName();
        this.level=plan.getLevel().name();
        this.goal=plan.getGoal().name();
        this.startDate=plan.getStartDate();
        this.durationWeeks=plan.getDurationWeeks();
        this.status = plan.getStatus().name();
        this.workouts=plan.getWorkouts().stream()
                .map(workout -> new WorkoutResponse(workout, completionsByWorkout.get(workout.getId())))
                .toList();
    }
}
