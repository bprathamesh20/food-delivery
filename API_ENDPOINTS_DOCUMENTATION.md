# 📚 Food Delivery System - API Endpoints Documentation

**Base URL (via API Gateway):** `http://localhost:9090`  
**Eureka Dashboard:** `http://localhost:8761`

---

## 🔐 USER SERVICE (Port 8000)

### Authentication Endpoints

#### Register New User
```http
POST /api/auth/signup
Content-Type: application/json

{
  "fullName": "John Doe",
  "email": "john@example.com",
  "phone": "1234567890",
  "password": "password123",
  "confirmPassword": "password123"
}

Response: 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": 1,
  "fullName": "John Doe",
  "email": "john@example.com",
  "phone": "1234567890"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "emailOrPhone": "john@example.com",
  "password": "password123"
}

Response: 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": 1,
  "fullName": "John Doe",
  "email": "john@example.com",
  "phone": "1234567890"
}
```

**Via Gateway:** `http://localhost:9090/api/auth/signup`

---

## 🍽️ RESTAURANT SERVICE (Port 8082)

### Restaurant Management

#### Register Restaurant
```http
POST /api/restaurants/register
Content-Type: application/json

{
  "name": "Pizza Palace",
  "email": "owner@pizzapalace.com",
  "password": "password123",
  "address": "123 Main St",
  "phone": "9876543210"
}
```

#### Get All Restaurants
```http
GET /api/restaurants
```

#### Get Restaurant by ID
```http
GET /api/restaurants/{id}
```

#### Disable Restaurant
```http
PUT /api/restaurants/{id}/disable
```

### Menu Management

#### Add Menu Item
```http
POST /api/menu/{restaurantId}
Content-Type: application/json

{
  "name": "Margherita Pizza",
  "description": "Classic cheese pizza",
  "price": 12.99,
  "category": "Pizza",
  "available": true
}
```

#### Get Restaurant Menu
```http
GET /api/menu/{restaurantId}
```

#### Disable Menu Item
```http
PUT /api/menu/item/{menuItemId}/disable
```

#### Delete Menu Item
```http
DELETE /api/menu/item/{menuItemId}
```

**Via Gateway:** `http://localhost:9090/api/restaurants`

---

## 📦 ORDER SERVICE (Port 8084)

### Order Management

#### Create Order
```http
POST /api/orders
Content-Type: application/json

{
  "customerId": 1,
  "restaurantId": 1,
  "deliveryAddress": "456 Oak Ave, Apt 2B",
  "specialInstructions": "Ring doorbell twice",
  "items": [
    {
      "menuItemId": 101,
      "quantity": 2
    },
    {
      "menuItemId": 102,
      "quantity": 1
    }
  ]
}

Response: 201 Created
{
  "id": 1,
  "customerId": 1,
  "restaurantId": 1,
  "orderStatus": "PENDING",
  "paymentStatus": "PENDING",
  "totalAmount": 35.97,
  "deliveryAddress": "456 Oak Ave, Apt 2B",
  "items": [...],
  "createdAt": "2026-02-12T10:30:00",
  "updatedAt": "2026-02-12T10:30:00"
}
```

#### Get Order by ID
```http
GET /api/orders/{id}
```

#### Get Customer Orders
```http
GET /api/orders/customer/{customerId}
```

#### Get Restaurant Orders
```http
GET /api/orders/restaurant/{restaurantId}
```

#### Update Order Status
```http
PUT /api/orders/{id}/status
Content-Type: application/json

{
  "status": "CONFIRMED"
}
```

#### Cancel Order
```http
DELETE /api/orders/{id}
```

**Order Status Flow:**
```
PENDING → CONFIRMED → PREPARING → READY → PICKED_UP → DELIVERED
   ↓          ↓           ↓
CANCELLED  CANCELLED  CANCELLED
```

**Via Gateway:** `http://localhost:9090/api/orders`

---

## 💳 PAYMENT SERVICE (Port 8085)

### Payment Processing

#### Create Razorpay Order
```http
POST /api/payments/razorpay/order
Content-Type: application/json

{
  "orderId": "1",
  "amount": 35.97,
  "paymentMethod": "RAZORPAY"
}

Response: 200 OK
{
  "appOrderId": "1",
  "razorpayOrderId": "order_xyz123",
  "keyId": "rzp_test_xxxxx",
  "amount": 3597,
  "currency": "INR",
  "status": "CREATED"
}
```

#### Verify Razorpay Payment
```http
POST /api/payments/razorpay/verify
Content-Type: application/json

{
  "orderId": "1",
  "razorpayOrderId": "order_xyz123",
  "razorpayPaymentId": "pay_abc456",
  "razorpaySignature": "signature_hash"
}

Response: 200 OK
{
  "verified": true,
  "status": "VERIFIED",
  "message": "Payment signature verified"
}
```

#### Get Payment by Order ID
```http
GET /api/payments/order/{orderId}
```

**Via Gateway:** `http://localhost:9090/api/payments`

---

## 🚚 DELIVERY SERVICE (Port 8083)

### Delivery Agent Management

#### Register Delivery Agent
```http
POST /delivery-service/api/agents/register
Content-Type: application/json

{
  "name": "Mike Driver",
  "email": "mike@delivery.com",
  "phone": "5551234567",
  "password": "password123",
  "vehicleType": "BIKE",
  "vehicleNumber": "ABC-1234",
  "licenseNumber": "DL123456"
}
```

