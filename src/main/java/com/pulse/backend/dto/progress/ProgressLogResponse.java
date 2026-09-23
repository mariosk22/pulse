package com.pulse.backend.dto.progress;
import com.pulse.backend.entity.ProgressLog;
import lombok.Getter;
import java.time.LocalDate;

@Getter
public class ProgressLogResponse {
    private final Long id;
    private final LocalDate logDate;
    private final Double weightKg;
    private final Double bodyFatPct;
    private final String notes;

    public ProgressLogResponse(ProgressLog log){
        this.id = log.getId();
        this.logDate=log.getLogDate();
        this.weightKg=log.getWeightKg();
        this.bodyFatPct=log.getBodyFatPct();
        this.notes=log.getNotes();

    }


}
