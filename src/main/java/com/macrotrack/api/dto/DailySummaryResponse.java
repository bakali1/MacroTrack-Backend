package com.macrotrack.api.dto;

import lombok.Data;
import java.util.List;

@Data
public class DailySummaryResponse {
    private String date;
    private int totalCalories;
    private double totalCarbs;
    private double totalProtein;
    private double totalFat;
    private int calorieGoal;
    private List<MealGroup> meals;

    @Data
    public static class MealGroup {
        private String name;
        private int calories;
        private List<FoodItem> items;

        @Data
        public static class FoodItem {
            private String name;
            private int calories;
        }
    }
}
