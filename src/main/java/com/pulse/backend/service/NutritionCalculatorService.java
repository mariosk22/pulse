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
            if (user.getWeightKg() == null || user.getHeightCm() == null || user.getAge() == null
                    || user.getGender() == null || user.getLevel() == null || user.getGoal() == null) {
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "Finish onboarding first: weight, height, age, gender, level and goal are required");
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
                    Meal.builder().nutritionPlan(plan).mealType(MealType.BREAKFAST).name("Breakfast")
                            .calories((int) Math.round(total * 0.25))
                            .description("A source of protein plus complex carbohydrates (e.g. eggs, oatmeal).")
                            .build(),
                    Meal.builder().nutritionPlan(plan).mealType(MealType.LUNCH).name("Lunch")
                            .calories((int) Math.round(total * 0.35))
                            .description("The main meal of the day: lean meat or fish, a side dish and vegetables.")
                            .build(),
                    Meal.builder().nutritionPlan(plan).mealType(MealType.DINNER).name("Dinner")
                            .calories((int) Math.round(total * 0.25))
                            .description("A lighter meal: protein plus vegetables.")
                            .build(),
                    Meal.builder().nutritionPlan(plan).mealType(MealType.SNACK).name("Snack")
                            .calories((int) Math.round(total * 0.15))
                            .description("Nuts, fruit or yogurt.")
                            .build()
            );
        }
    }

