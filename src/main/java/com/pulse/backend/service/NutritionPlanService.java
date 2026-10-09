package com.pulse.backend.service;

import com.pulse.backend.dto.nutrition.NutritionPlanResponse;
import com.pulse.backend.entity.NutritionPlan;
import com.pulse.backend.entity.TrainingPlan;
import com.pulse.backend.entity.User;
import com.pulse.backend.entity.enums.PlanStatus;
import com.pulse.backend.exception.ApiException;
import com.pulse.backend.repository.NutritionPlanRepository;
import com.pulse.backend.repository.TrainingPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Generates and reads the user's nutrition plan. The actual macro maths lives in
 * {@link NutritionCalculatorService}; this service ties it to the active
 * training plan (when there is one) and maps the entity to its response DTO.
 */
@Service
@RequiredArgsConstructor
public class NutritionPlanService {

    private final NutritionCalculatorService nutritionCalculatorService;
    private final NutritionPlanRepository nutritionPlanRepository;
    private final TrainingPlanRepository trainingPlanRepository;

    @Transactional
    public NutritionPlanResponse generate(User user) {
        TrainingPlan activePlan = trainingPlanRepository
                .findFirstByUserAndStatusOrderByStartDateDescIdDesc(user, PlanStatus.ACTIVE)
                .orElse(null);
        NutritionPlan plan = nutritionCalculatorService.generateForUser(user, activePlan);
        return new NutritionPlanResponse(plan);
    }

    @Transactional(readOnly = true)
    public NutritionPlanResponse getActive(User user) {
        NutritionPlan plan = nutritionPlanRepository.findFirstByUserOrderByIdDesc(user)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "No nutrition plan yet"));
        return new NutritionPlanResponse(plan);
    }
}
