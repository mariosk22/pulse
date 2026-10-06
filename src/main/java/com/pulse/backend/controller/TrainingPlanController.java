package com.pulse.backend.controller;

import com.pulse.backend.dto.plan.GeneratePlanRequest;
import com.pulse.backend.dto.plan.TrainingPlanResponse;
import com.pulse.backend.dto.plan.TrainingPlanSummaryResponse;
import com.pulse.backend.security.UserPrincipal;
import com.pulse.backend.service.TrainingPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/training-plans")
@RequiredArgsConstructor
public class TrainingPlanController {

    private static final int DEFAULT_DURATION_WEEKS = 4;

    private final TrainingPlanService trainingPlanService;

    @PostMapping("/generate")
    public ResponseEntity<TrainingPlanResponse> generate(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody(required = false) GeneratePlanRequest request) {
        int weeks = (request != null && request.getDurationWeeks() != null)
                ? request.getDurationWeeks()
                : DEFAULT_DURATION_WEEKS;
        return ResponseEntity.ok(trainingPlanService.generate(principal.getUser(), weeks));
    }

    @GetMapping("/active")
    public ResponseEntity<TrainingPlanResponse> getActive(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(trainingPlanService.getActive(principal.getUser()));
    }

    @GetMapping
    public ResponseEntity<List<TrainingPlanSummaryResponse>> getHistory(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(trainingPlanService.getHistory(principal.getUser()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingPlanResponse> getById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        return ResponseEntity.ok(trainingPlanService.getById(principal.getUser(), id));
    }
}
