package com.pulse.backend.repository;

import com.pulse.backend.entity.TrainingPlan;
import com.pulse.backend.entity.User;
import com.pulse.backend.entity.Workout;
import com.pulse.backend.entity.WorkoutCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutCompletionRepository extends JpaRepository<WorkoutCompletion, Long> {

    List<WorkoutCompletion> findByUserOrderByCompletedAtDesc(User user);

    Optional<WorkoutCompletion> findFirstByUserAndWorkoutOrderByCompletedAtDesc(User user, Workout workout);

    List<WorkoutCompletion> findByUserAndWorkoutTrainingPlanOrderByCompletedAtDesc(User user, TrainingPlan trainingPlan);
}
