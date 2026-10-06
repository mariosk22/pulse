package com.pulse.backend.dto.progress;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProgressLogRequest {

    @PastOrPresent(message = "must not be in the future")
    private LocalDate logDate;

    @DecimalMin(value = "20", message = "must be at least 20 kg")
    @DecimalMax(value = "400", message = "must be at most 400 kg")
    private Double weightKg;

    @DecimalMin(value = "1", message = "must be at least 1 %")
    @DecimalMax(value = "80", message = "must be at most 80 %")
    private Double bodyFatPct;

    @Size(max = 500, message = "must be at most 500 characters")
    private String notes;
}
