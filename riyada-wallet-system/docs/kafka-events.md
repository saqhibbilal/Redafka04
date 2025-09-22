# Kafka Events Schema

## Overview

Event-driven communication between microservices using Kafka topics.

## Event Topics

### User Service Events

- **user-events** - General user events
- **user-created** - New user registration
- **user-updated** - User profile updates

### Wallet Service Events

- **wallet-events** - General wallet events
- **wallet-created** - New wallet creation
- **balance-updated** - Wallet balance changes

### Payment Service Events

- **payment-events** - General payment events
- **payment-initiated** - Payment request started
- **payment-completed** - Payment successfully processed
- **payment-failed** - Payment processing failed

### Ledger Service Events

- **ledger-events** - General ledger events
- **transaction-recorded** - Transaction logged in ledger

### Notification Service Events

- **notification-events** - General notification events
- **notification-sent** - Notification delivered to user

## Event Structure

All events follow this basic structure:

```json
{
  "eventId": "uuid",
  "eventType": "string",
  "timestamp": "ISO-8601",
  "source": "service-name",
  "data": {
    // Event-specific data
  }
}
```

## Topic Configuration

- **Partitions:** 3 (for parallel processing)
- **Replication Factor:** 1 (single broker setup)
- **Retention:** Default (7 days)
