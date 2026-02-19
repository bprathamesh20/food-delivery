# Complete Backend API Mapping - Food Delivery System

**Gateway URL:** `http://localhost:9090`

## Service Ports
- API Gateway: 9090
- User Service: 8000
- Restaurant Service: 8082
- Order Service: 8084
- Payment Service: 8085
- Delivery Service: 8083 (context-path: `/delivery-service`)
- Notification Service: 8086 (Event-driven, no REST endpoints)
- Eureka Server: 8761

---

## 1. USER SERVICE

### Authentication Endpoints
**Base:** `/api/auth`

#### POST /api/auth/signup
```json
Request:
{
  "fullName": "string (2-100 chars)",
  "email": "string (valid email)",
  "phone": "string (10 digits)",
  "password": "string (min 6 chars)",
  "confirmPassword": "string"
}

Response:
{
  "token": "string",
  "type": "Bearer",
  "id": number,
  "fullName": "string",
  "email": "string",
  "phone": "string"
}
```

#### POST /api/auth/login
```json
Request:
{
  "emailOrPhone": "string",
  "password": "string"
}

Response: Same as signup
```

#### GET /api/auth/health
Returns: "Auth Service is running!"

### User Management
**Base:** `/api/users`

#### GET /api/users/{id}
Returns: User object

---

## 2. RESTAURANT SERVICE

### Restaurant Management
**Base:** `/api/restaurants`

#### POST /api/restaurants/register
#### POST /api/restaurants
```json
Request:
{
  "name": "string",
  "address": "string"
}

Response:
{
  "id": number,
  "name": "string",
  "address": "string",
  "active": boolean,
  "createdAt": "datetime"
}
```

#### GET /api/restaurants
Returns: Array of restaurants

#### GET /api/restaurants/{id}
Returns: Restaurant object

#### PUT /api/restaurants/{id}/disable
Returns: "Restaurant disabled successfully"

#### DELETE /api/restaurants/{id}
Returns: "Restaurant deleted successfully"

### Menu Management
**Base:** `/api/menus`

#### POST /api/menus/{restaurantId}
```json
Request:
{
  "name": "string",
  "price": number,
  "available": boolean
}

Response: MenuItem object
```

#### GET /api/menus/{restaurantId}
Returns: Array of menu items

#### GET /api/menus/item/{menuItemId}
Returns: Single menu item

#### PUT /api/menus/item/{menuItemId}/disable
Returns: "Menu item disabled successfully"

#### DELETE /api/menus/item/{menuItemId}
Returns: "Menu item deleted successfully"

### Restaurant Auth (Internal)
**Base:** `/auth`

#### POST /auth/register
```json
Request:
{
  "username": "string",
  "password": "string"
}
```

#### POST /auth/login
```json
Request:
{
  "username": "string",
  "password": "string"
}

Response:
{
  "token": "string"
}
```

---

## 3. ORDER SERVICE

**Base:** `/api/orders`

#### POST /api/orders
```json
Request:
{
  "customerId": number,
  "restaurantId": number,
  "deliveryAddress": "string",
  "specialInstructions": "string (optional)",
  "items": [
    {
      "menuItemId": number,
      "quantity": number (positive)
    }
  ]
}

Response:
{
  "id": number,
  "customerId": number,
  "restaurantId": number,
  "orderStatus": "PENDING|CONFIRMED|PREPARING|READY|PICKED_UP|DELIVERED|CANCELLED",
  "paymentStatus": "PENDING|COMPLETED|FAILED|REFUNDED",
  "totalAmount": number,
  "deliveryAddress": "string",
  "specialInstructions": "string",
  "createdAt": "datetime",
  "updatedAt": "datetime",
  "items": [
    {
      "id": number,
      "menuItemId": number,
      "menuItemName": "string",
      "quantity": number,
      "pricePerUnit": number,
      "subtotal": number
    }
  ]
}
```

#### GET /api/orders/{id}
Returns: Order object

#### GET /api/orders/customer/{customerId}
Returns: Array of orders

#### GET /api/orders/restaurant/{restaurantId}
Returns: Array of orders

#### GET /api/orders
Returns: Array of all orders

#### PUT /api/orders/{id}/status
```json
Request:
{
  "status": "PENDING|CONFIRMED|PREPARING|READY|PICKED_UP|DELIVERED|CANCELLED"
}

Response: Updated order object
```

#### DELETE /api/orders/{id}
Returns: 204 No Content

---

## 4. PAYMENT SERVICE

**Base:** `/api/payments`

#### POST /api/payments
```json
Request:
{
  "orderId": "string",
  "amount": number,
  "paymentMethod": "string"
}

Response:
{
  "paymentId": number,
  "status": "string",
  "timestamp": "string"
}
```

#### POST /api/payments/razorpay/order
```json
Request:
{
  "orderId": "string",
  "amount": number,
  "paymentMethod": "RAZORPAY"
}

Response:
{
  "appOrderId": "string",
  "razorpayOrderId": "string",
  "keyId": "string",
  "amount": number,
  "currency": "INR",
  "status": "CREATED"
}
```

#### POST /api/payments/razorpay/verify
```json
Request:
{
  "orderId": "string",
  "razorpayOrderId": "string",
  "razorpayPaymentId": "string",
  "razorpaySignature": "string"
}

Response:
{
  "verified": boolean,
  "status": "string",
  "message": "string"
}
```

#### GET /api/payments/{paymentId}
Returns: Payment object

#### GET /api/payments/order/{orderId}
Returns: Payment object

#### POST /api/payments/refund/{orderId}
Returns: Payment object

---

## 5. DELIVERY SERVICE

