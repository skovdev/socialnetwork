# SocialNetwork

Pet-project for self-education, designed to simulate a basic social media application.

## Overview

**SocialNetwork** is a monolith REST API built with Java 25 and Spring Boot 3. It covers user registration with email verification, JWT-based authentication with token rotation, user profile management including avatar upload, and post creation/browsing. Secrets are stored in AWS Secrets Manager; transactional emails are sent via AWS SES v2; avatar images are stored in AWS S3 and served via presigned URLs.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 3.5.6 |
| Security | Spring Security 6, JWT (RS256 via jjwt 0.13) |
| Database | PostgreSQL 42.7 |
| Migrations | Liquibase 5 |
| Cloud | AWS Secrets Manager, AWS SES v2, AWS S3 |
| API Docs | SpringDoc OpenAPI / Swagger UI |
| Testing | JUnit Jupiter 6, Mockito 5, Testcontainers 1.21 |
| Build | Maven 3, Lombok |

## Repository Structure

```
socialnetwork-api/     # Backend — this Spring Boot REST API
socialnetwork-web/     # Frontend — React + TypeScript SPA
docs/                  # Shared API docs and scenarios
CI/                    # Jenkins pipeline
```

## Project Structure

```
socialnetwork-api/src/main/java/local/socialnetwork/
├── auth/          # Registration, email verification, login, token refresh, logout
├── profiles/      # User profile read/update endpoints, avatar upload/delete
├── posts/         # Post creation, feed, and management endpoints
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

### Profiles (`/profiles`) — the authenticated user's own profile

| Method | Path | Auth | Description |
|---|---|---|---|
| `GET` | `/profiles` | Bearer | Retrieve the current user's profile |
| `PUT` | `/profiles` | Bearer | Update the current user's profile fields |
| `POST` | `/profiles/avatar` | Bearer | Upload a new avatar image (JPEG, PNG, or WebP; max 5 MB), replacing any existing one |
| `DELETE` | `/profiles/avatar` | Bearer | Delete the current user's avatar |

### Users (`/users`)

| Method | Path | Auth | Description |
|---|---|---|---|
| `GET` | `/users/{username}` | Bearer | Retrieve a user profile by username |

Avatar URLs returned in profile responses are short-lived, presigned S3 URLs (valid for 1 hour by default).

### Posts (`/posts`)

| Method | Path | Auth | Description |
|---|---|---|---|
| `POST` | `/posts` | Bearer | Create a new post authored by the current user |
| `GET` | `/posts` | Bearer | Retrieve a paginated feed of posts, newest first |
| `GET` | `/posts/{id}` | Bearer | Retrieve a single post by ID |
| `PUT` | `/posts/{id}` | Bearer | Update a post's content — author only |
| `DELETE` | `/posts/{id}` | Bearer | Delete a post — author only |

Post content is limited to 5000 characters. Each post response includes a minimal author summary (username, display name).

Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

## Database Schema

Migrations are managed by Liquibase and run automatically on startup.

| Table | Purpose |
|---|---|
| `auth_users` | Core credentials and account status (`PENDING_VERIFICATION`, `ACTIVE`, `DISABLED`) |
| `auth_user_roles` | Roles assigned to each user |
| `auth_email_verification_tokens` | One-time tokens sent by email on registration |
| `auth_refresh_tokens` | Issued refresh tokens (JTI-based, supports full logout) |
| `user_profiles` | Public profile data — username, display name, bio, avatar (S3 storage key), etc. |
| `posts` | User-authored posts — content, author reference, timestamps |

## Prerequisites

- Java 25
- Maven 3.9+
- PostgreSQL 14+ running locally (or via Docker)
- AWS credentials with access to Secrets Manager, SES, and S3

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
| `SN_AWS_S3_AVATAR_BUCKET_NAME` | `socialnetwork-user-avatar-upload` | S3 bucket used to store uploaded avatar images |

### AWS Secrets Manager

The application fetches the RS256 key pair from the secret named `socialnetwork-security-private-public-keys`.

### AWS S3 (avatar storage)

Avatar images are uploaded to the S3 bucket configured above (region `eu-north-1`), using the same IAM credentials as Secrets Manager/SES. Profile responses expose a presigned, time-limited download URL (default validity: 1 hour) rather than the raw storage key.

### Local database

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/socialnetwork_db
spring.datasource.username=socialnetwork_user
spring.datasource.password=socialnetwork_password
```

## Running the Application

```bash
cd socialnetwork-api
mvn spring-boot:run
```

The server starts on port `8080`.

## Running Tests

```bash
cd socialnetwork-api
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