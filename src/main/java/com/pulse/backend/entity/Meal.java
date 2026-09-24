package com.pulse.backend.entity;
import com.pulse.backend.entity.enums.MealType;
import jakarta.persistence.*;
import lombok.*;

import javax.swing.plaf.metal.MetalLabelUI;


@Entity
@Table(name = "meals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutrition_plan_id",nullable = false)
    private NutritionPlan nutritionPlan;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type",nullable = false)
    private MealType mealType;

    @Column(nullable = false)
    private String name;

    private Integer calories;
    @Column(length = 500)
    private String description;
}
