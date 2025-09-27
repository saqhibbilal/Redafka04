# Payment Service

The Payment Service is a microservice in the Riyada Wallet System responsible for processing payments and transfers between users.

## Features

- **Payment Processing**: Handle peer-to-peer money transfers
- **Payment Status Tracking**: Track payment status (PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED)
- **Transaction History**: Maintain payment history for users
- **Integration**: Integrates with User Service and Wallet Service

## API Endpoints

### Payment Operations

- `POST /api/payments/transfer` - Initiate a payment transfer
- `GET /api/payments/{paymentId}` - Get payment details
- `GET /api/payments/status/{referenceId}` - Get payment status by reference ID
- `GET /api/payments/user/{userId}` - Get user's payment history

### Health Check

- `GET /api/payments/health` - Service health check

## Database

The service uses PostgreSQL with the following main table:

- `payments` - Stores payment transactions

## Configuration

The service is configured via `application.yml`:

- Port: 8083
- Database: payment_db
- JWT authentication
- External service URLs

## Dependencies

- Spring Boot 3.2.0
- Spring Data JPA
- Spring Security
- PostgreSQL
- JWT for authentication

## Running the Service

### Using Docker

```bash
docker-compose up payment-service
```

### Using Maven

```bash
./mvnw spring-boot:run
```

## Integration

The Payment Service integrates with:

- **User Service** (port 8081): For user validation and email lookup
- **Wallet Service** (port 8082): For balance management and transaction processing
