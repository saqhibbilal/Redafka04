# User Service

This is the User Service for the Riyada Digital Wallet System. It handles user management, authentication, and authorization.

## Features

- User registration and login
- JWT token-based authentication
- User profile management
- Password hashing with BCrypt
- CORS configuration for frontend integration

## Technology Stack

- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (JSON Web Tokens)
- Maven

## API Endpoints

### Authentication

- `POST /api/users/register` - Register a new user
- `POST /api/users/login` - Login user and get JWT token

### User Management

- `GET /api/users/profile` - Get user profile (requires authentication)
- `PUT /api/users/profile` - Update user profile (requires authentication)
- `DELETE /api/users/account` - Delete user account (requires authentication)

## Configuration

The service uses the following configuration:

- **Port**: 8081
- **Database**: PostgreSQL (user_db)
- **JWT Secret**: Configured in application.yml
- **JWT Expiration**: 24 hours

## Running the Service

1. Ensure PostgreSQL is running with the user_db database
2. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Testing

Run tests with:

```bash
mvn test
```

The service includes integration tests that use an in-memory H2 database.
