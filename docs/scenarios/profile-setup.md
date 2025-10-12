# Profile Setup & View

## Actor
- Authenticated user

## Preconditions
- User has a valid JWT (access token).

## Steps
1. User sends `PUT /api/v1/me/profile` with fields like:
    - displayName
    - bio
    - avatarUrl
2. System extracts userId from the JWT and updates the corresponding profile record.
3. User (or another user) can retrieve the profile via `GET /api/v1/users/{userId}/profile`.

## Expected Result
- The profile is created or updated for the authenticated user.
- Updated profile data is retrievable via the public GET endpoint.

## Error Cases
- Missing JWT → 401 Unauthorized
- Invalid or expired JWT → 401 Unauthorized
- Invalid input data → 400 Bad Request
