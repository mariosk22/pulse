package com.pulse.backend.repository;
import com.pulse.backend.entity.NutritionPlan;
import com.pulse.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NutritionPlanRepository extends JpaRepository<NutritionPlan,Long> {
    Optional<NutritionPlan>findFirstByUserOrderByIdDesc(User user);
}
