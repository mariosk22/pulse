package com.pulse.backend.dto.nutrition;
import com.pulse.backend.entity.Meal;
import lombok.Getter;
@Getter
public class MealResponse {
    private final String mealType;
    private final String name;
    private final Integer calories;
    private final String description;

    public MealResponse(Meal meal){
        this.mealType= meal.getMealType().name();
        this.name=meal.getName();
        this.calories = meal.getCalories();
        this.description=meal.getDescription();
    }
}
