package com.pulse.backend.service;

import com.pulse.backend.dto.progress.ProgressLogRequest;
import com.pulse.backend.entity.ProgressLog;
import com.pulse.backend.entity.User;
import com.pulse.backend.repository.ProgressLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final ProgressLogRepository progressLogRepository;

    public ProgressLog addLog(User user, ProgressLogRequest request) {
        ProgressLog log = ProgressLog.builder()
                .user(user)
                .logDate(request.getLogDate() != null ? request.getLogDate() : LocalDate.now())
                .weightKg(request.getWeightKg())
                .bodyFatPct(request.getBodyFatPct())
                .notes(request.getNotes())
                .build();

        return progressLogRepository.save(log);
    }

    public List<ProgressLog> getLogs(User user) {
        return progressLogRepository.findByUserOrderByLogDateDesc(user);
    }
}