#### Agent Login
```http
POST /delivery-service/api/agents/login
Content-Type: application/json

{
  "email": "mike@delivery.com",
  "password": "password123"
}
```

#### Get Available Agents
```http
GET /delivery-service/api/agents/available
```

#### Update Agent Location
```http
PUT /delivery-service/api/agents/{agentId}/location
Content-Type: application/json

{
  "latitude": 40.7128,
  "longitude": -74.0060
}
```

### Delivery Management

#### Get Delivery by ID
```http
GET /delivery-service/api/deliveries/{id}
```

#### Get Delivery by Order ID
```http
GET /delivery-service/api/deliveries/order/{orderId}
```

#### Get Agent Deliveries
```http
GET /delivery-service/api/deliveries/agent/{agentId}
```

#### Update Delivery Status
```http
PUT /delivery-service/api/deliveries/{id}/status
Content-Type: application/json

{
  "status": "PICKED_UP",
  "remarks": "Package picked up from restaurant"
}
```

#### Track Delivery
```http
GET /delivery-service/api/deliveries/{id}/track
```

**Delivery Status Flow:**
```
PENDING → ASSIGNED → PICKED_UP → IN_TRANSIT → DELIVERED
   ↓          ↓           ↓            ↓
CANCELLED  CANCELLED  CANCELLED   CANCELLED
```

**Via Gateway:** `http://localhost:9090/delivery-service/api/deliveries`

---

## 🔔 NOTIFICATION SERVICE (Port 8086)

### Notification Endpoints

#### Get Notifications (Future)
```http
GET /api/notifications/user/{userId}
```

**Note:** Currently, Notification Service is event-driven and automatically sends notifications based on Kafka events. REST endpoints for querying notifications can be added in future phases.

**Via Gateway:** `http://localhost:9090/api/notifications`

---

## 🌐 API GATEWAY (Port 9090)

### Gateway Management

#### Get All Routes
```http
GET /actuator/gateway/routes
```

#### Get Gateway Health
```http
GET /actuator/health
```

#### Refresh Routes
```http
POST /actuator/gateway/refresh
```

---

## 📊 EUREKA SERVICE DISCOVERY (Port 8761)

### Eureka Dashboard
```
http://localhost:8761
```

### Get All Registered Services
```http
GET /eureka/apps
Accept: application/json
```

### Get Specific Service Instances
```http
GET /eureka/apps/{SERVICE-NAME}
Accept: application/json
```

---

## 🔄 COMPLETE USER FLOW EXAMPLE

### 1. User Registration
```bash
curl -X POST http://localhost:9090/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john@example.com",
    "phone": "1234567890",
    "password": "password123",
    "confirmPassword": "password123"
  }'
```

### 2. Browse Restaurants
```bash
curl http://localhost:9090/api/restaurants
```

### 3. View Restaurant Menu
```bash
curl http://localhost:9090/api/menu/1
```

### 4. Create Order
```bash
curl -X POST http://localhost:9090/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "customerId": 1,
    "restaurantId": 1,
    "deliveryAddress": "456 Oak Ave",
    "items": [
      {"menuItemId": 101, "quantity": 2}
    ]
  }'
```

### 5. Create Payment Order
```bash
curl -X POST http://localhost:9090/api/payments/razorpay/order \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "1",
    "amount": 35.97,
    "paymentMethod": "RAZORPAY"
  }'
```

### 6. Verify Payment
```bash
curl -X POST http://localhost:9090/api/payments/razorpay/verify \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "1",
    "razorpayOrderId": "order_xyz123",
    "razorpayPaymentId": "pay_abc456",
    "razorpaySignature": "signature_hash"
  }'
```

### 7. Track Delivery
```bash
curl http://localhost:9090/delivery-service/api/deliveries/order/1
```

---

## 🧪 HEALTH CHECK ENDPOINTS

| Service | Health Check URL |
|---------|-----------------|
| User Service | http://localhost:8000/actuator/health |
| Restaurant Service | http://localhost:8082/actuator/health |
| Order Service | http://localhost:8084/actuator/health |
| Payment Service | http://localhost:8085/actuator/health |
| Delivery Service | http://localhost:8083/delivery-service/actuator/health |
| Notification Service | http://localhost:8086/actuator/health |
| API Gateway | http://localhost:9090/actuator/health |
| Eureka Server | http://localhost:8761/actuator/health |

---

## 🔐 AUTHENTICATION

Most endpoints require JWT authentication. Include the token in the Authorization header:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Token obtained from:**
- `/api/auth/signup`
- `/api/auth/login`
- `/delivery-service/api/agents/login`

---

## 📝 NOTES

1. **Direct Service Access:** Services can be accessed directly via their ports or through the API Gateway (recommended)
2. **Service Discovery:** All services register with Eureka for load balancing and discovery
3. **Event-Driven:** Many operations trigger Kafka events for asynchronous processing
4. **Database:** Each service has its own database for data isolation
5. **CORS:** Configured for frontend integration

---

## 🚀 QUICK START

### Using Docker Compose
```bash
docker-compose up -d
```

### Access Services
- API Gateway: http://localhost:9090
- Eureka Dashboard: http://localhost:8761
- Individual services: See port table above

### Test Integration
```bash
# Check all services are registered
curl http://localhost:8761/eureka/apps

# Test via gateway
curl http://localhost:9090/api/restaurants
```

---

**Last Updated:** February 12, 2026  
**Version:** 1.0.0
