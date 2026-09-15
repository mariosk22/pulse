package com.pulse.backend.entity;
import  jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workouts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Workout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_plan_id",nullable = false)
    private TrainingPlan trainingPlan;

    @Column(name = "week_plan", nullable = false)
    private Integer weekNumber;
    //** 1= monday 7=sunday */
    @Column(name = "day_of_week",nullable = false)
    private Integer dayOfWeek;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "workout",cascade = CascadeType.ALL,orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    private List<WorkoutExercise>workoutExercises = new ArrayList<>();
}
