package com.macrotrack.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

        public static class FoodItem {
            private String itemId;
            private String name;
            private int calories;

            public FoodItem() {}

            @JsonProperty("id")
            public String getItemId() { return itemId; }
            public void setItemId(String itemId) { this.itemId = itemId; }

            public String getName() { return name; }
            public void setName(String name) { this.name = name; }

            public int getCalories() { return calories; }
            public void setCalories(int calories) { this.calories = calories; }
        }
    }
}
