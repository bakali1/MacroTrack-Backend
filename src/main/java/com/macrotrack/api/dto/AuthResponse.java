package com.macrotrack.api.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String userId;
    private String email;
    private String name;
    private Integer dailyCalorieGoal;

    public AuthResponse(String token, String userId, String email, String name, Integer dailyCalorieGoal) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.dailyCalorieGoal = dailyCalorieGoal;
    }
}
