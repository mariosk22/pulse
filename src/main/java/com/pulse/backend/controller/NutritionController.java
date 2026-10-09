package com.pulse.backend.controller;

import com.pulse.backend.dto.nutrition.NutritionPlanResponse;
import com.pulse.backend.entity.NutritionPlan;
import com.pulse.backend.entity.TrainingPlan;
import com.pulse.backend.entity.enums.PlanStatus;
import com.pulse.backend.exception.ApiException;
import com.pulse.backend.repository.NutritionPlanRepository;
import com.pulse.backend.repository.TrainingPlanRepository;
import com.pulse.backend.security.UserPrincipal;
import com.pulse.backend.service.NutritionCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nutrition-plans")
@RequiredArgsConstructor
public class NutritionController {

    private final NutritionCalculatorService nutritionCalculatorService;
    private final NutritionPlanRepository nutritionPlanRepository;
    private final TrainingPlanRepository trainingPlanRepository;

    @PostMapping("/generate")
    public ResponseEntity<NutritionPlanResponse> generate(@AuthenticationPrincipal UserPrincipal principal) {
        TrainingPlan activePlan = trainingPlanRepository
                .findFirstByUserAndStatusOrderByStartDateDesc(principal.getUser(), PlanStatus.ACTIVE)
                .orElse(null);

        NutritionPlan plan = nutritionCalculatorService.generateForUser(principal.getUser(), activePlan);
        return ResponseEntity.ok(new NutritionPlanResponse(plan));
    }

    @GetMapping("/active")
    public ResponseEntity<NutritionPlanResponse> getActive(@AuthenticationPrincipal UserPrincipal principal) {
        NutritionPlan plan = nutritionPlanRepository.findFirstByUserOrderByIdDesc(principal.getUser())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "You don't have a nutrition plan yet"));
        return ResponseEntity.ok(new NutritionPlanResponse(plan));
    }
}
