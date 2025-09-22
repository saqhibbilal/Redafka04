# Riyada Digital Wallet System

A modern microservices-based digital wallet system with real-time transactions, built for learning and portfolio development.

## 🏗️ Architecture

- **6 Microservices** - User, Wallet, Payment, Ledger, Notification, API Gateway
- **Event-Driven** - Apache Kafka for inter-service communication
- **Real-Time** - WebSocket notifications and live updates
- **Scalable** - Docker containers with Kubernetes deployment

## 🛠️ Technology Stack

### Backend

- **Spring Boot** (Java 17) - Microservices framework
- **PostgreSQL** - Primary database
- **Redis** - Caching layer
- **InfluxDB** - Time-series analytics
- **Apache Kafka** - Message broker

### Frontend

- **React.js** - UI framework
- **Vite** - Build tool
- **shadcn/ui** - Component library
- **Tailwind CSS** - Styling

### Infrastructure

- **Docker & Docker Compose** - Containerization
- **Kubernetes** - Orchestration
- **Spring Cloud Gateway** - API Gateway

## 🚀 Quick Start

### Prerequisites

- Docker Desktop
- Java 17
- Node.js 18+

### 1. Clone the Repository

```bash
git clone <your-repo-url>
cd riyada-wallet-system
```

### 2. Start Infrastructure

```bash
docker-compose up -d
```

### 3. Create Kafka Topics

```bash
chmod +x docker/kafka-topics.sh
./docker/kafka-topics.sh
```

### 4. Start Frontend (Task 1.6+)

```bash
cd frontend
npm install
npm run dev
```

## 📁 Project Structure

```
riyada-wallet-system/
├── user-service/          # User management & authentication
├── wallet-service/        # Digital wallet & balance management
├── payment-service/       # Payment processing & validation
├── ledger-service/        # Transaction history & audit trail
├── notification-service/  # Real-time notifications
├── api-gateway/          # Single entry point & routing
├── frontend/             # React app with shadcn/ui + Tailwind
├── docker/               # Docker configurations & scripts
└── docs/                 # Documentation
```

## 🔧 Development Phases

- **Phase 1:** Project Foundation & Infrastructure + Basic Frontend ✅
- **Phase 2:** Core Microservices + Authentication UI
- **Phase 3:** Advanced Microservices + Enhanced UI
- **Phase 4:** Event-Driven Architecture + Real-Time UI
- **Phase 5:** Analytics & Monitoring + Admin Dashboard
- **Phase 6:** Kubernetes Deployment
- **Phase 7:** Testing, Optimization & Documentation

## 📊 Services & Ports

| Service    | Port | Description           |
| ---------- | ---- | --------------------- |
| PostgreSQL | 5432 | Main database         |
| Redis      | 6379 | Caching layer         |
| InfluxDB   | 8086 | Time-series analytics |
| Zookeeper  | 2181 | Kafka coordination    |
| Kafka      | 9092 | Message broker        |

## 📚 Documentation

- [Database Schema](docs/database-schema.md)
- [Kafka Events](docs/kafka-events.md)

## 🤝 Contributing

This is a learning project. Feel free to fork and experiment!

## 📄 License

MIT License - Feel free to use for learning and portfolio purposes.
