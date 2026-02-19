# 🧪 Food Delivery System - Testing Guide

This guide provides step-by-step instructions to test all microservices and verify the complete system functionality.

---

## 📋 Prerequisites

Before testing, ensure:

1. ✅ All services are running (use `RUN_LOCALLY.bat`)
2. ✅ MySQL is running on port 3306 with password `12345678`
3. ✅ Kafka is running on port 9098
4. ✅ Eureka Server is accessible at http://localhost:8761

---

## 🚀 Quick Testing Options

### Option 1: Automated Script (Recommended)
```bash
# Run basic health checks
TEST_ENDPOINTS.bat

# Run full integration test
INTEGRATION_TEST.bat
```

### Option 2: Postman Collection
1. Import `Food_Delivery_Postman_Collection.json` into Postman
2. Run the collection in order (1 → 7)
3. Variables are auto-populated between requests

### Option 3: Manual cURL Commands
Follow the detailed steps below

---

## 🔍 Step-by-Step Testing

### Step 1: Verify All Services Are Running

Check Eureka Dashboard:
```
http://localhost:8761
```

You should see 6 services registered:
- USER-SERVICE
- RESTAURANT-SERVICE
- ORDER-SERVICE
- PAYMENT-SERVICE
- DELIVERY-SERVICE
- NOTIFICATION-SERVICE

### Step 2: Test Health Endpoints

```bash
# Eureka Server
curl http://localhost:8761/actuator/health

# API Gateway
curl http://localhost:9090/actuator/health

# User Service
curl http://localhost:8000/actuator/health

# Restaurant Service
curl http://localhost:8082/actuator/health

# Order Service
curl http://localhost:8084/actuator/health

# Payment Service
curl http://localhost:8085/actuator/health

# Delivery Service
curl http://localhost:8083/delivery-service/actuator/health

# Notification Service
curl http://localhost:8086/actuator/health
```

**Expected Response:** All should return `{"status":"UP"}`

---

## 🔄 Complete User Flow Test

### Test 1: User Registration & Authentication

#### 1.1 Register a New User
```bash
curl -X POST http://localhost:9090/api/auth/signup ^
  -H "Content-Type: application/json" ^
  -d "{\"fullName\":\"John Doe\",\"email\":\"john@example.com\",\"phone\":\"1234567890\",\"password\":\"password123\",\"confirmPassword\":\"password123\"}"
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": 1,
  "fullName": "John Doe",
  "email": "john@example.com",
  "phone": "1234567890"
}
```

**✅ Verification:**
- User created in `fooddelivery.users` table
- JWT token returned
- Kafka event published to `user-events` topic
- Notification service receives event (check logs)

#### 1.2 Login User
```bash
curl -X POST http://localhost:9090/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"emailOrPhone\":\"john@example.com\",\"password\":\"password123\"}"
```

**Expected Response:** Same as registration (with new token)

---

### Test 2: Restaurant Management

#### 2.1 Register a Restaurant
```bash
curl -X POST http://localhost:9090/api/restaurants/register ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Pizza Palace\",\"email\":\"owner@pizzapalace.com\",\"password\":\"password123\",\"address\":\"123 Main St\",\"phone\":\"9876543210\"}"
```

**Expected Response:**
```json
{
  "id": 1,
  "name": "Pizza Palace",
  "email": "owner@pizzapalace.com",
  "address": "123 Main St",
  "phone": "9876543210",
  "isActive": true
}
```

**✅ Verification:**
- Restaurant created in `restaurant_db.restaurants` table
- Kafka event published to `restaurant-events` topic

#### 2.2 Get All Restaurants
```bash
curl http://localhost:9090/api/restaurants
```

**Expected Response:** Array of restaurants

#### 2.3 Add Menu Item
```bash
curl -X POST http://localhost:9090/api/menu/1 ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Margherita Pizza\",\"description\":\"Classic cheese pizza\",\"price\":12.99,\"category\":\"Pizza\",\"available\":true}"
```

**Expected Response:**
```json
{
  "id": 1,
  "name": "Margherita Pizza",
  "description": "Classic cheese pizza",
  "price": 12.99,
  "category": "Pizza",
  "available": true,
  "restaurantId": 1
}
```

**✅ Verification:**
- Menu item created in `restaurant_db.menu_items` table

#### 2.4 Get Restaurant Menu
```bash
curl http://localhost:9090/api/menu/1
```

**Expected Response:** Array of menu items for restaurant ID 1

---

### Test 3: Order Management

#### 3.1 Create Order
```bash
curl -X POST http://localhost:9090/api/orders ^
  -H "Content-Type: application/json" ^
  -d "{\"customerId\":1,\"restaurantId\":1,\"deliveryAddress\":\"456 Oak Ave, Apt 2B\",\"specialInstructions\":\"Ring doorbell twice\",\"items\":[{\"menuItemId\":1,\"quantity\":2}]}"
```

