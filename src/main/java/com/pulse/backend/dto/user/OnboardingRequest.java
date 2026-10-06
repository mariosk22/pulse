package com.pulse.backend.dto.user;

import com.pulse.backend.entity.enums.Gender;
import com.pulse.backend.entity.enums.Goal;
import com.pulse.backend.entity.enums.Level;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OnboardingRequest {

    @NotNull
    private Long sportId;

    @NotNull
    private Level level;

    @NotNull
    private Goal goal;

    private Gender gender;

    @Min(value = 10, message = "must be at least 10")
    @Max(value = 100, message = "must be at most 100")
    private Integer age;

    @DecimalMin(value = "100", message = "must be at least 100 cm")
    @DecimalMax(value = "250", message = "must be at most 250 cm")
    private Double heightCm;

    @DecimalMin(value = "30", message = "must be at least 30 kg")
    @DecimalMax(value = "300", message = "must be at most 300 kg")
    private Double weightKg;
}
