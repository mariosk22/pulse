package com.pulse.backend.service;

import com.pulse.backend.entity.*;
import com.pulse.backend.entity.enums.Gender;
import com.pulse.backend.entity.enums.Goal;
import com.pulse.backend.entity.enums.Level;
import com.pulse.backend.entity.enums.MealType;
import com.pulse.backend.exception.ApiException;
import com.pulse.backend.repository.NutritionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

    @Service
    @RequiredArgsConstructor
    public class NutritionCalculatorService {

        private final NutritionPlanRepository nutritionPlanRepository;

        public NutritionPlan generateForUser(User user, TrainingPlan trainingPlan) {
            if (user.getWeightKg() == null || user.getHeightCm() == null || user.getAge() == null || user.getGender() == null) {
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "Pre vypocet vyzivoveho planu potrebujeme vahu, vysku, vek a pohlavie (doplin v onboardingu)");
            }

            double bmr = calculateBmr(user);
            double tdee = bmr * activityMultiplierFor(user.getLevel());
            int dailyCalories = (int) Math.round(adjustForGoal(tdee, user.getGoal()));

            int proteinG = (int) Math.round(user.getWeightKg() * proteinPerKgFor(user.getGoal()));
            int fatG = (int) Math.round((dailyCalories * 0.25) / 9);
            int carbsG = (int) Math.round((dailyCalories - (proteinG * 4) - (fatG * 9)) / 4.0);

            NutritionPlan plan = NutritionPlan.builder()
                    .user(user)
                    .trainingPlan(trainingPlan)
                    .dailyCalories(dailyCalories)
                    .proteinG(proteinG)
                    .carbsG(carbsG)
                    .fatG(fatG)
                    .build();

            plan.getMeals().addAll(buildMeals(plan));

            return nutritionPlanRepository.save(plan);
        }

        private double calculateBmr(User user) {
            double base = 10 * user.getWeightKg() + 6.25 * user.getHeightCm() - 5 * user.getAge();
            if (user.getGender() == Gender.MALE) {
                return base + 5;
            } else if (user.getGender() == Gender.FEMALE) {
                return base - 161;
            }
            return base - 78;
        }

        private double activityMultiplierFor(Level level) {
            return switch (level) {
                case BEGINNER -> 1.4;
                case INTERMEDIATE -> 1.55;
                case PRO -> 1.7;
            };
        }

        private double adjustForGoal(double tdee, Goal goal) {
            return switch (goal) {
                case WEIGHT_LOSS -> tdee - 500;
                case MUSCLE_GAIN -> tdee + 300;
                case ENDURANCE, STRENGTH, GENERAL_FITNESS -> tdee;
            };
        }

        private double proteinPerKgFor(Goal goal) {
            return switch (goal) {
                case MUSCLE_GAIN, STRENGTH -> 2.0;
                case WEIGHT_LOSS -> 1.8;
                case ENDURANCE -> 1.4;
                case GENERAL_FITNESS -> 1.6;
            };
        }

        private List<Meal> buildMeals(NutritionPlan plan) {
            int total = plan.getDailyCalories();
            return List.of(
                    Meal.builder().nutritionPlan(plan).mealType(MealType.BREAKFAST).name("Ranajky")
                            .calories((int) Math.round(total * 0.25))
                            .description("Zdroj bielkovin + komplexne sacharidy (napr. vajcia, ovsene vlocky).")
                            .build(),
                    Meal.builder().nutritionPlan(plan).mealType(MealType.LUNCH).name("Obed")
                            .calories((int) Math.round(total * 0.35))
                            .description("Hlavne jedlo dna - chudе maso/ryba, priloha, zelenina.")
                            .build(),
                    Meal.builder().nutritionPlan(plan).mealType(MealType.DINNER).name("Vecera")
                            .calories((int) Math.round(total * 0.25))
                            .description("Lahsie jedlo - bielkoviny + zelenina.")
                            .build(),
                    Meal.builder().nutritionPlan(plan).mealType(MealType.SNACK).name("Desiata/Olovrant")
                            .calories((int) Math.round(total * 0.15))
                            .description("Orechy, ovocie, jogurt.")
                            .build()
            );
        }
    }

