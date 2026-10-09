package com.pulse.backend.entity;
import com.pulse.backend.entity.enums.Goal;
import com.pulse.backend.entity.enums.Level;
import com.pulse.backend.entity.enums.PlanStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "training_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id",nullable = false)
    private Sport sport;

    @Enumerated (EnumType.STRING)
    @Column(nullable = false)
    private Level level;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Goal goal;

    @Column(name = "start_date",nullable = false)
    private LocalDate startDate;

    @Column(name = "duration_weeks",nullable = false)
    private Integer durationWeeks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PlanStatus status =PlanStatus.ACTIVE;

    @OneToMany(mappedBy = "trainingPlan",cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    private List<Workout>workouts = new ArrayList<>();
}
