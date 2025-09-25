# Wallet Service

The Wallet Service is a microservice in the Riyada Digital Wallet System responsible for managing digital wallets, balances, and wallet transactions.

## Features

- **Wallet Management**: Create and manage digital wallets for users
- **Balance Operations**: Credit and debit wallet balances with transaction tracking
- **Transaction History**: Complete audit trail of all wallet transactions
- **Auto-Creation**: Automatically create wallets when users register
- **Security**: JWT-based authentication and validation

## Technology Stack

- **Spring Boot 3.2.0** - Application framework
- **Spring Data JPA** - Data persistence
- **PostgreSQL** - Primary database
- **JWT** - Authentication and authorization
- **Spring Cloud OpenFeign** - Inter-service communication

## API Endpoints

### Wallet Management

- `POST /api/wallets/create` - Create a new wallet
- `POST /api/wallets/create-for-user/{userId}` - Create wallet for user
- `GET /api/wallets/user/{userId}` - Get wallet by user ID
- `GET /api/wallets/user/{userId}/balance` - Get wallet balance
- `GET /api/wallets/user/{userId}/exists` - Check if wallet exists

### Balance Operations

- `POST /api/wallets/user/{userId}/credit` - Credit amount to wallet
- `POST /api/wallets/user/{userId}/debit` - Debit amount from wallet
- `POST /api/wallets/transfer` - Transfer between wallets (placeholder)

### Transaction History

- `GET /api/wallets/user/{userId}/transactions` - Get wallet transactions

### Health Check

- `GET /api/wallets/health` - Service health status

## Database Schema

### Wallets Table

```sql
CREATE TABLE wallets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    balance DECIMAL(15,2) DEFAULT 0.00,
    currency VARCHAR(3) DEFAULT 'USD',
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Wallet Transactions Table

```sql
CREATE TABLE wallet_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    wallet_id UUID REFERENCES wallets(id) ON DELETE CASCADE,
    transaction_type VARCHAR(20) NOT NULL, -- 'CREDIT', 'DEBIT'
    amount DECIMAL(15,2) NOT NULL,
    balance_before DECIMAL(15,2) NOT NULL,
    balance_after DECIMAL(15,2) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Configuration

The service is configured via `application.yml`:

```yaml
server:
  port: 8082

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/wallet_db
    username: riyada_user
    password: riyada_password

jwt:
  secret: riyada-wallet-service-secret-key-2024
  expiration: 86400000 # 24 hours
```

## Running the Service

### Prerequisites

- Java 17+
- Maven 3.6+
- PostgreSQL running with wallet_db database

### Start the Service

```bash
cd wallet-service
mvn spring-boot:run
```

The service will start on port 8082.

### Docker Support

The service can be run in Docker as part of the complete system:

```bash
docker-compose up wallet-service
```

## Integration

### With User Service

- Uses Feign client to communicate with User Service
- Validates user existence before wallet operations
- Auto-creates wallets on user registration

### With Frontend

- Provides REST API endpoints for wallet operations
- Supports JWT authentication
- Returns consistent JSON responses

## Development

### Project Structure

```
src/
├── main/
│   ├── java/com/riyada/walletservice/
│   │   ├── config/          # Security and JWT configuration
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data transfer objects
│   │   ├── entity/         # JPA entities
│   │   ├── repository/     # Data repositories
│   │   ├── service/        # Business logic
│   │   ├── client/         # Feign clients
│   │   └── util/           # Utility classes
│   └── resources/
│       └── application.yml # Configuration
└── test/                   # Test classes
```

### Testing

```bash
mvn test
```

## Security

- All endpoints (except health check and wallet creation) require JWT authentication
- Input validation on all DTOs
- SQL injection prevention through JPA
- CORS configuration for frontend integration

## Monitoring

- Health check endpoint: `/api/wallets/health`
- Actuator endpoints available at `/actuator/`
- Comprehensive logging with configurable levels
