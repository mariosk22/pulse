package com.pulse.backend.dto.user;
import com.pulse.backend.entity.enums.Gender;
import com.pulse.backend.entity.enums.Goal;
import com.pulse.backend.entity.enums.Level;
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
    private Integer age;
    private Double heightCm;
    private Double weightKg;
}
