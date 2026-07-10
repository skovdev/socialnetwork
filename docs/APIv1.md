# API v1

Base path: `/api/v1`. Swagger UI: `http://localhost:8080/swagger-ui.html`.

## Auth (`/auth`)

- **Register a new user**
  `POST /auth/register` — Public. Sends a verification email.

- **Verify email**
  `GET /auth/verify?token=<token>` — Public.

- **Log in**
  `POST /auth/login` — Public. Returns an access + refresh token pair.

- **Refresh token**
  `POST /auth/refresh` — Public. Rotates the refresh token, issues a new pair.

- **Log out**
  `POST /auth/logout` — Bearer. Invalidates all refresh tokens for the current user.

## Profiles (`/profiles`) — the authenticated user's own profile

- **Get my profile**
  `GET /profiles` — Bearer.

- **Update my profile**
  `PUT /profiles` — Bearer.

- **Upload avatar**
  `POST /profiles/avatar` — Bearer. JPEG, PNG, or WebP; max 5 MB. Replaces any existing avatar.

- **Delete avatar**
  `DELETE /profiles/avatar` — Bearer.

## Users (`/users`)

- **Get a user's profile by username**
  `GET /users/{username}` — Bearer.

Avatar URLs returned in profile responses are short-lived, presigned S3 URLs (valid for 1 hour by default).
