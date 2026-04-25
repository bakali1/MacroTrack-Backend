# MacroTrack API

A Spring Boot REST API that integrates with [OpenFoodFacts](https://world.openfoodfacts.org/) to provide food product nutritional data.

## Features

- Search food products by name or barcode
- Fetch detailed nutritional information (calories, carbs, proteins, fats)
- Caffeine-based caching for API responses
- PostgreSQL database support

## Tech Stack

- Java 21
- Spring Boot 4.0.5 (Web, Data JPA, WebFlux, Cache)
- PostgreSQL
- Caffeine cache
- Maven

## Quick Start

```bash
# Start with Docker
cp .env.example .env
docker-compose up -d

# API runs at http://localhost:8080
```

## Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/food/search?q=<query>` | Search products |
| GET | `/api/food/product/{barcode}` | Get product by barcode |
| GET | `/api/food/product/{barcode}/details` | Get product details |

## Configuration

Configure via `.env` or `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/macrotrack_db
spring.datasource.username=macrotrack_user
spring.datasource.password=macrotrack_pass
spring.cache.type=caffeine
```