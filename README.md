# 🍕 Food Delivery System - Microservices Architecture

A complete food delivery platform built with Spring Boot microservices, Kafka event streaming, and service discovery.

## 📋 Table of Contents
- [Overview](#overview)
- [Architecture](#architecture)
- [Services](#services)
- [Quick Start](#quick-start)
- [Deployment Options](#deployment-options)
- [Documentation](#documentation)
- [Technology Stack](#technology-stack)

---

## 🎯 Overview

This is a production-ready food delivery system implementing microservices architecture with:
- **6 Independent Microservices**
- **Event-Driven Communication** via Kafka
- **Service Discovery** with Eureka
- **API Gateway** for unified access
- **Multiple Deployment Options** (Local, Hybrid, Full Docker)

---

## 🏗️ Architecture

```
API Gateway (9090) → Eureka (8761) → Microservices
                                      ↓
                                    Kafka (9098)
                                      ↓
                                    MySQL (3306)
```

### Services Overview

| Service | Port | Purpose |
|---------|------|---------|
| User Service | 8000 | Authentication & user management |
| Restaurant Service | 8082 | Restaurant & menu management |
| Order Service | 8084 | Order lifecycle management |
| Payment Service | 8085 | Payment processing (Razorpay) |
| Delivery Service | 8083 | Delivery tracking & agent management |
| Notification Service | 8086 | Event-driven notifications |
| API Gateway | 9090 | Request routing & load balancing |
| Eureka Server | 8761 | Service discovery |

---

## 🚀 Quick Start

### Choose Your Deployment Option:

#### Option 1: Fully Local (No Docker) - For Development
**Best if:** You have MySQL and Kafka running locally

See [DEPLOYMENT_OPTIONS.md](DEPLOYMENT_OPTIONS.md) for detailed steps.

#### Option 2: Hybrid (Local MySQL/Kafka + Docker Services) - ✅ RECOMMENDED
**Best if:** You have MySQL and Kafka locally but want services in Docker

```bash
# 1. Ensure MySQL is running on localhost:3306
# 2. Ensure Kafka is running on localhost:9098
# 3. Create databases and topics (see DEPLOYMENT_OPTIONS.md)

# 4. Start services
docker-compose -f docker-compose-local.yml up -d

# 5. Access services
# - API Gateway: http://localhost:9090
# - Eureka Dashboard: http://localhost:8761
```

#### Option 3: Full Docker - For Clean Environment
**Best if:** You want everything in Docker

```bash
# Start everything (MySQL, Kafka, and all services)
docker-compose up -d

# Access services
# - API Gateway: http://localhost:9090
# - Eureka Dashboard: http://localhost:8761
```

---

## 📚 Deployment Options

We provide **3 deployment options** to match your setup:

| Option | MySQL | Kafka | Services | File to Use |
|--------|-------|-------|----------|-------------|
| **Fully Local** | Local | Local | Local (Maven) | Manual startup |
| **Hybrid** ✅ | Local | Local | Docker | `docker-compose-local.yml` |
| **Full Docker** | Docker | Docker | Docker | `docker-compose.yml` |

**See [DEPLOYMENT_OPTIONS.md](DEPLOYMENT_OPTIONS.md) for complete guide.**

---

## 📚 Documentation

### Integration Documentation
- **[Deployment Options](DEPLOYMENT_OPTIONS.md)** - Choose your deployment method
- **[Kafka KRaft Setup](KAFKA_KRAFT_SETUP.md)** - Kafka configuration guide
- **[Integration Phase 1 Summary](INTEGRATION_PHASE1_SUMMARY.md)** - Infrastructure standardization
- **[Integration Phase 2 Summary](INTEGRATION_PHASE2_SUMMARY.md)** - Kafka topic architecture
- **[Integration Phase 3 Summary](INTEGRATION_PHASE3_SUMMARY.md)** - API Gateway & orchestration
- **[Verification Report](INTEGRATION_VERIFICATION_REPORT.md)** - Complete system verification

### API Documentation
- **[API Endpoints Documentation](API_ENDPOINTS_DOCUMENTATION.md)** - Complete API reference

---

## 🔄 Event Flow

### User Registration Flow
```
User Registration → USER_REGISTERED event → Notification Service → Welcome Email
```

### Order Creation Flow
```
Order Created → ORDER_CREATED event → Restaurant + Delivery + Notification Services
              ↓
Payment Success → PAYMENT_SUCCESS event → Order Service (CONFIRMED)
              ↓
Order Confirmed → ORDER_CONFIRMED event → Delivery Service (Assign Agent)
              ↓
Delivery Update → DELIVERY_PICKED_UP event → Notification Service → Customer SMS
```

---

## 🛠️ Technology Stack

### Backend
- **Java 17 & 21**
- **Spring Boot 3.x**
- **Spring Cloud** (Gateway, Eureka, Config)
- **Spring Data JPA**
- **Spring Security** with JWT
- **Spring Kafka**

### Infrastructure
- **MySQL 8.0** - Database
- **Apache Kafka** - Event streaming
- **Zookeeper** - Kafka coordination
- **Netflix Eureka** - Service discovery
- **Docker & Docker Compose** - Containerization

### Payment Integration
- **Razorpay** - Payment gateway

---

## 🧪 Testing

### Health Checks
```bash
# Check all services via Eureka
curl http://localhost:8761

# Check API Gateway
curl http://localhost:9090/actuator/health

# Check individual services
curl http://localhost:8000/actuator/health  # User Service
curl http://localhost:8082/actuator/health  # Restaurant Service
curl http://localhost:8084/actuator/health  # Order Service
curl http://localhost:8085/actuator/health  # Payment Service
curl http://localhost:8083/delivery-service/actuator/health  # Delivery Service
curl http://localhost:8086/actuator/health  # Notification Service
```

### API Testing
```bash
# Register a user
curl -X POST http://localhost:9090/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john@example.com",
    "phone": "1234567890",
    "password": "password123",
    "confirmPassword": "password123"
  }'

# Get restaurants
curl http://localhost:9090/api/restaurants

# Create an order
curl -X POST http://localhost:9090/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "customerId": 1,
    "restaurantId": 1,
    "deliveryAddress": "123 Main St",
    "items": [{"menuItemId": 101, "quantity": 2}]
  }'
```

---

## 📊 Monitoring

### Eureka Dashboard
Access service registry and health status:
```
http://localhost:8761
```

### API Gateway Routes
View all configured routes:
```
http://localhost:9090/actuator/gateway/routes
```

### Kafka Topics
```bash
# List all topics
kafka-topics.sh --list --bootstrap-server localhost:9098

# Expected topics:
# - user-events
# - restaurant-events
# - order-events
# - payment-events
# - delivery-events
# - notification-events
```

---

## 🔧 Configuration

### Environment Variables

Each service can be configured via environment variables:

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/dbname
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root

# Kafka
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9098

# Eureka
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://localhost:8761/eureka/

# Payment (Razorpay)
RAZORPAY_KEY_ID=your_key_id
RAZORPAY_KEY_SECRET=your_key_secret
```

---

## 🐛 Troubleshooting

### Services Not Registering with Eureka
- Ensure Eureka Server is running on port 8761
- Check `eureka.client.enabled=true` in service configuration
- Verify network connectivity

### Kafka Connection Issues
- Ensure Kafka is running on port 9098 (KRaft mode)
- Verify Kafka is in KRaft mode (no Zookeeper dependency)
- Verify topic creation with: `kafka-topics.sh --list --bootstrap-server localhost:9098`

### Database Connection Failures
- Ensure MySQL is running on port 3306
- Verify database credentials
- Check if databases are created (see `docker/mysql/init.sql`)

### Port Conflicts
```bash
# Windows - Check port usage
netstat -ano | findstr :8000

# Kill process if needed
taskkill /PID <process_id> /F
```

---

## 📈 Project Structure

```
.
├── user-service-backend/          # User authentication service
├── restaurant-service/            # Restaurant management service
├── Food-deli-order_service/       # Order management service
├── payments-service/              # Payment processing service
│   ├── payment/demo/             # Payment service implementation
│   ├── gateway/                  # API Gateway
│   └── eureka/                   # Eureka Server
├── delivery-service/              # Delivery tracking service
├── notification-service/          # Notification service
├── docker/                        # Docker configuration
│   └── mysql/init.sql            # Database initialization
├── docker-compose.yml             # Docker Compose orchestration
└── README.md                      # This file
```

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

---

## 📄 License

This project is licensed under the MIT License.

---

## 👥 Team

Developed as part of a microservices architecture learning project.

---

## 🎉 Acknowledgments

- Spring Boot & Spring Cloud teams
- Apache Kafka community
- Netflix OSS (Eureka)
- Razorpay for payment integration

---

**Status:** ✅ Production Ready  
**Last Updated:** February 12, 2026  
**Version:** 1.0.0
