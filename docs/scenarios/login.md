# Login / JWT / Refresh / Logout

## Actor
- Registered and active user

## Preconditions
- User account exists and has status `ACTIVE`.
- Email and password are valid.

## Steps
1. User sends `POST /api/v1/auth/login` with email and password.
2. System validates credentials and issues:
    - Access token (short-lived, JWT, returned in response)
    - Refresh token (long-lived, stored in HttpOnly cookie)
3. User calls protected endpoints with `Authorization: Bearer <access>`.
4. When the access token expires, user sends `POST /api/v1/auth/refresh` using the refresh cookie.
5. System validates and rotates the refresh token, issuing a new access token.
6. User can log out by sending `POST /api/v1/auth/logout`.
7. System revokes the refresh token, making it invalid for future refresh attempts.

## Expected Result
- User can authenticate with valid credentials.
- Access token grants temporary access to protected endpoints.
- Refresh token rotation works correctly.
- After logout, refresh token can’t be reused.

## Error Cases
- Invalid credentials → 401 Unauthorized
- Missing or invalid JWT → 401 Unauthorized
- Expired refresh token → 401 Unauthorized / 403 Forbidden
