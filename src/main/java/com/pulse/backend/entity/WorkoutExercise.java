package com.pulse.backend.entity;
import com.pulse.backend.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "workout_exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_id",nullable = false)
    private Workout workout;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id",nullable = false)
    private Excercise excercise;
    @Column(name = "order_index",nullable = false)
    private Integer orderIndex;
    private Integer steps;
    private Integer reps;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "rest_seconds")
    private Integer restSeconds;

}
