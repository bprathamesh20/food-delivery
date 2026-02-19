# 📚 Food Delivery System - Correct API Endpoints

**Base URL (via API Gateway):** `http://localhost:9090`  
**Eureka Dashboard:** `http://localhost:8761`

---

## 🔐 USER SERVICE (Port 8000)

### Base Path: `/api/auth`

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
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "emailOrPhone": "john@example.com",
  "password": "password123"
}
```

#### Health Check
```http
GET /api/auth/health
```

---

## 🍽️ RESTAURANT SERVICE (Port 8082)

### Restaurant Management - Base Path: `/api/restaurants`

#### Register/Create Restaurant
```http
POST /api/restaurants/register
Content-Type: application/json

{
  "name": "Pizza Palace",
  "address": "123 Main St, City, State 12345"
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Pizza Palace",
  "address": "123 Main St, City, State 12345",
  "active": true,
  "createdAt": "2026-02-12T10:15:00"
}
```

#### Get All Restaurants
```http
GET /restaurants
```

#### Get Restaurant by ID
```http
GET /restaurants/{id}
```

#### Disable Restaurant
```http
PUT /restaurants/{id}/disable
```

#### Delete Restaurant
```http
DELETE /restaurants/{id}
```

### Menu Management - Base Path: `/menus`

#### Add Menu Item
```http
POST /menus/{restaurantId}
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
GET /menus/{restaurantId}
```

#### Disable Menu Item
```http
PUT /menus/item/{menuItemId}/disable
```

#### Delete Menu Item
```http
DELETE /menus/item/{menuItemId}
```

### Restaurant Auth - Base Path: `/auth`

#### Register User (Restaurant Owner)
```http
POST /auth/register
Content-Type: application/json

{
  "username": "owner123",
  "password": "password123"
}
```

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "username": "owner123",
  "password": "password123"
}
```

---

## 📦 ORDER SERVICE (Port 8084)

### Base Path: `/api/orders`

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
      "menuItemId": 1,
      "quantity": 2
    }
  ]
}
```

**Note:** The `price` field is NOT included in items - it's fetched automatically from the menu item.

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

#### Get All Orders
```http
GET /api/orders
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

**Order Status Values:**
- PENDING
- CONFIRMED
- PREPARING
- READY
- PICKED_UP
- DELIVERED
- CANCELLED

---

## 💳 PAYMENT SERVICE (Port 8085)

### Base Path: `/api/payments`

#### Create Payment
```http
POST /api/payments
Content-Type: application/json

{
  "orderId": "1",
  "amount": 25.98,
  "paymentMethod": "RAZORPAY"
}
```

#### Create Razorpay Order
```http
POST /api/payments/razorpay/order
Content-Type: application/json

{
  "orderId": "1",
  "amount": 25.98,
  "paymentMethod": "RAZORPAY"
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
```

#### Get Payment by ID
```http
GET /api/payments/{paymentId}
```

#### Get Payment by Order ID
```http
GET /api/payments/order/{orderId}
```

#### Refund Payment
```http
POST /api/payments/refund/{orderId}
```

---

## 🚚 DELIVERY SERVICE (Port 8083)

**Important:** Delivery Service has context path `/delivery-service`, so all endpoints are prefixed with it.

### Delivery Agent Management - Base Path: `/delivery-service/api/v1/agents`

#### Get My Profile (Authenticated)
```http
GET /delivery-service/api/v1/agents/me
Authorization: Bearer {token}
```

#### Update My Status (Authenticated)
```http
PUT /delivery-service/api/v1/agents/me/status
Authorization: Bearer {token}
Content-Type: application/json

{
  "status": "AVAILABLE"
}
```

#### Update My Location (Authenticated)
```http
PUT /delivery-service/api/v1/agents/me/location
Authorization: Bearer {token}
Content-Type: application/json

{
  "latitude": 40.7128,
  "longitude": -74.0060
}
```

#### Get All Agents
```http
GET /delivery-service/api/v1/agents
```

#### Get Available Agents
```http
GET /delivery-service/api/v1/agents/available
```

#### Get Agent by ID
```http
GET /delivery-service/api/v1/agents/{id}
```

#### Update Agent Status (Admin)
```http
PUT /delivery-service/api/v1/agents/{id}/status
Content-Type: application/json

{
  "status": "BUSY"
}
```

**Agent Status Values:**
- AVAILABLE
- BUSY
- OFFLINE

### Delivery Management - Base Path: `/delivery-service/api/v1/deliveries`

#### Create Delivery
```http
POST /delivery-service/api/v1/deliveries
Content-Type: application/json

{
  "orderId": 1,
  "customerId": 1,
  "restaurantId": 1,
  "pickupAddress": "123 Main St",
  "deliveryAddress": "456 Oak Ave"
}
```

#### Assign Delivery to Agent
```http
POST /delivery-service/api/v1/deliveries/{deliveryId}/assign
Content-Type: application/json

{
  "agentId": 1
}
```

#### Update Delivery Status
```http
PUT /delivery-service/api/v1/deliveries/{deliveryId}/status
Content-Type: application/json

{
  "status": "PICKED_UP",
  "remarks": "Package picked up from restaurant"
}
```

#### Get Delivery by ID
```http
GET /delivery-service/api/v1/deliveries/{id}
```

