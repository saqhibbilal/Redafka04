# Riyada Database Schema

## Overview

Each microservice has its own database to ensure data isolation and independent scaling.

## Database Structure

### 1. User Service (`user_db`)

- **users** - Core user information and authentication
- **user_profiles** - Extended user profile data
- **auth_tokens** - JWT token management

### 2. Wallet Service (`wallet_db`)

- **wallets** - User wallet information and balances
- **wallet_transactions** - Internal wallet transaction history

### 3. Payment Service (`payment_db`)

- **payments** - Payment requests and processing
- **payment_methods** - User payment method preferences

### 4. Ledger Service (`ledger_db`)

- **transactions** - Complete transaction history
- **audit_logs** - Audit trail for compliance
- **categories** - Transaction categorization

### 5. Notification Service (`notification_db`)

- **notifications** - User notifications
- **user_preferences** - Notification preferences

## Key Features

- UUID primary keys for all tables
- Timestamps for audit trails
- Proper foreign key relationships
- JSONB for flexible audit logging
- Decimal precision for financial amounts

## Connection Details

- **Host:** localhost:5432
- **User:** riyada_user
- **Password:** riyada_password
- **Main DB:** riyada
