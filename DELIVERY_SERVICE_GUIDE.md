# Delivery Service - Complete Guide

## Overview
The Delivery Service manages delivery agents and order deliveries. It has its own authentication system separate from the User Service.

## Architecture

### Delivery Service Components
1. **Delivery Agent Authentication** - Separate login system for delivery agents
2. **Delivery Management** - Create and track deliveries
3. **Location Tracking** - Track delivery agent locations
4. **Status Updates** - Update delivery status in real-time

### Base URL
- Direct: `http://localhost:8083/delivery-service/api/v1`
- Via Gateway: `http://localhost:9090/delivery-service/api/v1`

---

## Step 1: Register a Delivery Agent

### API Endpoint
```
POST /delivery-service/api/v1/auth/agent/register
```

### Request Body
```json
{
  "name": "John Delivery",
  "email": "john.delivery@example.com",
  "password": "delivery123",
  "phoneNumber": "9876543210",
  "vehicleType": "BIKE",
  "vehicleNumber": "MH12AB1234",
  "address": "123 Agent Street",
  "city": "Mumbai",
  "state": "Maharashtra",
  "licenseNumber": "DL1234567890",
  "currentLatitude": 19.0760,
  "currentLongitude": 72.8777
}
```

### Vehicle Types
- `BIKE`
- `CAR`
- `SCOOTER`

### cURL Command
```bash
curl -X POST http://localhost:9090/delivery-service/api/v1/auth/agent/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Delivery",
    "email": "john.delivery@example.com",
    "password": "delivery123",
    "phoneNumber": "9876543210",
    "vehicleType": "BIKE",
    "vehicleNumber": "MH12AB1234",
    "address": "123 Agent Street",
    "city": "Mumbai",
    "state": "Maharashtra",
    "licenseNumber": "DL1234567890",
    "currentLatitude": 19.0760,
    "currentLongitude": 72.8777
  }'
```

### Response
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "agentId": 1,
  "name": "John Delivery",
  "email": "john.delivery@example.com"
}
```

---

## Step 2: Login as Delivery Agent

### API Endpoint
```
POST /delivery-service/api/v1/auth/agent/login
```

### Request Body
```json
{
  "email": "john.delivery@example.com",
  "password": "delivery123"
}
```

### cURL Command
```bash
curl -X POST http://localhost:9090/delivery-service/api/v1/auth/agent/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.delivery@example.com",
    "password": "delivery123"
  }'
```

### Response
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "agentId": 1,
  "name": "John Delivery",
  "email": "john.delivery@example.com"
}
```

**Save this token!** You'll need it for authenticated requests.

---

## Step 3: Get Agent Profile

### API Endpoint
```
GET /delivery-service/api/v1/agents/me
```

### Headers
```
Authorization: Bearer <agent_token>
```

### cURL Command
```bash
curl -X GET http://localhost:9090/delivery-service/api/v1/agents/me \
  -H "Authorization: Bearer YOUR_AGENT_TOKEN"
```

### Response
```json
{
  "id": 1,
  "name": "John Delivery",
  "email": "john.delivery@example.com",
  "phoneNumber": "9876543210",
  "vehicleType": "BIKE",
  "vehicleNumber": "MH12AB1234",
  "status": "AVAILABLE",
  "currentLatitude": 19.0760,
  "currentLongitude": 72.8777,
  "address": "123 Agent Street",
  "city": "Mumbai",
  "state": "Maharashtra",
  "licenseNumber": "DL1234567890"
}
```

---

## Step 4: Update Agent Status

### API Endpoint
```
PUT /delivery-service/api/v1/agents/me/status
```

### Agent Status Options
- `AVAILABLE` - Ready to accept deliveries
- `BUSY` - Currently on a delivery
- `OFFLINE` - Not available

### Request Body
```json
{
  "status": "AVAILABLE"
}
```

### cURL Command
```bash
curl -X PUT http://localhost:9090/delivery-service/api/v1/agents/me/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_AGENT_TOKEN" \
  -d '{
    "status": "AVAILABLE"
  }'
```

---

## Step 5: Create a Delivery (After Order is Placed)

### API Endpoint
```
POST /delivery-service/api/v1/deliveries
```

### Request Body
```json
{
  "orderId": 1,
  "restaurantId": 1,
  "customerId": 1,
  "pickupAddress": "Pizza Palace, 123 Main St",
  "pickupLatitude": 19.0760,
  "pickupLongitude": 72.8777,
  "deliveryAddress": "456 Oak Ave, Apt 2B",
  "deliveryLatitude": 19.0820,
  "deliveryLongitude": 72.8850,
  "deliveryInstructions": "Ring doorbell twice",
  "deliveryFee": 40.0
}
```

