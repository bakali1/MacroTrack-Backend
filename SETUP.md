# MacroTrack API - Setup Guide

New to this project? Follow this guide to get running locally in 5 minutes.

## Prerequisites

- Docker + Docker Compose
- Java 21+ (for local dev without Docker)
- Maven 3.9+

## Quick Start (Docker)

### 1. Clone & Navigate

```bash
cd /home/baku/programing/api
```

### 2. Setup Environment

```bash
cp .env.example .env
```

Edit `.env` if you want different DB credentials (optional).

### 3. Start Services

```bash
docker-compose up -d
```

Waits for DB health check. API available at `http://localhost:8080` after ~30-60s.

### 4. Verify

```bash
curl http://localhost:8080/api/food/search?q=apple
```

### 5. Stop Services

```bash
docker-compose down
```

---

## Local Development (No Docker)

### 1. Java + Maven Setup

```bash
java -version  # Ensure Java 21+
mvn -version   # Ensure Maven 3.9+
```

### 2. Start Postgres Only

```bash
docker-compose up -d postgres
```

### 3. Configure Spring

Create `src/main/resources/application-dev.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/macrotrack_db
spring.datasource.username=macrotrack_user
spring.datasource.password=macrotrack_pass
spring.jpa.hibernate.ddl-auto=update
spring.cache.type=caffeine
```

### 4. Build & Run

```bash
mvn clean install
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

API available at `http://localhost:8080`.

---

## Project Structure

```bash
src/
├── main/java/com/macrotrack/api/
│   ├── controllers/     # REST endpoints
│   ├── services/        # Business logic (OpenFoodFacts API client)
│   ├── entity/          # JPA entities (User, etc)
│   └── config/          # Spring config (Caffeine cache)
└── main/resources/
    └── application.yml  # Spring config
```

## Key Endpoints

- `GET /api/food/product/{barcode}` - Fetch product by barcode (cached)
- `GET /api/food/search?q=<query>` - Search products

## Troubleshooting

### Port 5432 already in use

```bash
# Find process using port
lsof -i :5432

# Change in .env
POSTGRES_PORT=5433
```

### Build fails with "cannot resolve OpenFoodFactsService"

```bash
# Clean Maven cache
mvn clean
mvn compile
```

### Container won't start

```bash
# Check logs
docker-compose logs postgres
docker-compose logs api
```

### Database connection refused

Ensure `postgres` service is healthy:

```bash
docker-compose ps  # Check STATUS
```

---

## Development Commands

```bash
# Rebuild containers
docker-compose up --build

# View real-time logs
docker-compose logs -f api

# Execute commands in container
docker-compose exec api mvn test

# Access database
docker-compose exec postgres psql -U macrotrack_user -d macrotrack_db
```

---

## Environment Variables

| Variable                 | Default          | Purpose        |
| ----------------------   | ---------------- | -------------- |
| `POSTGRES_DB`            | macrotrack_db    | Database name  |
| `POSTGRES_USER`          | macrotrack_user  | DB username    |
| `POSTGRES_PASSWORD`      | macrotrack_pass  | DB password    |
| `POSTGRES_PORT`          | 5432             | DB port        |
| `API_PORT`               | 8080             | API port       |
| `SPRING_PROFILES_ACTIVE` | dev              | Spring profile |

---

## Next Steps

1. Read `src/main/java/com/macrotrack/api/services/OpenFoodFactsService.java` - understand food data fetching
2. Check `src/main/java/com/macrotrack/api/config/CacheConfig.java` - caching strategy
3. Add new endpoints to `controllers/OpenFoodFactsController.java`

---

## Questions?

- Check project logs: `docker-compose logs`
- Review Spring Boot docs: [Spring Boot](https://spring.io/projects/spring-boot)
- OpenFoodFacts API: [API Documentation](https://world.openfoodfacts.org/api/v2/search)
