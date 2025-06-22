# MarketView Authentication System

## Overview

This authentication system provides:

- JWT-based authentication
- Google OAuth2 integration
- Role-based authorization
- User registration and login

## Authentication Flow

1. User registers or logs in through one of the endpoints
2. Server validates credentials and issues a JWT token
3. Client includes JWT token in the Authorization header for subsequent requests
4. JwtFilter validates tokens and sets up SecurityContext
5. Controllers check for proper authorization via Spring Security

## Directory Structure

```
src/main/java/com/marketview/Spring/MV/
├── config/             # Configuration classes
├── controller/         # REST controllers
├── dto/                # Data Transfer Objects
│   ├── AuthResponse.java     # JWT token response
│   ├── JwtResponse.java      # Extends AuthResponse for backward compatibility
│   ├── LoginRequest.java     # Login request payload
│   └── RegisterRequest.java  # Registration request payload
├── model/              # Data models
│   ├── Role.java            # User role entity
│   └── User.java            # User entity
├── repository/         # Data access interfaces
├── security/           # Security configuration
│   ├── CustomUserDetailsService.java  # User details service
│   ├── JwtAuthenticationEntryPoint.java  # Handles unauthorized access
│   ├── JwtFilter.java       # JWT processing filter
│   ├── JwtUtil.java         # JWT creation and validation
│   ├── SecurityConfig.java  # Security configuration
│   └── UserPrincipal.java   # User principal for both JWT and OAuth
└── service/            # Business logic
    └── AuthService.java      # Authentication service
```

## API Endpoints

- **POST /api/auth/register** - Register a new user
- **POST /api/auth/login** - Login and get JWT token
- **POST /api/auth/signup** - Alias for /register
- **GET /oauth2/authorize/google** - Start Google OAuth flow

### Deprecated/Legacy Endpoints
- **POST /auth/register** - Redirects to /api/auth/legacy/register
- **POST /auth/login** - Redirects to /api/auth/legacy/login

## Testing with Postman

See the included Postman collection for testing all authentication endpoints.

## Role-Based Access

Endpoints are protected based on roles:

- `/api/public/**` - Public access
- `/api/admin/**` - Requires ADMIN role
- `/api/premium/**` - Requires PREMIUM role
- All other endpoints require authentication

## OAuth Configuration

The system supports Google OAuth2. Additional providers (GitHub, Facebook, etc.) can be added by:

1. Creating implementation classes in the oauth2 package
2. Adding provider configurations in application.properties
3. Updating the OAuth2UserInfoFactory
