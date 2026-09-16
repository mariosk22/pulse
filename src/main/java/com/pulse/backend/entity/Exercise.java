package com.pulse.backend.entity;
import com.pulse.backend.entity.enums.ExerciseType;
import com.pulse.backend.entity.enums.Level;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exercise {
@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
@Column(nullable = false)
    private String name;
@Column(length = 1000)
    private String description;

@Column(name = "muscle_group")
    private String muscleGroup;

@Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExerciseType type;

@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="sport_id")
    private Sport sport;
@Enumerated(EnumType.STRING)
    @Column( name ="min_level", nullable= false)
    private Level minLevel;

}
