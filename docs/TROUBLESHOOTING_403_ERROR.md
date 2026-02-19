# Troubleshooting 403 Forbidden Error on /api/auth/signup

## Issue
Frontend shows: `Failed to load resource: the server responded with a status of 403 (Forbidden)` when trying to signup.

## Root Cause
The User Service needs proper CORS configuration to accept requests from the frontend (localhost:5173).

## Solution Applied ✅

### 1. Added CORS Configuration to User Service
Created: `user-service-backend/src/main/java/com/fooddelivery/auth/config/CorsConfig.java`

This configuration:
- Allows all origins (for development)
- Allows all headers
- Allows credentials
- Supports GET, POST, PUT, DELETE, OPTIONS, PATCH methods

### 2. Updated Security Configuration
Modified: `user-service-backend/src/main/java/com/fooddelivery/auth/config/SecurityConfig.java`

Changed CORS handling to use the CorsFilter bean instead of default Spring Security CORS.

## Steps to Fix

### 1. Rebuild User Service
```bash
cd user-service-backend
mvn clean install
```

### 2. Restart User Service
Stop the current User Service process (Ctrl+C) and restart:
```bash
cd user-service-backend
mvn spring-boot:run
```

### 3. Verify Service is Running
Check Eureka Dashboard: http://localhost:8761
- USER-SERVICE should be UP and registered

### 4. Test Signup Endpoint Directly
```bash
curl -X POST http://localhost:9090/api/auth/signup \
  -H "Content-Type: application/json" \
  -d "{\"fullName\":\"Test User\",\"email\":\"test@example.com\",\"phone\":\"1234567890\",\"password\":\"test123\",\"confirmPassword\":\"test123\"}"
```

Expected response: 200 OK with user data and JWT token

### 5. Test from Frontend
1. Open http://localhost:5173
2. Click "Sign Up"
3. Fill in the form
4. Submit

Should now work without 403 error!

## Additional Checks

### Check 1: User Service Logs
Look for these in User Service console:
```
Started UserServiceApplication in X seconds
Mapped "{[/api/auth/signup],methods=[POST]}"
```

### Check 2: API Gateway Logs
Look for routing information:
```
Mapped [/api/auth/**] to lb://USER-SERVICE
```

### Check 3: Browser Network Tab
1. Open browser DevTools (F12)
2. Go to Network tab
3. Try signup
4. Check the request:
   - URL: http://localhost:5173/api/auth/signup
   - Method: POST
   - Status: Should be 200 (not 403)
   - Response Headers: Should include CORS headers

### Check 4: Preflight Request
The browser sends an OPTIONS request first (CORS preflight):
- Request Method: OPTIONS
- Status: Should be 200
- Response Headers should include:
  - Access-Control-Allow-Origin: *
  - Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS, PATCH
  - Access-Control-Allow-Headers: *

## Common Issues

### Issue 1: Service Not Restarted
**Symptom:** Still getting 403 after changes
**Solution:** Make sure you stopped and restarted User Service

### Issue 2: Service Not Registered in Eureka
**Symptom:** Gateway returns 503 or 404
**Solution:** 
1. Check Eureka dashboard
2. Wait 30 seconds for registration
3. Restart User Service if not showing

### Issue 3: Wrong Port
**Symptom:** Connection refused
**Solution:** Verify User Service is on port 8000:
```bash
netstat -ano | findstr :8000
```

### Issue 4: Database Connection Error
**Symptom:** Service starts but crashes
**Solution:** 
1. Verify MySQL is running on port 3306
2. Check credentials: root / 12345678
3. Database will auto-create on first run

### Issue 5: Multiple Instances Running
**Symptom:** Inconsistent behavior
**Solution:** 
1. Stop all User Service instances
2. Check for processes on port 8000
3. Kill any existing processes
4. Start fresh instance

## Verification Commands

### Check if User Service is Running
```bash
# Windows
netstat -ano | findstr :8000

# Test health endpoint
curl http://localhost:8000/api/auth/health
```

### Check API Gateway Routing
```bash
# Get all routes
curl http://localhost:9090/actuator/gateway/routes

# Should show USER-SERVICE route
```

### Test CORS Headers
```bash
curl -X OPTIONS http://localhost:9090/api/auth/signup \
  -H "Origin: http://localhost:5173" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  -v
```

Should return CORS headers in response.

## Still Not Working?

### Full Reset Procedure

1. **Stop All Services**
   - Stop User Service
   - Stop API Gateway
   - Stop Eureka Server

2. **Clean Build**
   ```bash
   cd user-service-backend
   mvn clean install
   
   cd ../payments-service/gateway
   mvn clean install
   
   cd ../eureka-server
   mvn clean install
   ```

3. **Start in Order**
   ```bash
   # Terminal 1 - Eureka
   cd payments-service/eureka-server
   mvn spring-boot:run
   
   # Wait 30 seconds, then Terminal 2 - Gateway
   cd payments-service/gateway
   mvn spring-boot:run
   
   # Wait 30 seconds, then Terminal 3 - User Service
   cd user-service-backend
   mvn spring-boot:run
   ```

4. **Verify Each Step**
   - Eureka: http://localhost:8761
   - Gateway: http://localhost:9090
   - User Service: http://localhost:8000/api/auth/health

5. **Test Frontend**
   - Open http://localhost:5173
   - Try signup

## Success Indicators ✅

When working correctly, you should see:

1. **Browser Console:** No 403 errors
2. **Network Tab:** 
   - OPTIONS request: 200 OK
   - POST request: 200 OK with user data
3. **User Service Logs:** 
   ```
   Received signup request for email: test@example.com
   User created successfully
   ```
4. **Frontend:** 
   - Success toast notification
   - Redirect to home page
   - User name in navbar

## Need More Help?

Check these files:
- `user-service-backend/src/main/java/com/fooddelivery/auth/config/CorsConfig.java`
- `user-service-backend/src/main/java/com/fooddelivery/auth/config/SecurityConfig.java`
- `user-service-backend/src/main/resources/application.properties`
- `payments-service/gateway/src/main/resources/application.yaml`

All CORS and security configurations are now properly set up!
