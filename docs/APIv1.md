# API v1

## Authentication

- **Register a new user**  
  `POST /api/v1/auth/register`

- **Verify a new user**  
  `POST /api/v1/auth/verify`

- **Log in a new user**  
  `POST /api/v1/auth/login`

- **Refresh token**  
  `POST /api/v1/auth/refresh`

- **Log out**  
  `POST /api/v1/auth/logout`


## Profile

- **Get my profile**  
  `GET /api/v1/me/profile`

- **Update my profile**  
  `PUT /api/v1/me/profile`

- **Get another user's profile**  
  `GET /api/v1/users/{userId}/profile`