### cURL Command
```bash
curl -X POST http://localhost:9090/delivery-service/api/v1/deliveries \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "restaurantId": 1,
    "customerId": 1,
    "pickupAddress": "Pizza Palace, 123 Main St",
    "pickupLatitude": 19.0760,
    "pickupLongitude": 72.8777,
    "deliveryAddress": "456 Oak Ave, Apt 2B",
    "deliveryLatitude": 19.0820,
    "deliveryLongitude": 72.8850,
    "deliveryInstructions": "Ring doorbell twice",
    "deliveryFee": 40.0
  }'
```

### Response
```json
{
  "id": 1,
  "orderId": 1,
  "status": "PENDING",
  "pickupAddress": "Pizza Palace, 123 Main St",
  "deliveryAddress": "456 Oak Ave, Apt 2B",
  "deliveryFee": 40.0,
  "createdAt": "2026-02-13T10:30:00",
  "deliveryAgent": null
}
```

---

## Step 6: Assign Delivery to Agent

### API Endpoint
```
POST /delivery-service/api/v1/deliveries/{deliveryId}/assign
```

### Request Body
```json
{
  "agentId": 1
}
```

### cURL Command
```bash
curl -X POST http://localhost:9090/delivery-service/api/v1/deliveries/1/assign \
  -H "Content-Type: application/json" \
  -d '{
    "agentId": 1
  }'
```

### Response
```json
{
  "id": 1,
  "orderId": 1,
  "status": "ASSIGNED",
  "deliveryAgent": {
    "id": 1,
    "name": "John Delivery",
    "phoneNumber": "9876543210",
    "vehicleType": "BIKE",
    "vehicleNumber": "MH12AB1234"
  },
  "pickupAddress": "Pizza Palace, 123 Main St",
  "deliveryAddress": "456 Oak Ave, Apt 2B",
  "deliveryFee": 40.0
}
```

---

## Step 7: Update Delivery Status

### API Endpoint
```
PUT /delivery-service/api/v1/deliveries/{deliveryId}/status
```

### Delivery Status Flow
1. `PENDING` - Delivery created, waiting for agent
2. `ASSIGNED` - Agent assigned
3. `PICKED_UP` - Agent picked up from restaurant
4. `IN_TRANSIT` - Agent on the way to customer
5. `DELIVERED` - Delivered to customer
6. `CANCELLED` - Delivery cancelled

### Request Body
```json
{
  "status": "PICKED_UP",
  "remarks": "Food picked up from restaurant"
}
```

### cURL Commands

**Mark as Picked Up:**
```bash
curl -X PUT http://localhost:9090/delivery-service/api/v1/deliveries/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_AGENT_TOKEN" \
  -d '{
    "status": "PICKED_UP",
    "remarks": "Food picked up from restaurant"
  }'
```

**Mark as In Transit:**
```bash
curl -X PUT http://localhost:9090/delivery-service/api/v1/deliveries/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_AGENT_TOKEN" \
  -d '{
    "status": "IN_TRANSIT",
    "remarks": "On the way to customer"
  }'
```

**Mark as Delivered:**
```bash
curl -X PUT http://localhost:9090/delivery-service/api/v1/deliveries/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_AGENT_TOKEN" \
  -d '{
    "status": "DELIVERED",
    "remarks": "Delivered successfully"
  }'
```

---

## Step 8: Update Agent Location

### API Endpoint
```
PUT /delivery-service/api/v1/agents/me/location
```

### Request Body
```json
{
  "latitude": 19.0800,
  "longitude": 72.8800
}
```

### cURL Command
```bash
curl -X PUT http://localhost:9090/delivery-service/api/v1/agents/me/location \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_AGENT_TOKEN" \
  -d '{
    "latitude": 19.0800,
    "longitude": 72.8800
  }'
```

---

## Step 9: Track Delivery

### Get Delivery by Order ID
```
GET /delivery-service/api/v1/deliveries/order/{orderId}
```

### cURL Command
```bash
curl -X GET http://localhost:9090/delivery-service/api/v1/deliveries/order/1
```

### Get Delivery Tracking History
```
GET /delivery-service/api/v1/deliveries/{deliveryId}/tracking
```

### cURL Command
```bash
curl -X GET http://localhost:9090/delivery-service/api/v1/deliveries/1/tracking
```

### Response
```json
[
  {
    "id": 1,
    "latitude": 19.0760,
    "longitude": 72.8777,
    "timestamp": "2026-02-13T10:35:00",
    "remarks": "Picked up from restaurant"
  },
  {
    "id": 2,
    "latitude": 19.0800,
    "longitude": 72.8800,
    "timestamp": "2026-02-13T10:40:00",
    "remarks": "On the way"
  }
]
```

---

## Step 10: Get Agent's Deliveries

### API Endpoint
```
GET /delivery-service/api/v1/deliveries/agent/{agentId}
```

