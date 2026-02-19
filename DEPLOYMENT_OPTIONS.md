# 🚀 Deployment Options Guide

## Overview

You have multiple ways to run the Food Delivery System based on your setup:

---

## Option 1: Fully Local (No Docker) ✅ RECOMMENDED FOR DEVELOPMENT

**When to use:** You already have MySQL and Kafka running locally

**Prerequisites:**
- ✅ MySQL running on localhost:3306
- ✅ Kafka (KRaft mode) running on localhost:9098
- ✅ Java 17+ and Maven installed

**Steps:**

### 1. Ensure Infrastructure is Running
```bash
# Check MySQL
mysql -u root -p -e "SHOW DATABASES;"

# Check Kafka
kafka-topics.sh --list --bootstrap-server localhost:9098
```

### 2. Create Databases
```sql
CREATE DATABASE IF NOT EXISTS fooddelivery;
CREATE DATABASE IF NOT EXISTS food_db;
CREATE DATABASE IF NOT EXISTS order_db;
CREATE DATABASE IF NOT EXISTS payment_db;
CREATE DATABASE IF NOT EXISTS delivery_db;
```

### 3. Create Kafka Topics
```bash
kafka-topics.sh --create --topic user-events --bootstrap-server localhost:9098 --partitions 3 --replication-factor 1
kafka-topics.sh --create --topic restaurant-events --bootstrap-server localhost:9098 --partitions 3 --replication-factor 1
kafka-topics.sh --create --topic order-events --bootstrap-server localhost:9098 --partitions 3 --replication-factor 1
kafka-topics.sh --create --topic payment-events --bootstrap-server localhost:9098 --partitions 3 --replication-factor 1
kafka-topics.sh --create --topic delivery-events --bootstrap-server localhost:9098 --partitions 3 --replication-factor 1
kafka-topics.sh --create --topic notification-events --bootstrap-server localhost:9098 --partitions 3 --replication-factor 1
```

### 4. Start Services (Open separate terminals)

**Terminal 1 - Eureka Server:**
```bash
cd payments-service/eureka
mvn spring-boot:run
```

**Terminal 2 - User Service:**
```bash
cd user-service-backend
mvn spring-boot:run
```

**Terminal 3 - Restaurant Service:**
```bash
cd restaurant-service
mvn spring-boot:run
```

**Terminal 4 - Order Service:**
```bash
cd Food-deli-order_service
mvn spring-boot:run
```

**Terminal 5 - Payment Service:**
```bash
cd payments-service/payment/demo
mvn spring-boot:run
```

**Terminal 6 - Delivery Service:**
```bash
cd delivery-service
mvn spring-boot:run
```

**Terminal 7 - Notification Service:**
```bash
cd notification-service/notification
mvn spring-boot:run
```

**Terminal 8 - API Gateway:**
```bash
cd payments-service/gateway
mvn spring-boot:run
```

### 5. Verify
```bash
# Check Eureka Dashboard
http://localhost:8761

# Test API Gateway
curl http://localhost:9090/actuator/health
```

---

## Option 2: Hybrid (Local MySQL/Kafka + Docker Services) ✅ RECOMMENDED FOR YOU

**When to use:** You have MySQL and Kafka locally but want services in Docker

**Prerequisites:**
- ✅ MySQL running on localhost:3306
- ✅ Kafka (KRaft mode) running on localhost:9098
- ✅ Docker Desktop installed and running

**Steps:**

### 1. Ensure Infrastructure is Running
```bash
# Check MySQL
mysql -u root -p -e "SHOW DATABASES;"

# Check Kafka
kafka-topics.sh --list --bootstrap-server localhost:9098
```

### 2. Create Databases (if not exists)
```sql
CREATE DATABASE IF NOT EXISTS fooddelivery;
CREATE DATABASE IF NOT EXISTS food_db;
CREATE DATABASE IF NOT EXISTS order_db;
CREATE DATABASE IF NOT EXISTS payment_db;
CREATE DATABASE IF NOT EXISTS delivery_db;
```

### 3. Create Kafka Topics (if not exists)
```bash
# Create all topics at once
for topic in user-events restaurant-events order-events payment-events delivery-events notification-events; do
  kafka-topics.sh --create --topic $topic --bootstrap-server localhost:9098 --partitions 3 --replication-factor 1 2>/dev/null || echo "Topic $topic already exists"
done
```

