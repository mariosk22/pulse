package com.pulse.backend.controller;

import com.pulse.backend.dto.plan.GeneratePlanRequest;
import com.pulse.backend.dto.plan.TrainingPlanResponse;
import com.pulse.backend.entity.TrainingPlan;
import com.pulse.backend.entity.enums.PlanStatus;
import com.pulse.backend.exception.ApiException;
import com.pulse.backend.repository.TrainingPlanRepository;
import com.pulse.backend.security.UserPrincipal;
import com.pulse.backend.service.TrainingPlanGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/training-plans")
@RequiredArgsConstructor
public class TrainingPlanController {

    private final TrainingPlanGeneratorService generatorService;
    private final TrainingPlanRepository trainingPlanRepository;

    @PostMapping("/generate")
    public ResponseEntity<TrainingPlanResponse> generate(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody(required = false) GeneratePlanRequest request) {

        int weeks = (request != null && request.getDurationWeeks() != null) ? request.getDurationWeeks() : 4;
        TrainingPlan plan = generatorService.generate(principal.getUser(), weeks);
        return ResponseEntity.ok(new TrainingPlanResponse(plan));
    }

    @GetMapping("/active")
    public ResponseEntity<TrainingPlanResponse> getActive(@AuthenticationPrincipal UserPrincipal principal) {
        TrainingPlan plan = trainingPlanRepository
                .findFirstByUserAndStatusOrderByStartDateDesc(principal.getUser(), PlanStatus.ACTIVE)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Nemas zatial ziadny aktivny plan"));
        return ResponseEntity.ok(new TrainingPlanResponse(plan));
    }
}