**Expected Response:**
```json
{
  "id": 1,
  "customerId": 1,
  "restaurantId": 1,
  "orderStatus": "PENDING",
  "paymentStatus": "PENDING",
  "totalAmount": 25.98,
  "deliveryAddress": "456 Oak Ave, Apt 2B",
  "specialInstructions": "Ring doorbell twice",
  "items": [
    {
      "menuItemId": 1,
      "quantity": 2,
      "price": 12.99
    }
  ],
  "createdAt": "2026-02-12T10:30:00",
  "updatedAt": "2026-02-12T10:30:00"
}
```

**✅ Verification:**
- Order created in `order_db.orders` table
- Order items created in `order_db.order_items` table
- Kafka event published to `order-events` topic
- Restaurant service receives order event (check logs)
- Notification service sends order confirmation (check logs)

#### 3.2 Get Order by ID
```bash
curl http://localhost:9090/api/orders/1
```

#### 3.3 Get Customer Orders
```bash
curl http://localhost:9090/api/orders/customer/1
```

#### 3.4 Update Order Status
```bash
curl -X PUT http://localhost:9090/api/orders/1/status ^
  -H "Content-Type: application/json" ^
  -d "{\"status\":\"CONFIRMED\"}"
```

**✅ Verification:**
- Order status updated in database
- Kafka event published with new status
- Notification service sends status update (check logs)

---

### Test 4: Payment Processing

#### 4.1 Create Razorpay Order
```bash
curl -X POST http://localhost:9090/api/payments/razorpay/order ^
  -H "Content-Type: application/json" ^
  -d "{\"orderId\":\"1\",\"amount\":25.98,\"paymentMethod\":\"RAZORPAY\"}"
```

**Expected Response:**
```json
{
  "appOrderId": "1",
  "razorpayOrderId": "order_xyz123",
  "keyId": "rzp_test_xxxxx",
  "amount": 2598,
  "currency": "INR",
  "status": "CREATED"
}
```

**✅ Verification:**
- Payment record created in `payment_db.payments` table
- Razorpay order ID generated

#### 4.2 Verify Payment (Simulated)
```bash
curl -X POST http://localhost:9090/api/payments/razorpay/verify ^
  -H "Content-Type: application/json" ^
  -d "{\"orderId\":\"1\",\"razorpayOrderId\":\"order_xyz123\",\"razorpayPaymentId\":\"pay_abc456\",\"razorpaySignature\":\"test_signature\"}"
```

**Expected Response:**
```json
{
  "verified": true,
  "status": "VERIFIED",
  "message": "Payment signature verified"
}
```

**✅ Verification:**
- Payment status updated to COMPLETED
- Kafka event published to `payment-events` topic
- Order service receives payment event and updates order (check logs)
- Notification service sends payment confirmation (check logs)

#### 4.3 Get Payment by Order ID
```bash
curl http://localhost:9090/api/payments/order/1
```

---

### Test 5: Delivery Management

#### 5.1 Register Delivery Agent
```bash
curl -X POST http://localhost:9090/delivery-service/api/agents/register ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Mike Driver\",\"email\":\"mike@delivery.com\",\"phone\":\"5551234567\",\"password\":\"password123\",\"vehicleType\":\"BIKE\",\"vehicleNumber\":\"ABC-1234\",\"licenseNumber\":\"DL123456\"}"
```

**Expected Response:**
```json
{
  "id": 1,
  "name": "Mike Driver",
  "email": "mike@delivery.com",
  "phone": "5551234567",
  "vehicleType": "BIKE",
  "vehicleNumber": "ABC-1234",
  "status": "AVAILABLE"
}
```

**✅ Verification:**
- Agent created in `delivery_db.delivery_agents` table

#### 5.2 Agent Login
```bash
curl -X POST http://localhost:9090/delivery-service/api/agents/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"mike@delivery.com\",\"password\":\"password123\"}"
```

#### 5.3 Get Available Agents
```bash
curl http://localhost:9090/delivery-service/api/agents/available
```

#### 5.4 Check Delivery Status
```bash
curl http://localhost:9090/delivery-service/api/deliveries/order/1
```

**Expected Response:**
```json
{
  "id": 1,
  "orderId": 1,
  "agentId": 1,
  "status": "ASSIGNED",
  "pickupAddress": "123 Main St",
  "deliveryAddress": "456 Oak Ave, Apt 2B",
  "assignedAt": "2026-02-12T10:35:00"
}
```

**✅ Verification:**
- Delivery created automatically when order is confirmed
- Agent assigned based on availability
- Kafka event published to `delivery-events` topic

#### 5.5 Update Agent Location
```bash
curl -X PUT http://localhost:9090/delivery-service/api/agents/1/location ^
  -H "Content-Type: application/json" ^
  -d "{\"latitude\":40.7128,\"longitude\":-74.0060}"
```

#### 5.6 Update Delivery Status
```bash
curl -X PUT http://localhost:9090/delivery-service/api/deliveries/1/status ^
  -H "Content-Type: application/json" ^
  -d "{\"status\":\"PICKED_UP\",\"remarks\":\"Package picked up from restaurant\"}"
```

