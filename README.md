# URL Shortener Backend

Production-quality URL shortener built with Java 21, Spring Boot, Spring Security, JWT, and MySQL.

## Features
- User registration/login with BCrypt password hashing + JWT authentication
- Short URL generation (Base62 encoding) with optional custom aliases
- URL expiration (read-time enforced)
- Click analytics with per-click event tracking
- Rate limiting (20 requests/min per client on URL creation)
- Ownership-based authorization (users can only manage their own URLs)
- Swagger/OpenAPI interactive documentation

## Tech Stack
Java 21 · Spring Boot 3.5 · Spring Data JPA · Hibernate · Spring Security · MySQL · JWT (jjwt) · Bucket4j · Swagger/OpenAPI · Maven

## Architecture
Layered architecture: Controller → Service → Repository → Entity, with separate DTOs for API contracts, a centralized global exception handler, and a custom JWT authentication filter chain.

## Setup Instructions
1. Clone the repo
2. Create MySQL database:
```sql
   CREATE DATABASE url_shortener_db;
```
3. Update `src/main/resources/application.properties` with your MySQL credentials
4. Run:
```bash
   mvnw.cmd clean install
   mvnw.cmd spring-boot:run
```
5. API docs available at: `http://localhost:8080/swagger-ui/index.html`
6. Postman collection: `postman/URL-Shortener.postman_collection.json`

## API Endpoints

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| POST | `/api/auth/register` | No | Register a new user |
| POST | `/api/auth/login` | No | Login, returns JWT |
| POST | `/api/urls` | Yes | Create a short URL (optional custom alias/expiry) |
| GET | `/api/urls/{shortCode}` | Yes | Get URL metadata |
| GET | `/api/urls/{shortCode}/redirect` | No | Redirect + record click |
| GET | `/api/urls/my-urls` | Yes | List your URLs |
| DELETE | `/api/urls/{shortCode}` | Yes | Delete your own URL |

## Key Design Decisions
- **Base62 encoding of DB-generated IDs** over random strings — collision-free by construction, no retry logic needed
- **JWT (stateless)** over server-side sessions — enables horizontal scalability across multiple server instances
- **Read-time expiration checks** over scheduled-only cleanup — guarantees correctness on every single access
- **Separate `click_events` table** over a simple counter — enables real analytics (trends over time), not just a running total
- **Object-level ownership checks** in the service layer — prevents Broken Object Level Authorization (BOLA), OWASP API Security Top 10 #1

## Testing
Unit tests for core utility logic (`Base62Encoder`) and service-layer business logic (`UrlService`), using JUnit 5 and Mockito for dependency isolation. Run with:
```bash
mvnw.cmd test
```

## Future Enhancements
- Redis caching layer (cache-aside pattern) for the redirect hot path
- Docker containerization for consistent environments
- Flyway migrations for versioned schema changes
- Refresh token support for JWT renewal without