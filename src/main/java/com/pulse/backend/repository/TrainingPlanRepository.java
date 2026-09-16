package com.pulse.backend.repository;

import com.pulse.backend.entity.TrainingPlan;
import com.pulse.backend.entity.User;
import com.pulse.backend.entity.enums.PlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainingPlanRepository  extends JpaRepository<TrainingPlan,Long>{
        Optional<TrainingPlan> findFirstByUserAndStatusOrderByStartDateDesc(User user, PlanStatus status);

    }


