# Enterprise HR Management API

Spring Boot REST backend for employee onboarding, organization structure, attendance, leave approval, payroll, performance review cycles, and HR reporting.

## Requirements

- Java 21
- MySQL 8.4
- Gradle wrapper (`./gradlew`)

## Configuration

`application.yml` reads `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION_MS`, and `SERVER_PORT` from the environment. Local defaults are intended only for development.

Start MySQL with `docker compose up -d mysql`, then run:

```sh
./gradlew bootRun
```

All APIs are rooted at `/api`. `POST /api/auth/login` is public; all other routes require `Authorization: Bearer <token>`. Authorities are exactly `ROLE_HR`, `ROLE_MANAGER`, and `ROLE_EMPLOYEE`.

## Verification

```sh
./gradlew build
./gradlew test
curl -sS -X POST http://localhost:8080/api/auth/login -H 'Content-Type: application/json' -d '{"email":"unknown@example.com","password":"invalid"}'
```

The final test and project reports are in `tests-artifacts/`.
