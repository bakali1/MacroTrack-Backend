package com.macrotrack.api.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class OpenFoodFactsService {

    private final WebClient webClient;

    // For production: "https://world.openfoodfacts.org"
    // For staging (testing): "https://world.openfoodfacts.net"
    private static final String BASE_URL = "https://world.openfoodfacts.org";

    public OpenFoodFactsService() {
        this.webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.USER_AGENT, "MySpringApp/1.0 (myapp@example.com)")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    // GET product by barcode (read operation - no auth needed)
    @Cacheable(value = "products", key = "#barcode")
    public Mono<String> getProductByBarcode(String barcode) {
        return webClient.get()
                .uri("/api/v2/product/{barcode}.json", barcode)
                .retrieve()
                .bodyToMono(String.class);
    }

    // Search products (limited to 10 req/min!)
    public Mono<String> searchProducts(String query) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v2/search")
                        .queryParam("search_terms", query)
                        .queryParam("page_size", 10)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }
}