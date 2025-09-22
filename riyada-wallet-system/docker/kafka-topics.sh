#!/bin/bash

# Kafka Topics Setup Script for Riyada Wallet System
# This script creates all required Kafka topics for our microservices

echo "Creating Kafka topics for Riyada Wallet System..."

# User Service Topics
kafka-topics --create --topic user-events --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics --create --topic user-created --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics --create --topic user-updated --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

# Wallet Service Topics
kafka-topics --create --topic wallet-events --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics --create --topic wallet-created --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics --create --topic balance-updated --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

# Payment Service Topics
kafka-topics --create --topic payment-events --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics --create --topic payment-initiated --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics --create --topic payment-completed --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics --create --topic payment-failed --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

# Ledger Service Topics
kafka-topics --create --topic ledger-events --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics --create --topic transaction-recorded --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

# Notification Service Topics
kafka-topics --create --topic notification-events --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics --create --topic notification-sent --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

echo "All Kafka topics created successfully!"
echo "Listing all topics:"
kafka-topics --list --bootstrap-server localhost:9092
