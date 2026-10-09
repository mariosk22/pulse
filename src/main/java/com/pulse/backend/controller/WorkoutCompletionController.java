package com.pulse.backend.controller;

import com.pulse.backend.dto.workout.WorkoutCompletionRequest;
import com.pulse.backend.dto.workout.WorkoutCompletionResponse;
import com.pulse.backend.security.UserPrincipal;
import com.pulse.backend.service.WorkoutCompletionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/workout-completions")
@RequiredArgsConstructor
public class WorkoutCompletionController {

    private final WorkoutCompletionService workoutCompletionService;

    @PostMapping
    public ResponseEntity<WorkoutCompletionResponse> complete(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody WorkoutCompletionRequest request) {
        return ResponseEntity.ok(workoutCompletionService.complete(principal.getUser(), request));
    }

    @DeleteMapping("/{workoutId}")
    public ResponseEntity<Void> uncomplete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long workoutId) {
        workoutCompletionService.uncomplete(principal.getUser(), workoutId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<WorkoutCompletionResponse>> getCompletions(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(workoutCompletionService.getCompletions(principal.getUser()));
    }
}
