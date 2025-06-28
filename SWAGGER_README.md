# Swagger UI Documentation

This Spring Boot application includes Swagger UI for API documentation and testing.

## Accessing Swagger UI

Once the application is running, you can access the Swagger UI at:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## Features

The Swagger UI provides:

- **Interactive API Documentation**: Browse and test all available endpoints
- **Request/Response Examples**: See example requests and responses
- **Authentication Support**: Test endpoints with JWT Bearer tokens
- **OAuth2 Integration**: Support for Google OAuth2 authentication
- **Parameter Validation**: Built-in validation for request parameters
- **Response Schemas**: Detailed response models and schemas

## API Categories

The API is organized into the following categories:

1. **Authentication** - User registration and login
2. **Users** - User management operations
3. **Stocks** - Stock market data and quotes
4. **News** - Financial news and headlines
5. **Wishlist** - User wishlist management
6. **Currency** - Currency exchange rates
7. **Admin** - Administrative operations

## Authentication

### JWT Bearer Token
For protected endpoints, use the JWT Bearer token obtained from the login endpoint:
```
Authorization: Bearer <your-jwt-token>
```

### OAuth2 (Google)
The application supports Google OAuth2 authentication for social login.

## Configuration

Swagger UI configuration can be customized in `application.properties`:

```properties
# Swagger UI Configuration
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.tryItOutEnabled=true
```

## Development

To add Swagger annotations to new controllers:

1. Add the `@Tag` annotation to the controller class
2. Add `@Operation` annotations to describe each endpoint
3. Add `@ApiResponses` to document possible responses
4. Add `@Parameter` annotations to describe request parameters
5. Add `@SecurityRequirement` for protected endpoints

Example:
```java
@RestController
@Tag(name = "Example", description = "Example API endpoints")
public class ExampleController {

    @GetMapping("/example")
    @Operation(summary = "Get example data", description = "Retrieves example data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<String> getExample() {
        return ResponseEntity.ok("Example");
    }
}
``` 