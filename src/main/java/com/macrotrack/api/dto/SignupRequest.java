package com.macrotrack.api.dto;

import lombok.Data;

@Data
public class SignupRequest {
    private String name;
    private String email;
    private String password;
    private Integer dailyCalorieGoal;
}
