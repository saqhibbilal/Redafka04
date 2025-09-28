# Ledger Service

The Ledger Service is responsible for maintaining a complete transaction history and audit trail for the Riyada Digital Wallet System. It provides immutable transaction records, comprehensive audit logging, and financial reporting capabilities.

## Features

- **Transaction Recording**: Records all payment transactions with complete details
- **Audit Trail**: Maintains immutable audit logs for compliance and tracking
- **Financial Reporting**: Provides transaction summaries and analytics
- **Transaction Search**: Advanced filtering and search capabilities
- **Double-Entry Principles**: Follows accounting best practices

## API Endpoints

### Transaction Management

- `POST /api/ledger/record-transaction` - Record a new transaction
- `PUT /api/ledger/transactions/{id}/status` - Update transaction status
- `GET /api/ledger/transactions` - Get user transactions (paginated)
- `GET /api/ledger/transactions/{id}` - Get transaction by ID
- `GET /api/ledger/transactions/payment/{paymentId}` - Get transactions by payment ID
- `GET /api/ledger/transactions/recent` - Get recent transactions

### Audit & Reporting

- `GET /api/ledger/transactions/{id}/audit-trail` - Get audit trail for transaction
- `GET /api/ledger/reports/summary` - Get financial summary for user
- `GET /api/ledger/search` - Search transactions with filters

## Database Schema

### Transactions Table

- `id` (UUID) - Primary key
- `payment_id` (UUID) - Reference to payment
- `sender_user_id` (UUID) - Sender user ID
- `receiver_user_id` (UUID) - Receiver user ID
- `amount` (DECIMAL) - Transaction amount
- `currency` (VARCHAR) - Currency code
- `transaction_type` (ENUM) - TRANSFER, DEPOSIT, WITHDRAWAL, REFUND
- `status` (ENUM) - PENDING, COMPLETED, FAILED, CANCELLED
- `description` (TEXT) - Transaction description
- `created_at` (TIMESTAMP) - Creation timestamp

### Audit Logs Table

- `id` (UUID) - Primary key
- `transaction_id` (UUID) - Reference to transaction
- `action` (VARCHAR) - Action performed
- `old_values` (JSONB) - Previous values
- `new_values` (JSONB) - New values
- `user_id` (UUID) - User who performed action
- `created_at` (TIMESTAMP) - Creation timestamp

### Categories Table

- `id` (UUID) - Primary key
- `name` (VARCHAR) - Category name
- `description` (TEXT) - Category description
- `is_active` (BOOLEAN) - Active status
- `created_at` (TIMESTAMP) - Creation timestamp

## Configuration

- **Port**: 8084
- **Database**: ledger_db
- **JWT Authentication**: Required for all endpoints except record-transaction
- **Timezone**: UTC

## Integration

The Ledger Service integrates with:

- **Payment Service**: Receives transaction records when payments are processed
- **User Service**: Validates user IDs for transaction participants
- **Frontend**: Provides transaction history and reporting data

## Security

- JWT-based authentication for user endpoints
- Service-to-service authentication for internal calls
- Audit logging for all transaction modifications
- Input validation and sanitization

## Running the Service

```bash
# Using Docker Compose (recommended)
docker-compose up ledger-service

# Using Maven
./mvnw spring-boot:run
```

## Testing

```bash
# Run tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```