**Base:** `/delivery-service/api/v1`

### Delivery Agent Auth

#### POST /delivery-service/api/v1/auth/agent/register
```json
Request:
{
  "name": "string",
  "email": "string",
  "password": "string",
  "phoneNumber": "string",
  "vehicleType": "BIKE|CAR|SCOOTER",
  "vehicleNumber": "string",
  "address": "string",
  "city": "string",
  "state": "string",
  "licenseNumber": "string",
  "currentLatitude": number (optional),
  "currentLongitude": number (optional)
}

Response:
{
  "token": "string",
  "agentId": number,
  "name": "string",
  "email": "string"
}
```

#### POST /delivery-service/api/v1/auth/agent/login
```json
Request:
{
  "email": "string",
  "password": "string"
}

Response: Same as register
```

#### GET /delivery-service/api/v1/auth/test
Returns: "Auth endpoint is working!"

### Delivery Agent Management
**Base:** `/delivery-service/api/v1/agents`

#### GET /delivery-service/api/v1/agents/me
**Requires:** JWT token
Returns: Agent profile (password excluded)

#### PUT /delivery-service/api/v1/agents/me/status
**Requires:** JWT token
```json
Request:
{
  "status": "AVAILABLE|BUSY|OFFLINE"
}

Response: DeliveryAgentResponse
```

#### PUT /delivery-service/api/v1/agents/me/location
**Requires:** JWT token
```json
Request:
{
  "latitude": number,
  "longitude": number
}

Response: DeliveryAgentResponse
```

#### GET /delivery-service/api/v1/agents
Returns: Array of all agents

#### GET /delivery-service/api/v1/agents/available
Returns: Array of available agents

#### GET /delivery-service/api/v1/agents/{id}
Returns: Agent object

#### PUT /delivery-service/api/v1/agents/{id}/status
```json
Request:
{
  "status": "AVAILABLE|BUSY|OFFLINE"
}
```

### Delivery Management
**Base:** `/delivery-service/api/v1/deliveries`

#### POST /delivery-service/api/v1/deliveries
```json
Request:
{
  "orderId": number,
  "restaurantId": number,
  "customerId": number,
  "pickupAddress": "string",
  "pickupLatitude": number (optional),
  "pickupLongitude": number (optional),
  "deliveryAddress": "string",
  "deliveryLatitude": number (optional),
  "deliveryLongitude": number (optional),
  "deliveryInstructions": "string (optional)",
  "deliveryFee": number (optional)
}

Response: DeliveryResponse object
```

#### POST /delivery-service/api/v1/deliveries/{deliveryId}/assign
```json
Request:
{
  "agentId": number
}

Response: DeliveryResponse object
```

#### PUT /delivery-service/api/v1/deliveries/{deliveryId}/status
```json
Request:
{
  "status": "PENDING|ASSIGNED|PICKED_UP|IN_TRANSIT|DELIVERED|CANCELLED",
  "remarks": "string (optional)"
}

Response: DeliveryResponse object
```

#### GET /delivery-service/api/v1/deliveries/{id}
Returns: Delivery object

#### GET /delivery-service/api/v1/deliveries/order/{orderId}
Returns: Delivery object

#### GET /delivery-service/api/v1/deliveries/agent/{agentId}
Returns: Array of deliveries

#### GET /delivery-service/api/v1/deliveries/customer/{customerId}
Returns: Array of deliveries

#### GET /delivery-service/api/v1/deliveries/{deliveryId}/tracking
Returns: Array of tracking records

#### POST /delivery-service/api/v1/deliveries/{deliveryId}/location
```json
Request:
{
  "latitude": number,
  "longitude": number,
  "remarks": "string (optional)"
}

Response: DeliveryTrackingResponse
```

---

## 6. NOTIFICATION SERVICE

**Port:** 8086
**Type:** Event-driven (Kafka consumers only)
**No REST endpoints** - listens to Kafka topics:
- user-events
- order-events
- payment-events
- delivery-events

---

## Authentication

All services use JWT authentication with shared secret key.

**Token Format:**
```
Authorization: Bearer <token>
```

**Token obtained from:**
- User Service: `/api/auth/signup` or `/api/auth/login`
- Delivery Agent: `/delivery-service/api/v1/auth/agent/register` or `/delivery-service/api/v1/auth/agent/login`

---

## Gateway Routing

All requests go through API Gateway (port 9090):
- `/api/auth/**` → USER-SERVICE
- `/api/users/**` → USER-SERVICE
- `/api/restaurants/**` → RESTAURANT-SERVICE
- `/api/menus/**` → RESTAURANT-SERVICE
- `/auth/**` → RESTAURANT-SERVICE (internal auth)
- `/api/orders/**` → ORDER-SERVICE
- `/api/payments/**` → PAYMENT-SERVICE
- `/delivery-service/**` → DELIVERY-SERVICE

---

## CORS Configuration

Gateway has CORS enabled for all origins with:
- Methods: GET, POST, PUT, DELETE, OPTIONS, PATCH
- Headers: All
- Credentials: true

---

## Status Enums

### Order Status
- PENDING
- CONFIRMED
- PREPARING
- READY
- PICKED_UP
- DELIVERED
- CANCELLED

### Payment Status
- PENDING
- COMPLETED
- FAILED
- REFUNDED

### Delivery Status
- PENDING
- ASSIGNED
- PICKED_UP
- IN_TRANSIT
- DELIVERED
- CANCELLED

### Agent Status
- AVAILABLE
- BUSY
- OFFLINE

### Vehicle Type
- BIKE
- CAR
- SCOOTER

---

**Last Updated:** February 12, 2026
