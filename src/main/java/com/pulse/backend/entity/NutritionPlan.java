package com.pulse.backend.entity;
import com.pulse.backend.entity.enums.MealType;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "nutrition_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutritionPlan {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

   @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_plan_id")
    private TrainingPlan trainingPlan;

   @Column(name = "daily_calories",nullable = false)
    private Integer dailyCalories;

   @Column(name = "protein_g",nullable = false)
    private Integer proteinG;

   @Column(name = "carbs_g",nullable = false)
    private Integer carbsG;

   @Column(name = "fat_G",nullable = false)
    private Integer fatG;

   @OneToMany(mappedBy = "nutritionPlan",cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    private List<Meal> meals = new ArrayList<>();
}
