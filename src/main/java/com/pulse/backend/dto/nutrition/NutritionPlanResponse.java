package com.pulse.backend.dto.nutrition;
import com.pulse.backend.entity.NutritionPlan;

import lombok.Getter;

import java.util.List;
@Getter
public class NutritionPlanResponse {
    private final Long id;
    private final Integer dailyCalories;
    private final Integer proteinG;
    private final Integer carbsG;
    private final Integer fatG;
    private final List<MealResponse>meals;

    public NutritionPlanResponse(NutritionPlan plan){
        this.id=plan.getId();
        this.dailyCalories=plan.getDailyCalories();
        this.proteinG = plan.getProteinG();
        this.carbsG = plan.getCarbsG();
        this.fatG = plan.getFatG();
        this.meals = plan.getMeals().stream().map(MealResponse::new).toList();
    }
}
