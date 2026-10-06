package com.pulse.backend.dto.plan;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneratePlanRequest {

    @Min(value = 1, message = "must be at least 1 week")
    @Max(value = 52, message = "must be at most 52 weeks")
    private Integer durationWeeks = 4;
}
