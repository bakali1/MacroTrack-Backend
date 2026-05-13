package com.macrotrack.api.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.macrotrack.api.entity.Product;
import com.macrotrack.api.services.OpenFoodFactsService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/food")
public class OpenFoodFactsController {

    private final OpenFoodFactsService openFoodFactsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenFoodFactsController(OpenFoodFactsService openFoodFactsService) {
        this.openFoodFactsService = openFoodFactsService;
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
    public String searchProducts(@RequestParam String q) {
        return openFoodFactsService.searchProducts(q);
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