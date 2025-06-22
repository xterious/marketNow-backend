# MarketView Application

A Spring Boot application with MongoDB for stock market data viewing and analysis.

## Features

- User authentication with JWT
- OAuth2 authentication with Google
- Role-based access control
- Stock market data management

## Prerequisites

- Java 21
- Maven
- MongoDB

## Setup

### 1. Configure application.properties

Update the following properties in `src/main/resources/application.properties`:

```properties
# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/marketview

# JWT Configuration
jwt.secret=your-jwt-secret-key-should-be-very-long-and-secure

# OAuth2 Configuration
spring.security.oauth2.client.registration.google.client-id=your-google-client-id
spring.security.oauth2.client.registration.google.client-secret=your-google-client-secret
```

### 2. Create Google OAuth Client ID

1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Navigate to "APIs & Services" > "Credentials"
4. Click "Create Credentials" > "OAuth client ID"
5. Set the application type to "Web application"
6. Add the authorized redirect URI: `http://localhost:8080/oauth2/callback/google`
7. Copy the generated client ID and client secret to your application.properties file

### 3. Run the application

```bash
./mvnw spring-boot:run
```

## API Endpoints

### Authentication

- `POST /api/auth/signup` - Register a new user
- `POST /api/auth/login` - Login with username and password
- `GET /oauth2/authorize/google` - Start Google OAuth2 authentication flow

### Role-specific endpoints

- `/api/public/**` - Accessible by anyone
- `/api/admin/**` - Accessible only by ADMIN users
- `/api/premium/**` - Accessible only by PREMIUM users

## User Roles

- **ROLE_USER** - Default role for all registered users
- **ROLE_ADMIN** - Administrative access
- **ROLE_PREMIUM** - Premium features access

## Security Architecture

The application uses a combination of JWT-based authentication for traditional login and OAuth2 for social login with Google.

### Local Authentication Flow

1. User submits username/password to `/api/auth/login`
2. Server validates credentials and returns a JWT token
3. Client stores the token and includes it in the Authorization header for subsequent requests

### OAuth2 Authentication Flow

1. User initiates Google login by visiting `/oauth2/authorize/google`
2. User authenticates with Google
3. Google redirects back to our application with an authorization code
4. The application exchanges the code for user information
5. If the user exists, they are logged in; otherwise, a new account is created
6. A JWT token is returned for subsequent requests
