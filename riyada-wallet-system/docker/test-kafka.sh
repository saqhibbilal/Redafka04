#!/bin/bash

# Kafka Test Script for Riyada Wallet System
# This script tests basic Kafka functionality

echo "Testing Kafka connectivity..."

# Test 1: List all topics
echo "1. Listing all topics:"
kafka-topics --list --bootstrap-server localhost:9092

# Test 2: Send a test message
echo "2. Sending test message to user-events topic:"
echo '{"eventId":"test-123","eventType":"TEST","timestamp":"2024-01-01T00:00:00Z","source":"test","data":{"message":"Hello Kafka!"}}' | kafka-console-producer --topic user-events --bootstrap-server localhost:9092

# Test 3: Consume the test message
echo "3. Consuming test message (timeout after 10 seconds):"
timeout 10s kafka-console-consumer --topic user-events --bootstrap-server localhost:9092 --from-beginning

echo "Kafka test completed!"
