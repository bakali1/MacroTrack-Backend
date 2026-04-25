package com.macrotrack.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Products")
public class Product {
    @Id
    private String id;

    private String name;

    private String Serving_size;

    private String Serving_cal;

    private String Serving_carbs;

    private String Serving_sugar;

    private String Serving_fat;
    
    private String Serving_satFat;

    private String Serving_Protein;
}
