package com.macrotrack.api.dto;

import lombok.Data;

@Data
public class MealEntryRequest {
    private String date;
    private String mealType;
    private String foodName;
    private Integer calories;
    private Double carbs;
    private Double protein;
    private Double fat;
}
