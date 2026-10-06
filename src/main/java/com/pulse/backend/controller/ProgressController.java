package com.pulse.backend.controller;

import com.pulse.backend.dto.progress.ProgressLogRequest;
import com.pulse.backend.dto.progress.ProgressLogResponse;
import com.pulse.backend.security.UserPrincipal;
import com.pulse.backend.service.ProgressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @PostMapping
    public ResponseEntity<ProgressLogResponse> addLog(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ProgressLogRequest request) {
        return ResponseEntity.ok(new ProgressLogResponse(progressService.addLog(principal.getUser(), request)));
    }

    @GetMapping
    public ResponseEntity<List<ProgressLogResponse>> getLogs(@AuthenticationPrincipal UserPrincipal principal) {
        List<ProgressLogResponse> logs = progressService.getLogs(principal.getUser()).stream()
                .map(ProgressLogResponse::new)
                .toList();
        return ResponseEntity.ok(logs);
    }
}
