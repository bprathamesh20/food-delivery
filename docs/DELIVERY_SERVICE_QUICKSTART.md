# Delivery Service - Quick Start

## What is the Delivery Service?

The Delivery Service manages:
- Delivery agents (separate from customers)
- Delivery assignments
- Real-time tracking
- Status updates

## Quick Test (5 Minutes)

### 1. Register a Delivery Agent
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

**Save the token from the response!**

### 2. Create a Delivery (After Placing an Order)
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

**Note the delivery ID from the response!**

### 3. Assign to Agent
```bash
curl -X POST http://localhost:9090/delivery-service/api/v1/deliveries/1/assign \
  -H "Content-Type: application/json" \
  -d '{"agentId": 1}'
```

### 4. Update Status (Use Agent Token)
```bash
# Mark as picked up
curl -X PUT http://localhost:9090/delivery-service/api/v1/deliveries/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_AGENT_TOKEN" \
  -d '{"status": "PICKED_UP"}'

# Mark as delivered
curl -X PUT http://localhost:9090/delivery-service/api/v1/deliveries/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_AGENT_TOKEN" \
  -d '{"status": "DELIVERED"}'
```

### 5. Track from Frontend
1. Go to http://localhost:5173
2. Login as customer
3. Go to "My Orders"
4. Click on an order
5. Click "Track Order"
6. You'll see delivery agent info and status!

## Delivery Status Flow

```
PENDING → ASSIGNED → PICKED_UP → IN_TRANSIT → DELIVERED
```

## Vehicle Types

- `BIKE` - Motorcycle/Scooter
- `CAR` - Car
- `SCOOTER` - Electric scooter

## Agent Status

- `AVAILABLE` - Ready for deliveries
- `BUSY` - Currently delivering
- `OFFLINE` - Not available

## Key Endpoints

### Agent Authentication
- `POST /delivery-service/api/v1/auth/agent/register` - Register agent
- `POST /delivery-service/api/v1/auth/agent/login` - Login agent

### Agent Management
- `GET /delivery-service/api/v1/agents/me` - Get profile
- `PUT /delivery-service/api/v1/agents/me/status` - Update status
- `PUT /delivery-service/api/v1/agents/me/location` - Update location

### Delivery Management
- `POST /delivery-service/api/v1/deliveries` - Create delivery
- `POST /delivery-service/api/v1/deliveries/{id}/assign` - Assign to agent
- `PUT /delivery-service/api/v1/deliveries/{id}/status` - Update status
- `GET /delivery-service/api/v1/deliveries/order/{orderId}` - Get by order
- `GET /delivery-service/api/v1/deliveries/{id}/tracking` - Get tracking

## Frontend Integration

The frontend already supports delivery tracking:

1. **Order Tracking Page** (`/track/{orderId}`)
   - Shows delivery status timeline
   - Shows agent information
   - Shows estimated delivery time

2. **Order Confirmation Page** (`/orders/{id}`)
   - Shows delivery details
   - Link to track order

## Testing Script

Run the automated test:
```bash
TEST_DELIVERY_SERVICE.bat
```

## Complete Documentation

See `DELIVERY_SERVICE_GUIDE.md` for:
- Complete API reference
- All endpoints with examples
- Request/response formats
- Complete workflow examples
- Frontend integration details

## Common Workflow

1. **Customer places order** (already working)
2. **System creates delivery** (manual or automatic)
3. **Delivery assigned to available agent**
4. **Agent updates status:**
   - PICKED_UP (from restaurant)
   - IN_TRANSIT (on the way)
   - DELIVERED (completed)
5. **Customer tracks in real-time** (frontend)

## Next Steps

1. Register a delivery agent
2. Place an order from frontend
3. Create a delivery for that order
4. Assign to the agent
5. Update delivery status
6. Track from frontend

Everything is ready to use! 🚀
