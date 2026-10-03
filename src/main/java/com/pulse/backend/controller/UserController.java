package com.pulse.backend.controller;

import com.pulse.backend.dto.user.OnboardingRequest;
import com.pulse.backend.dto.user.UserResponse;
import com.pulse.backend.security.UserPrincipal;
import com.pulse.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(userService.getProfile(principal.getUser()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateOnboarding(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody OnboardingRequest request) {
        return ResponseEntity.ok(userService.updateOnboarding(principal.getUser(), request));
    }
}