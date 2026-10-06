package com.pulse.backend.repository;

import com.pulse.backend.entity.User;
import com.pulse.backend.entity.WorkoutCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutCompletionRepository extends JpaRepository<WorkoutCompletion, Long> {

    List<WorkoutCompletion> findByUserOrderByCompletedAtDesc(User user);
}