**✅ Verification:**
- Delivery status updated
- Kafka event published
- Notification service sends delivery update (check logs)

#### 5.7 Track Delivery
```bash
curl http://localhost:9090/delivery-service/api/deliveries/1/track
```

---

## 🔔 Test 6: Notification Service (Event-Driven)

The Notification Service doesn't have REST endpoints - it listens to Kafka events.

**Check Notification Service Logs:**

Look for these log messages:
```
✅ User registered notification: john@example.com
✅ Order created notification: Order #1
✅ Payment completed notification: Order #1
✅ Delivery status update: Order #1 - PICKED_UP
```

**✅ Verification:**
- Check notification service console output
- Verify Kafka consumers are processing events
- All 4 consumers should be active:
  - UserEventConsumer
  - OrderEventConsumer
  - PaymentEventConsumer (via kafkaconsumer.java)
  - DeliveryEventConsumer

---

## 📊 Kafka Event Verification

### Check Kafka Topics

If you have Kafka CLI tools installed:

```bash
# List all topics
kafka-topics.bat --list --bootstrap-server localhost:9098

# Expected topics:
# - user-events
# - restaurant-events
# - order-events
# - payment-events
# - delivery-events
# - notification-events

# Consume messages from a topic
kafka-console-consumer.bat --bootstrap-server localhost:9098 --topic order-events --from-beginning
```

---

## 🗄️ Database Verification

### Check MySQL Databases

```sql
-- Connect to MySQL
mysql -u root -p12345678

-- Check all databases
SHOW DATABASES;

-- Expected databases:
-- - fooddelivery (User Service)
-- - restaurant_db (Restaurant Service)
-- - order_db (Order Service)
-- - payment_db (Payment Service)
-- - delivery_db (Delivery Service)

-- Verify user data
USE fooddelivery;
SELECT * FROM users;

-- Verify restaurant data
USE restaurant_db;
SELECT * FROM restaurants;
SELECT * FROM menu_items;

-- Verify order data
USE order_db;
SELECT * FROM orders;
SELECT * FROM order_items;

-- Verify payment data
USE payment_db;
SELECT * FROM payments;

-- Verify delivery data
USE delivery_db;
SELECT * FROM delivery_agents;
SELECT * FROM deliveries;
SELECT * FROM delivery_tracking;
```

---

## ✅ Complete Integration Test Checklist

- [ ] All 6 services registered in Eureka
- [ ] All health endpoints return UP status
- [ ] User registration successful
- [ ] User login returns JWT token
- [ ] Restaurant registration successful
- [ ] Menu items can be added
- [ ] Order creation successful
- [ ] Order appears in database
- [ ] Kafka order event published
- [ ] Payment order created
- [ ] Payment verification successful
- [ ] Kafka payment event published
- [ ] Order service receives payment event
- [ ] Delivery agent registration successful
- [ ] Delivery automatically assigned
- [ ] Delivery status can be updated
- [ ] Kafka delivery event published
- [ ] Notification service logs show all events received
- [ ] All database tables populated correctly

---

## 🐛 Troubleshooting

### Service Not Registered in Eureka
- Check service logs for connection errors
- Verify `eureka.client.enabled=true` in configuration
- Ensure Eureka server is running on port 8761

### Kafka Events Not Received
- Check Kafka is running on port 9098
- Verify topic names match across services
- Check consumer group IDs are unique
- Look for Kafka connection errors in logs

### Database Connection Errors
- Verify MySQL is running on port 3306
- Check password is `12345678`
- Ensure databases are created (use `docker/mysql/init.sql`)

### JWT Token Issues
- Ensure token is included in Authorization header
- Format: `Authorization: Bearer <token>`
- Check token hasn't expired (24 hours validity)

### Gateway Routing Issues
- Check gateway routes: `curl http://localhost:9090/actuator/gateway/routes`
- Verify service names match in gateway configuration
- Ensure services are registered in Eureka

---

## 📈 Performance Testing

### Load Testing with Apache Bench (Optional)

```bash
# Test user registration endpoint
ab -n 100 -c 10 -p user_data.json -T application/json http://localhost:9090/api/auth/signup

# Test restaurant listing
ab -n 1000 -c 50 http://localhost:9090/api/restaurants
```

---

## 📝 Test Results Documentation

After testing, document:

1. **Service Status:** All services UP/DOWN
2. **API Response Times:** Average response time per endpoint
3. **Kafka Event Flow:** Events published and consumed successfully
4. **Database State:** All tables populated correctly
5. **Error Logs:** Any errors encountered during testing
6. **Integration Issues:** Any cross-service communication problems

---

## 🎯 Next Steps

After successful testing:

1. ✅ Document any bugs or issues found
2. ✅ Create automated test scripts
3. ✅ Set up monitoring and logging
4. ✅ Configure production environment
5. ✅ Implement CI/CD pipeline
6. ✅ Add frontend integration

---

**Last Updated:** February 12, 2026  
**Version:** 1.0.0
