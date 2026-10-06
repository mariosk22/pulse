package com.pulse.backend.controller;

import com.pulse.backend.dto.nutrition.NutritionPlanResponse;
import com.pulse.backend.security.UserPrincipal;
import com.pulse.backend.service.NutritionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/nutrition-plans")
@RequiredArgsConstructor
public class NutritionController {

    private final NutritionPlanService nutritionPlanService;

    @PostMapping("/generate")
    public ResponseEntity<NutritionPlanResponse> generate(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(nutritionPlanService.generate(principal.getUser()));
    }

    @GetMapping("/active")
    public ResponseEntity<NutritionPlanResponse> getActive(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(nutritionPlanService.getActive(principal.getUser()));
    }
}
