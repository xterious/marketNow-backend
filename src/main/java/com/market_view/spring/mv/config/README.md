# MarketView Authentication System

## Overview

This authentication system supports:

- Traditional username/password authentication
- Google OAuth2 authentication
- Role-based access control

## API Endpoints

### Authentication

- **Register**: `POST /api/auth/register` or `POST /api/auth/signup`
  - Request body: `RegisterRequest` with username, email, password, firstName, lastName
  - Response: Success message

- **Login**: `POST /api/auth/login`
  - Request body: `LoginRequest` with username, password
  - Response: `AuthResponse` containing JWT token

- **Google OAuth2**: `GET /oauth2/authorize/google`
  - Redirects to Google login page
  - After successful authentication, redirects back with JWT token

### Legacy Endpoints (Maintained for Backward Compatibility)

- **Register**: `POST /auth/register`
- **Login**: `POST /auth/login`

### Protected Endpoints

- **Public**: `/api/public/**` - Accessible by anyone
- **User**: `/api/user/**` - Requires authentication
- **Admin**: `/api/admin/**` - Requires ADMIN role
- **Premium**: `/api/premium/**` - Requires PREMIUM role

## Testing with Postman

1. **Register a User**:
   - `POST /api/auth/register`
   - Body: `{"username": "user", "email": "user@example.com", "password": "password", "firstName": "John", "lastName": "Doe"}`

2. **Login**:
   - `POST /api/auth/login`
   - Body: `{"username": "user", "password": "password"}`
   - Save the returned token

3. **Access Protected Endpoints**:
   - Add header: `Authorization: Bearer {token}`
   - Access endpoints based on user roles

## Role Management

Users are assigned the ROLE_USER role by default. Additional roles can be assigned by administrators.