### 4. Start Services with Docker Compose
```bash
# Use the local infrastructure version
docker-compose -f docker-compose-local.yml up -d

# View logs
docker-compose -f docker-compose-local.yml logs -f

# Stop services
docker-compose -f docker-compose-local.yml down
```

**Note:** This uses `host.docker.internal` to connect to your local MySQL and Kafka from Docker containers.

---

## Option 3: Full Docker (Everything in Docker)

**When to use:** Clean environment, CI/CD, or don't have local MySQL/Kafka

**Prerequisites:**
- ✅ Docker Desktop installed and running

**Steps:**

### 1. Start Everything
```bash
# Start all services including MySQL and Kafka
docker-compose up -d

# View logs
docker-compose logs -f

# Stop everything
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

**Note:** This will pull MySQL and Kafka images from Docker Hub and run them in containers.

---

## Comparison Table

| Feature | Fully Local | Hybrid | Full Docker |
|---------|-------------|--------|-------------|
| MySQL | Local | Local | Docker |
| Kafka | Local | Local | Docker |
| Services | Local (Maven) | Docker | Docker |
| Startup Time | Fast | Medium | Slow (first time) |
| Resource Usage | Low | Medium | High |
| Isolation | None | Partial | Full |
| Best For | Development | Testing | Production/CI |

---

## Troubleshooting

### Issue 1: Docker Can't Connect to Local MySQL/Kafka

**Problem:** Services in Docker can't reach localhost:3306 or localhost:9098

**Solution:** Use `host.docker.internal` instead of `localhost`
```yaml
# In docker-compose-local.yml (already configured)
SPRING_DATASOURCE_URL: jdbc:mysql://host.docker.internal:3306/...
SPRING_KAFKA_BOOTSTRAP_SERVERS: host.docker.internal:9098
```

### Issue 2: Docker Desktop Not Running

**Problem:** `unable to get image` or `cannot connect to Docker daemon`

**Solution:**
1. Start Docker Desktop
2. Wait for it to fully start (whale icon in system tray)
3. Run `docker ps` to verify it's working

### Issue 3: Port Already in Use

**Problem:** Service fails to start due to port conflict

**Solution:**
```bash
# Windows - Find process using port
netstat -ano | findstr :8000

# Kill the process
taskkill /PID <process_id> /F
```

### Issue 4: Kafka Topics Not Found

**Problem:** Services can't publish/consume events

**Solution:**
```bash
# List existing topics
kafka-topics.sh --list --bootstrap-server localhost:9098

# Create missing topics
kafka-topics.sh --create --topic order-events --bootstrap-server localhost:9098 --partitions 3 --replication-factor 1
```

---

## Recommended Setup for Your Case

Based on your setup (MySQL and Kafka already running locally):

### ✅ Best Option: Hybrid Approach

```bash
# 1. Ensure MySQL is running
# 2. Ensure Kafka is running on port 9098
# 3. Create databases and topics (see above)
# 4. Run services in Docker

docker-compose -f docker-compose-local.yml up -d
```

**Why?**
- ✅ Uses your existing MySQL and Kafka
- ✅ No need to pull MySQL/Kafka images
- ✅ Services are isolated in Docker
- ✅ Easy to restart individual services
- ✅ Consistent environment

---

## Quick Commands Reference

### Check Infrastructure
```bash
# MySQL
mysql -u root -p -e "SELECT VERSION();"

# Kafka
kafka-broker-api-versions.sh --bootstrap-server localhost:9098

# Docker
docker ps
```

### Start Services
```bash
# Hybrid (recommended for you)
docker-compose -f docker-compose-local.yml up -d

# Full Docker
docker-compose up -d

# Fully Local
# Start each service with mvn spring-boot:run
```

### Monitor Services
```bash
# Eureka Dashboard
http://localhost:8761

# API Gateway Health
curl http://localhost:9090/actuator/health

# View Docker logs
docker-compose -f docker-compose-local.yml logs -f service-name
```

---

## Summary

**For Your Setup (MySQL + Kafka already running):**

1. ✅ Use `docker-compose-local.yml`
2. ✅ Create databases manually
3. ✅ Create Kafka topics manually
4. ✅ Run: `docker-compose -f docker-compose-local.yml up -d`

This avoids pulling MySQL and Kafka images while still containerizing your microservices!
