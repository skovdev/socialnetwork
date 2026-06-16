# SocialNetwork

Pet-project for self-education, designed to simulate a basic social media application.

## Overview

**SocialNetwork** is a monolith REST API built with Java 25 and Spring Boot 3. It covers user registration with email verification, JWT-based authentication with token rotation, and user profile management. Secrets are stored in AWS Secrets Manager; transactional emails are sent via AWS SES v2.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 3.5.6 |
| Security | Spring Security 6, JWT (RS256 via jjwt 0.13) |
| Database | PostgreSQL 42.7 |
| Migrations | Liquibase 5 |
| Cloud | AWS Secrets Manager, AWS SES v2 |
| API Docs | SpringDoc OpenAPI / Swagger UI |
| Testing | JUnit Jupiter 6, Mockito 5, Testcontainers 1.20 |
| Build | Maven 3, Lombok |

## Project Structure

```
src/main/java/local/socialnetwork/
├── auth/          # Registration, email verification, login, token refresh, logout
├── profiles/      # User profile read endpoints
├── core/          # JWT provider, security config, filters, AWS clients
├── shared/        # Base entity, exceptions, API version constant
└── dto/           # Shared API response wrapper
```

## API Endpoints

Base path: `/api/v1`

### Auth (`/auth`)

| Method | Path | Auth | Description |
|---|---|---|---|
| `POST` | `/auth/register` | Public | Register a new account — sends a verification email |
| `GET` | `/auth/verify` | Public | Verify email via `?token=<token>` |
| `POST` | `/auth/login` | Public | Authenticate and receive access + refresh token pair |
| `POST` | `/auth/refresh` | Public | Rotate refresh token, issue new token pair |
| `POST` | `/auth/logout` | Bearer | Invalidate all refresh tokens for the current user |

### Users (`/users`)

| Method | Path | Auth | Description |
|---|---|---|---|
| `GET` | `/users/{username}` | Bearer | Retrieve a user profile by username |

Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

## Database Schema

Migrations are managed by Liquibase and run automatically on startup.

| Table | Purpose |
|---|---|
| `auth_users` | Core credentials and account status (`PENDING_VERIFICATION`, `ACTIVE`, `DISABLED`) |
| `auth_user_roles` | Roles assigned to each user |
| `auth_email_verification_tokens` | One-time tokens sent by email on registration |
| `auth_refresh_tokens` | Issued refresh tokens (JTI-based, supports full logout) |
| `user_profiles` | Public profile data — username, display name, bio, avatar, etc. |

## Prerequisites

- Java 25
- Maven 3.9+
- PostgreSQL 14+ running locally (or via Docker)
- AWS credentials with access to Secrets Manager and SES

## Configuration

The application reads its configuration from `application.properties`. Sensitive values are injected via environment variables or AWS Secrets Manager.

### Required environment variables

| Variable | Description |
|---|---|
| `AWS_ACCESS_KEY_ID` | IAM access key |
| `AWS_SECRET_ACCESS_KEY` | IAM secret key |

### Optional environment variables

| Variable | Default | Description |
|---|---|---|
| `CORS_ALLOWED_ORIGINS` | `*` | Comma-separated list of allowed CORS origins |

### AWS Secrets Manager

The application fetches the RS256 key pair from the secret named `socialnetwork-security-private-public-keys`.

### Local database

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/socialnetwork_db
spring.datasource.username=socialnetwork_user
spring.datasource.password=socialnetwork_password
```

## Running the Application

```bash
mvn spring-boot:run
```

The server starts on port `8080`.

## Running Tests

```bash
mvn test
```

Integration tests use Testcontainers and spin up a real PostgreSQL instance automatically.

## Actuator

Health, info, and metrics endpoints are exposed at `/actuator`:

```
GET /actuator/health
GET /actuator/info
GET /actuator/metrics
```