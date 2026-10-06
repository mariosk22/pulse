package com.pulse.backend.repository;

import com.pulse.backend.entity.User;
import com.pulse.backend.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    Optional<Workout> findByIdAndTrainingPlanUser(Long id, User user);
}
