package com.pulse.backend.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
@Entity
@Table(name = "progress_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(name = "log_date",nullable = false)
    private LocalDate logDate;

    @Column(name = "weight_kg")
    private Double weightKg;

    @Column(name = "body_fat_pct")
    private Double bodyFatPct;

    @Column(length = 500)
    private String notes;

}
