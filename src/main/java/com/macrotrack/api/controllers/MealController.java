package com.macrotrack.api.controllers;

import com.macrotrack.api.dto.DailySummaryResponse;
import com.macrotrack.api.dto.MealEntryRequest;
import com.macrotrack.api.entity.MealEntry;
import com.macrotrack.api.entity.User;
import com.macrotrack.api.repository.MealEntryRepository;
import com.macrotrack.api.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/meals")
public class MealController {

    private final MealEntryRepository mealEntryRepository;
    private final UserRepository userRepository;

    public MealController(MealEntryRepository mealEntryRepository,
                          UserRepository userRepository) {
        this.mealEntryRepository = mealEntryRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> addEntry(
            Authentication auth,
            @RequestBody MealEntryRequest request) {
        User user = userRepository.findById(UUID.fromString(auth.getName())).orElse(null);
        if (user == null) return ResponseEntity.status(401).build();

        MealEntry entry = new MealEntry();
        entry.setUser(user);
        entry.setDate(request.getDate());
        entry.setMealType(request.getMealType());
        entry.setFoodName(request.getFoodName());
        entry.setCalories(request.getCalories());
        entry.setCarbs(request.getCarbs());
        entry.setProtein(request.getProtein());
        entry.setFat(request.getFat());
        mealEntryRepository.save(entry);

        return ResponseEntity.ok(Map.of("success", true, "id", entry.getId().toString()));
    }

    @GetMapping("/daily")
    public ResponseEntity<?> getDailySummary(
            Authentication auth,
            @RequestParam String date) {
        User user = userRepository.findById(UUID.fromString(auth.getName())).orElse(null);
        if (user == null) return ResponseEntity.status(401).build();

        List<MealEntry> entries =
            mealEntryRepository.findByUserIdAndDateOrderByCreatedAt(user.getId(), date);

        return ResponseEntity.ok(buildSummary(user, date, entries));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEntry(
            Authentication auth,
            @PathVariable UUID id) {
        User user = userRepository.findById(UUID.fromString(auth.getName())).orElse(null);
        if (user == null) return ResponseEntity.status(401).build();

        mealEntryRepository.findById(id).ifPresent(entry -> {
            if (entry.getUser().getId().equals(user.getId())) {
                mealEntryRepository.delete(entry);
            }
        });
        return ResponseEntity.ok(Map.of("success", true));
    }

    private DailySummaryResponse buildSummary(User user, String date, List<MealEntry> entries) {
        DailySummaryResponse response = new DailySummaryResponse();
        response.setDate(date);
        response.setCalorieGoal(user.getDailyCalorieGoal());

        String[] mealTypes = {"Breakfast", "Lunch", "Dinner", "Snacks"};
        List<DailySummaryResponse.MealGroup> mealGroups = new ArrayList<>();

        int totalCal = 0;
        double totalCarbs = 0, totalProtein = 0, totalFat = 0;

        for (String mealType : mealTypes) {
            DailySummaryResponse.MealGroup group = new DailySummaryResponse.MealGroup();
            group.setName(mealType);
            List<DailySummaryResponse.MealGroup.FoodItem> items = new ArrayList<>();
            int mealCal = 0;

            for (MealEntry entry : entries) {
                if (entry.getMealType().equalsIgnoreCase(mealType)) {
                    DailySummaryResponse.MealGroup.FoodItem item =
                        new DailySummaryResponse.MealGroup.FoodItem();
                    item.setName(entry.getFoodName());
                    item.setCalories(entry.getCalories() != null ? entry.getCalories() : 0);
                    items.add(item);
                    mealCal += entry.getCalories() != null ? entry.getCalories() : 0;
                }
            }

            group.setItems(items);
            group.setCalories(mealCal);
            mealGroups.add(group);
            totalCal += mealCal;
        }

        for (MealEntry entry : entries) {
            totalCarbs += entry.getCarbs() != null ? entry.getCarbs() : 0;
            totalProtein += entry.getProtein() != null ? entry.getProtein() : 0;
            totalFat += entry.getFat() != null ? entry.getFat() : 0;
        }

        response.setTotalCalories(totalCal);
        response.setTotalCarbs(Math.round(totalCarbs * 10.0) / 10.0);
        response.setTotalProtein(Math.round(totalProtein * 10.0) / 10.0);
        response.setTotalFat(Math.round(totalFat * 10.0) / 10.0);
        response.setMeals(mealGroups);

        return response;
    }
}
