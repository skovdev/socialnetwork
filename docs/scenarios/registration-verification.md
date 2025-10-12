# Registration & Email Verification

## Actor
- Guest user

## Preconditions
- Email is not registered in the system.

## Steps
1. User sends `POST /api/v1/auth/register` with email and password.
2. System creates a new user with status `PENDING_VERIFICATION`.
3. System generates a verification token and stores it.
4. System sends a verification email with the token to the user.
5. User clicks the link in the email, which sends `POST /api/v1/auth/verify` with the token.
6. System validates the token, updates user status to `ACTIVE`, and marks the token as used.

## Expected Result
- User account is active.
- Verification token is consumed.
- User can now log in.

## Error Cases
- Email already exists → 409 Conflict
- Token invalid or expired → 400 Bad Request / 410 Gone
