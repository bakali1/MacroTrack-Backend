package com.macrotrack.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.macrotrack.api.entity.MealEntry;
import com.macrotrack.api.entity.Product;
import com.macrotrack.api.entity.User;
import com.macrotrack.api.repository.MealEntryRepository;
import com.macrotrack.api.repository.UserRepository;
import com.macrotrack.api.services.OpenFoodFactsService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/food")
public class OpenFoodFactsController {

    private static final Logger LOGGER = Logger.getLogger(OpenFoodFactsController.class.getName());

    private final OpenFoodFactsService openFoodFactsService;
    private final MealEntryRepository mealEntryRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenFoodFactsController(OpenFoodFactsService openFoodFactsService,
                                   MealEntryRepository mealEntryRepository,
                                   UserRepository userRepository) {
        this.openFoodFactsService = openFoodFactsService;
        this.mealEntryRepository = mealEntryRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/product/{barcode}")
    public String getProduct(@PathVariable String barcode) {
        return openFoodFactsService.getProductByBarcode(barcode);
    }

    @GetMapping("/product/{barcode}/details")
    public Product getProductDetails(@PathVariable String barcode) {
        String json = openFoodFactsService.getProductByBarcode(barcode);
        return transformToProduct(json, barcode);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            Authentication auth,
            @RequestParam String q) {
        User user = userRepository.findById(UUID.fromString(auth.getName())).orElse(null);
        if (user == null) return ResponseEntity.status(401).build();

        try {
            ObjectNode result = objectMapper.createObjectNode();
            ArrayNode products = objectMapper.createArrayNode();

            // 1. Local results from meal_entries
            List<MealEntry> localEntries =
                mealEntryRepository.findByUserIdAndFoodNameContainingIgnoreCaseOrderByCreatedAtDesc(user.getId(), q);

            LinkedHashMap<String, MealEntry> latestByName = new LinkedHashMap<>();
            for (MealEntry entry : localEntries) {
                latestByName.putIfAbsent(entry.getFoodName().toLowerCase(), entry);
            }

            for (MealEntry entry : latestByName.values()) {
                ObjectNode localProduct = objectMapper.createObjectNode();
                localProduct.put("_id", "local_" + entry.getId().toString());
                localProduct.put("product_name", entry.getFoodName());
                localProduct.put("source", "local");

                ObjectNode nutriments = objectMapper.createObjectNode();
                nutriments.put("energy-kcal_serving", entry.getCalories() != null ? entry.getCalories() : 0);
                if (entry.getCarbs() != null) nutriments.put("carbohydrates_serving", entry.getCarbs());
                if (entry.getProtein() != null) nutriments.put("proteins_serving", entry.getProtein());
                if (entry.getFat() != null) nutriments.put("fat_serving", entry.getFat());
                localProduct.set("nutriments", nutriments);

                products.add(localProduct);
            }

            // 2. OpenFoodFacts results
            try {
                String offJson = openFoodFactsService.searchProducts(q);
                JsonNode offRoot = objectMapper.readTree(offJson);
                JsonNode offProducts = offRoot.path("products");
                if (offProducts.isArray()) {
                    for (JsonNode p : offProducts) {
                        products.add(p);
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "OpenFoodFacts search failed, returning local results only", e);
            }

            result.put("count", products.size());
            result.set("products", products);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Search failed"));
        }
    }

    private Product transformToProduct(String json, String barcode) {
        Product product = new Product();
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode productNode = root.path("product");
            if (productNode.isMissingNode()) {
                return product;
            }
            product.setId(getTextValue(productNode, "_id"));
            product.setName(getTextValue(productNode, "product_name", "product_name"));
            product.setServing_size(getTextValue(productNode, "serving_size"));
            product.setServing_cal(getTextValue(productNode, "energy-kcal_serving"));
            product.setServing_carbs(getTextValue(productNode, "carbohydrates-total_serving"));
            product.setServing_sugar(getTextValue(productNode, "sugars_serving"));
            product.setServing_fat(getTextValue(productNode, "fat_serving"));
            product.setServing_satFat(getTextValue(productNode, "saturated-fat_serving"));
            product.setServing_Protein(getTextValue(productNode, "proteins_serving"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return product;
    }

    private String getTextValue(JsonNode node, String... fields) {
        for (String field : fields) {
            JsonNode value = node.path(field);
            if (!value.isMissingNode() && !value.asText().isEmpty()) {
                return value.asText();
            }
        }
        return null;
    }
}