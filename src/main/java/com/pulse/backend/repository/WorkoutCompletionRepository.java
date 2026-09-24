package com.pulse.backend.repository;

import com.pulse.backend.entity.WorkoutCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutCompletionRepository extends JpaRepository<WorkoutCompletion,Long> {
}
