package com.macrotrack.api.repository;

import com.macrotrack.api.entity.MealEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface MealEntryRepository extends JpaRepository<MealEntry, UUID> {
    List<MealEntry> findByUserIdAndDateOrderByCreatedAt(UUID userId, String date);

    List<MealEntry> findByUserIdAndFoodNameContainingIgnoreCaseOrderByCreatedAtDesc(UUID userId, String foodName);
}
