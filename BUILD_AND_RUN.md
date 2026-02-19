# 🚀 Build and Run Guide

## Problem: Docker Build Network Issues

If you're getting "Network is unreachable" errors during Docker build, it means Docker can't download Maven dependencies.

---

## ✅ Solution: Build Locally, Then Run in Docker

### Step 1: Build All Services Locally

```bash
# Build User Service
cd user-service-backend
mvn clean package -DskipTests
cd ..

# Build Restaurant Service
cd restaurant-service
mvn clean package -DskipTests
cd ..

# Build Order Service
cd Food-deli-order_service
mvn clean package -DskipTests
cd ..

# Build Payment Service
cd payments-service/payment/demo
mvn clean package -DskipTests
cd ../../..

# Build Delivery Service
cd delivery-service
mvn clean package -DskipTests
cd ..

# Build Notification Service
cd notification-service/notification
mvn clean package -DskipTests
cd ../..

# Build Eureka Server
cd payments-service/eureka
mvn clean package -DskipTests
cd ../..

# Build API Gateway
cd payments-service/gateway
mvn clean package -DskipTests
cd ../..
```

### Step 2: Update Dockerfiles to Use Pre-built JARs

I'll create simplified Dockerfiles that just copy the JAR files.

### Step 3: Run Docker Compose

```bash
docker-compose -f docker-compose-local.yml up -d
```

---

## 🔄 Alternative: Run Everything Locally (No Docker)

If Docker continues to have issues, run everything locally:

### Prerequisites
- MySQL running on localhost:3306
- Kafka running on localhost:9098
- Databases created
- Kafka topics created

### Start Services (Open 8 terminals)

**Terminal 1 - Eureka:**
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

---

## 🐛 Troubleshooting Docker Network Issues

### Issue: Maven Can't Download Dependencies

**Cause:** Docker network configuration or firewall blocking Maven Central

**Solutions:**

1. **Check Docker Network Settings:**
```bash
docker network ls
docker network inspect bridge
```

2. **Use Docker Host Network (Windows):**
Add to docker-compose:
```yaml
network_mode: "host"
```

3. **Configure Maven Proxy (if behind corporate firewall):**
Create `settings.xml` in each service and configure proxy.

4. **Use Local Maven Repository:**
Mount your local `.m2` folder:
```yaml
volumes:
  - ~/.m2:/root/.m2
```

---

## ✅ Recommended Approach for You

Since you have network issues with Docker, I recommend:

**Option A: Run Everything Locally (Simplest)**
- No Docker needed for services
- Just use your local MySQL and Kafka
- Run each service with `mvn spring-boot:run`

**Option B: Build Locally + Simple Docker**
- Build JARs locally with Maven
- Use simplified Dockerfiles that just copy JARs
- Run with Docker Compose

Let me know which approach you prefer, and I'll help you set it up!
