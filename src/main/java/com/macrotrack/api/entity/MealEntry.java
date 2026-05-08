package com.macrotrack.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "meal_entries")
public class MealEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String date;

    @Column(name = "meal_type", nullable = false)
    private String mealType;

    @Column(name = "food_name", nullable = false)
    private String foodName;

    private Integer calories;

    private Double carbs;

    private Double protein;

    private Double fat;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
