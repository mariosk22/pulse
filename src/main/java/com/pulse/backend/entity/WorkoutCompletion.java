package com.pulse.backend.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;

import java.time.Instant;
@Entity
@Table(name = "workout_completions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_id",nullable = false)
    private Workout workout;

    @Column(name = "completed_at",nullable = false)
    @Builder.Default
    private Instant completedAt = Instant.now();

    @Column(name = "durations_minutes")
    private Integer durationMinutes;

    @Column(length = 500)
    private String notes;
}