### cURL Command
```bash
curl -X GET http://localhost:9090/delivery-service/api/v1/deliveries/agent/1 \
  -H "Authorization: Bearer YOUR_AGENT_TOKEN"
```

---

## Complete Workflow Example

### 1. Register Delivery Agent
```bash
curl -X POST http://localhost:9090/delivery-service/api/v1/auth/agent/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Delivery",
    "email": "john@delivery.com",
    "password": "pass123",
    "phoneNumber": "9876543210",
    "vehicleType": "BIKE",
    "vehicleNumber": "MH12AB1234",
    "address": "123 Agent St",
    "city": "Mumbai",
    "state": "Maharashtra",
    "licenseNumber": "DL123456"
  }'
```

### 2. Set Agent Status to Available
```bash
curl -X PUT http://localhost:9090/delivery-service/api/v1/agents/me/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer AGENT_TOKEN" \
  -d '{"status": "AVAILABLE"}'
```

### 3. Customer Places Order (Already Working)
Order ID: 1

### 4. Create Delivery for Order
```bash
curl -X POST http://localhost:9090/delivery-service/api/v1/deliveries \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "restaurantId": 1,
    "customerId": 1,
    "pickupAddress": "Restaurant Address",
    "deliveryAddress": "Customer Address",
    "deliveryFee": 40.0
  }'
```

### 5. Assign to Agent
```bash
curl -X POST http://localhost:9090/delivery-service/api/v1/deliveries/1/assign \
  -H "Content-Type: application/json" \
  -d '{"agentId": 1}'
```

### 6. Agent Updates Status
```bash
# Picked up
curl -X PUT http://localhost:9090/delivery-service/api/v1/deliveries/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer AGENT_TOKEN" \
  -d '{"status": "PICKED_UP"}'

# In transit
curl -X PUT http://localhost:9090/delivery-service/api/v1/deliveries/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer AGENT_TOKEN" \
  -d '{"status": "IN_TRANSIT"}'

# Delivered
curl -X PUT http://localhost:9090/delivery-service/api/v1/deliveries/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer AGENT_TOKEN" \
  -d '{"status": "DELIVERED"}'
```

### 7. Customer Tracks Delivery
```bash
curl -X GET http://localhost:9090/delivery-service/api/v1/deliveries/order/1
```

---

## Frontend Integration

The frontend already has delivery service API calls in `frontend/src/services/api.js`:

```javascript
// Get delivery by order ID
deliveryService.getByOrderId(orderId)

// Get delivery tracking
deliveryService.getTracking(deliveryId)
```

These are used in:
- `OrderTracking.jsx` - Shows delivery status and agent info
- `OrderConfirmation.jsx` - Shows delivery details

---

## Testing Checklist

- [ ] Register delivery agent
- [ ] Login as delivery agent (save token)
- [ ] Set agent status to AVAILABLE
- [ ] Create delivery for an order
- [ ] Assign delivery to agent
- [ ] Update delivery status (PICKED_UP → IN_TRANSIT → DELIVERED)
- [ ] Update agent location
- [ ] Track delivery from customer side
- [ ] View delivery history

---

## Quick Test Script

```bash
# 1. Register agent
AGENT_RESPONSE=$(curl -s -X POST http://localhost:9090/delivery-service/api/v1/auth/agent/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Agent",
    "email": "agent@test.com",
    "password": "test123",
    "phoneNumber": "9876543210",
    "vehicleType": "BIKE",
    "vehicleNumber": "TEST123",
    "address": "Test Address",
    "city": "Mumbai",
    "state": "Maharashtra",
    "licenseNumber": "DL123"
  }')

echo "Agent registered: $AGENT_RESPONSE"

# Extract token (you'll need to do this manually or use jq)
# AGENT_TOKEN="paste_token_here"

# 2. Set status
# curl -X PUT http://localhost:9090/delivery-service/api/v1/agents/me/status \
#   -H "Content-Type: application/json" \
#   -H "Authorization: Bearer $AGENT_TOKEN" \
#   -d '{"status": "AVAILABLE"}'

# 3. Create delivery (replace orderId with actual order)
# curl -X POST http://localhost:9090/delivery-service/api/v1/deliveries \
#   -H "Content-Type: application/json" \
#   -d '{
#     "orderId": 1,
#     "restaurantId": 1,
#     "customerId": 1,
#     "pickupAddress": "Restaurant",
#     "deliveryAddress": "Customer",
#     "deliveryFee": 40.0
#   }'
```

---

## Next Steps

1. Register a delivery agent using the API
2. Create a delivery for your existing order
3. Assign the delivery to the agent
4. Update delivery status through the workflow
5. Track the delivery from the frontend

The frontend Order Tracking page will automatically show delivery information when available!
