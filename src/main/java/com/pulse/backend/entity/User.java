package com.pulse.backend.entity;
import com.pulse.backend.entity.enums.Gender;
import com.pulse.backend.entity.enums.Goal;
import com.pulse.backend.entity.enums.Level;
import com.pulse.backend.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,unique = true)
    private String email;
    @Column(name = "password_hash",nullable = false)
    private String passwordHash;

    @Column(name = "full_name",nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

     private Integer age;
     @Column(name = "height_cm")
    private Double heightCm;
     @Column(name = "weight_kg")
    private Double weightKg;
     @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id")
    private Sport sport;
     @Enumerated(EnumType.STRING)
    private Level level;
     @Enumerated(EnumType.STRING)
    private Goal goal;

     @Enumerated(EnumType.STRING)
     @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;
     @Column(name = "created_at",nullable = false,updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
