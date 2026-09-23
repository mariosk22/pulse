package com.pulse.backend.dto.progress;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class ProgressLogRequest {
    private LocalDate logDate;
    private Double weightKg;
    private Double bodyFatPct;
    private String notes;

}