#### Get Delivery by Order ID
```http
GET /delivery-service/api/v1/deliveries/order/{orderId}
```

#### Get Deliveries by Agent
```http
GET /delivery-service/api/v1/deliveries/agent/{agentId}
```

#### Get Deliveries by Customer
```http
GET /delivery-service/api/v1/deliveries/customer/{customerId}
```

#### Get Delivery Tracking
```http
GET /delivery-service/api/v1/deliveries/{deliveryId}/tracking
```

#### Update Delivery Location
```http
POST /delivery-service/api/v1/deliveries/{deliveryId}/location
Content-Type: application/json

{
  "latitude": 40.7128,
  "longitude": -74.0060,
  "remarks": "Near destination"
}
```

**Delivery Status Values:**
- PENDING
- ASSIGNED
- PICKED_UP
- IN_TRANSIT
- DELIVERED
- CANCELLED

### Delivery Auth - Base Path: `/delivery-service/api/v1/auth`

#### Register Delivery Agent
```http
POST /delivery-service/api/v1/auth/agent/register
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
POST /delivery-service/api/v1/auth/agent/login
Content-Type: application/json

{
  "email": "mike@delivery.com",
  "password": "password123"
}
```

#### Test Auth Endpoint
```http
GET /delivery-service/api/v1/auth/test
```

---

## 🔔 NOTIFICATION SERVICE (Port 8086)

**Note:** Notification Service is event-driven and listens to Kafka events. It doesn't expose REST endpoints for sending notifications. Notifications are automatically triggered by events from other services.

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
  -d "{\"fullName\":\"John Doe\",\"email\":\"john@example.com\",\"phone\":\"1234567890\",\"password\":\"password123\",\"confirmPassword\":\"password123\"}"
```

### 2. Register Restaurant
```bash
curl -X POST http://localhost:9090/restaurants/register \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Pizza Palace\",\"address\":\"123 Main St, City, State 12345\"}"
```

### 3. Add Menu Item
```bash
curl -X POST http://localhost:9090/menus/1 \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Margherita Pizza\",\"description\":\"Classic cheese pizza\",\"price\":12.99,\"category\":\"Pizza\",\"available\":true}"
```

### 4. Create Order
```bash
curl -X POST http://localhost:9090/api/orders \
  -H "Content-Type: application/json" \
  -d "{\"customerId\":1,\"restaurantId\":1,\"deliveryAddress\":\"456 Oak Ave\",\"items\":[{\"menuItemId\":1,\"quantity\":2}]}"
```

### 5. Create Payment
```bash
curl -X POST http://localhost:9090/api/payments/razorpay/order \
  -H "Content-Type: application/json" \
  -d "{\"orderId\":\"1\",\"amount\":25.98,\"paymentMethod\":\"RAZORPAY\"}"
```

### 6. Register Delivery Agent
```bash
curl -X POST http://localhost:9090/delivery-service/api/v1/auth/agent/register \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Mike Driver\",\"email\":\"mike@delivery.com\",\"phone\":\"5551234567\",\"password\":\"password123\",\"vehicleType\":\"BIKE\",\"vehicleNumber\":\"ABC-1234\",\"licenseNumber\":\"DL123456\"}"
```

### 7. Track Delivery
```bash
curl http://localhost:9090/delivery-service/api/v1/deliveries/order/1
```

---

## 🧪 HEALTH CHECK ENDPOINTS

| Service | Health Check URL |
|---------|-----------------|
| User Service | http://localhost:8000/actuator/health |
| Restaurant Service | http://localhost:8082/actuator/health |
| Order Service | http://localhost:8084/actuator/health |
| Payment Service | http://localhost:8085/actuator/health |
| Delivery Service | http://localhost:8083/actuator/health |
| Notification Service | http://localhost:8086/actuator/health |
| API Gateway | http://localhost:9090/actuator/health |
| Eureka Server | http://localhost:8761/actuator/health |

---

## 📝 IMPORTANT NOTES

### Gateway Routing
- **User Service**: `/api/auth/**` → USER-SERVICE
- **Restaurant Service**: `/restaurants/**`, `/menus/**`, `/auth/**` → RESTAURANT-SERVICE
- **Order Service**: `/api/orders/**` → ORDER-SERVICE
- **Payment Service**: `/api/payments/**` → PAYMENT-SERVICE
- **Delivery Service**: `/delivery-service/**` → DELIVERY-SERVICE (includes context path)

### Authentication
- User Service uses JWT tokens from `/api/auth/login` or `/api/auth/signup`
- Delivery Service uses JWT tokens from `/delivery-service/api/v1/auth/agent/login` or `/delivery-service/api/v1/auth/agent/register`
- Include token in header: `Authorization: Bearer {token}`

### Direct Service Access
Services can also be accessed directly (bypassing gateway):
- User Service: http://localhost:8000/api/auth/**
- Restaurant Service: http://localhost:8082/restaurants/**, http://localhost:8082/menus/**
- Order Service: http://localhost:8084/api/orders/**
- Payment Service: http://localhost:8085/api/payments/**
- Delivery Service: http://localhost:8083/delivery-service/api/v1/**

---

**Last Updated:** February 12, 2026  
**Version:** 2.0.0 (Corrected)
