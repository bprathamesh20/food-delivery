# Quick Start Guide - Food Delivery System

## Prerequisites
- Java 17+
- Maven
- MySQL (running on port 3306)
- Kafka (running on port 9098)
- Node.js 18+ and npm

---

## Step 1: Start Backend Services

### Option A: Using Batch File (Recommended)
```bash
RUN_LOCALLY.bat
```

This will start all services in order:
1. Eureka Server (8761)
2. API Gateway (9090)
3. User Service (8000)
4. Restaurant Service (8082)
5. Order Service (8084)
6. Payment Service (8085)
7. Delivery Service (8083)
8. Notification Service (8086)

### Option B: Manual Start
Open 8 separate terminals and run:

```bash
# Terminal 1 - Eureka Server
cd payments-service/eureka-server
mvn spring-boot:run

# Terminal 2 - API Gateway
cd payments-service/gateway
mvn spring-boot:run

# Terminal 3 - User Service
cd user-service-backend
mvn spring-boot:run

# Terminal 4 - Restaurant Service
cd restaurant-service
mvn spring-boot:run

# Terminal 5 - Order Service
cd Food-deli-order_service
mvn spring-boot:run

# Terminal 6 - Payment Service
cd payments-service/payment/demo
mvn spring-boot:run

# Terminal 7 - Delivery Service
cd delivery-service
mvn spring-boot:run

# Terminal 8 - Notification Service
cd notification-service/notification
mvn spring-boot:run
```

---

## Step 2: Verify Services

### Check Eureka Dashboard
Open: http://localhost:8761

You should see all services registered:
- USER-SERVICE
- RESTAURANT-SERVICE
- ORDER-SERVICE
- PAYMENT-SERVICE
- DELIVERY-SERVICE
- NOTIFICATION-SERVICE
- API-GATEWAY

### Check API Gateway
Open: http://localhost:9090

Should return a response (might be 404, that's okay - it means gateway is running)

---

## Step 3: Start Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend will be available at: http://localhost:5173

---

## Step 4: Test the Application

### 1. Register a New User
1. Go to http://localhost:5173
2. Click "Sign Up"
3. Fill in:
   - Full Name: John Doe
   - Email: john@example.com
   - Phone: 1234567890
   - Password: password123
   - Confirm Password: password123
4. Click "Sign Up"

### 2. Create a Restaurant (Using API)
```bash
curl -X POST http://localhost:9090/api/restaurants/register \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Pizza Palace\",\"address\":\"123 Main St, City\"}"
```

### 3. Add Menu Items (Using API)
```bash
# Get restaurant ID from previous response, then:
curl -X POST http://localhost:9090/api/menus/1 \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Margherita Pizza\",\"price\":299.99,\"available\":true}"

curl -X POST http://localhost:9090/api/menus/1 \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Pepperoni Pizza\",\"price\":349.99,\"available\":true}"

curl -X POST http://localhost:9090/api/menus/1 \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Garlic Bread\",\"price\":99.99,\"available\":true}"
```

### 4. Browse and Order
1. Go to "Restaurants" in the navbar
2. Click on "Pizza Palace"
3. Add items to cart
4. Click cart icon
5. Click "Proceed to Checkout"
6. Enter delivery address
7. Click "Place Order"
8. View order confirmation
9. Track your order

---

## Common Issues & Solutions

### Issue: Services not starting
**Solution:** Check if ports are already in use
```bash
# Windows
netstat -ano | findstr :8761
netstat -ano | findstr :9090

# Kill process if needed
taskkill /PID <process_id> /F
```

### Issue: Eureka shows services as DOWN
**Solution:** 
1. Wait 30 seconds for registration
2. Check service logs for errors
3. Verify `application.yaml` has correct Eureka URL

### Issue: Frontend API calls failing
**Solution:**
1. Check API Gateway is running on 9090
2. Open browser console for error details
3. Verify CORS is enabled in gateway
4. Check if services are registered in Eureka

### Issue: Database connection errors
**Solution:**
1. Verify MySQL is running on port 3306
2. Check username: root, password: 12345678
3. Databases should auto-create on first run

### Issue: Kafka connection errors
**Solution:**
1. Verify Kafka is running on port 9098
2. Check Kafka is in KRaft mode (no Zookeeper)
3. Restart services after Kafka is up

---

## Quick Test Commands

### Test User Service
```bash
# Health check
curl http://localhost:9090/api/auth/health

# Signup
curl -X POST http://localhost:9090/api/auth/signup \
  -H "Content-Type: application/json" \
  -d "{\"fullName\":\"Test User\",\"email\":\"test@test.com\",\"phone\":\"9876543210\",\"password\":\"test123\",\"confirmPassword\":\"test123\"}"
```

### Test Restaurant Service
```bash
# Get all restaurants
curl http://localhost:9090/api/restaurants

# Get restaurant by ID
curl http://localhost:9090/api/restaurants/1

# Get menu
curl http://localhost:9090/api/menus/1
```

### Test Order Service
```bash
# Create order (replace token with actual JWT)
curl -X POST http://localhost:9090/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d "{\"customerId\":1,\"restaurantId\":1,\"deliveryAddress\":\"456 Oak Ave\",\"items\":[{\"menuItemId\":1,\"quantity\":2}]}"
```

---

## Stopping Services

### Stop Frontend
Press `Ctrl+C` in the terminal running `npm run dev`

### Stop Backend Services
Press `Ctrl+C` in each terminal running a service

Or if using batch file, close all terminal windows

---

## Development Tips

### Hot Reload
- Frontend: Changes auto-reload (Vite HMR)
- Backend: Restart service after code changes

### Logs
- Check terminal output for each service
- Look for ERROR or WARN messages
- Kafka events logged in Notification Service

### Database
- Connect to MySQL: `mysql -u root -p12345678`
- View databases: `SHOW DATABASES;`
- View tables: `USE user_db; SHOW TABLES;`

---

## Next Steps

1. ✅ All services running
2. ✅ Frontend accessible
3. ✅ User can register/login
4. ✅ Restaurants and menus created
5. ✅ Orders can be placed
6. ✅ Order tracking works

**You're all set! Start ordering food! 🍕🍔🍜**

---

For detailed information, see:
- `FRONTEND_IMPLEMENTATION_COMPLETE.md` - Complete frontend documentation
- `BACKEND_API_COMPLETE_MAPPING.md` - All API endpoints
- `BUILD_AND_RUN.md` - Detailed build instructions
