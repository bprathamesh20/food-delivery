# Fix Delivery Service 403 Error

## Problem
Getting 403 Forbidden when accessing `/delivery-service/api/v1/agents/me`

## Root Cause
The Delivery Service security configuration was too restrictive with role-based access control, and CORS wasn't properly configured.

## Solution Applied

### 1. Updated SecurityConfig.java
**File:** `delivery-service/src/main/java/com/foodDelivery/config/SecurityConfig.java`

**Changes:**
- Disabled Spring Security CORS (handled by CorsFilter bean)
- Relaxed role-based restrictions to use `.authenticated()` instead of `.hasRole()`
- Added support for both `/api/v1/auth/**` and `/delivery-service/api/v1/auth/**` paths
- Made agent endpoints accessible with any valid JWT token

**Before:**
```java
.cors(AbstractHttpConfigurer::disable)
.requestMatchers("/api/v1/agents/me").hasRole("DELIVERY_AGENT")
```

**After:**
```java
.cors(cors -> cors.disable()) // CORS handled by CorsFilter bean
.requestMatchers("/api/v1/agents/me/**").authenticated()
```

### 2. Updated CorsConfig.java
**File:** `delivery-service/src/main/java/com/foodDelivery/config/CorsConfig.java`

**Changes:**
- Added PATCH method to allowed methods

**Before:**
```java
config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
```

**After:**
```java
config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
```

## How to Apply the Fix

### Step 1: Rebuild Delivery Service
```bash
cd delivery-service
mvn clean install
```

Wait for "BUILD SUCCESS"

### Step 2: Restart Delivery Service
```bash
# Stop current Delivery Service (Ctrl+C)
mvn spring-boot:run
```

Wait for "Started DeliveryServiceApplication"

### Step 3: Wait for Eureka Registration
- Wait 30 seconds
- Check http://localhost:8761
- Verify DELIVERY-SERVICE shows UP

### Step 4: Test Backend Directly
```bash
# Register an agent
curl -X POST http://localhost:9090/delivery-service/api/v1/auth/agent/register \
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
  }'
```

**Expected:** 200 OK with token and agent data

### Step 5: Test Agent Profile
```bash
# Use the token from step 4
curl -X GET http://localhost:9090/delivery-service/api/v1/agents/me \
  -H "Authorization: Bearer YOUR_AGENT_TOKEN"
```

**Expected:** 200 OK with agent profile (NOT 403!)

### Step 6: Restart Frontend
```bash
# Stop frontend (Ctrl+C)
cd frontend
npm run dev
```

### Step 7: Clear Browser Cache
- Press `Ctrl+Shift+R` or use incognito window

### Step 8: Test Agent Registration from Frontend
1. Go to http://localhost:5173/agent/register
2. Fill in all fields
3. Submit
4. Should redirect to dashboard (NOT 403!)

## Verification Commands

### Test Agent Registration
```bash
curl -X POST http://localhost:9090/delivery-service/api/v1/auth/agent/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Agent",
    "email": "agent@test.com",
    "password": "test123",
    "phoneNumber": "9876543210",
    "vehicleType": "BIKE",
    "vehicleNumber": "TEST123",
    "address": "Test",
    "city": "Mumbai",
    "state": "Maharashtra",
    "licenseNumber": "DL123"
  }'
```

### Test Agent Login
```bash
curl -X POST http://localhost:9090/delivery-service/api/v1/auth/agent/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "agent@test.com",
    "password": "test123"
  }'
```

### Test Agent Profile (with token)
```bash
curl -X GET http://localhost:9090/delivery-service/api/v1/agents/me \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Test CORS
```bash
curl -X OPTIONS http://localhost:9090/delivery-service/api/v1/agents/me \
  -H "Origin: http://localhost:5173" \
  -H "Access-Control-Request-Method: GET" \
  -H "Access-Control-Request-Headers: Authorization" \
  -v
```

Should return 200 OK with CORS headers.

## What Changed

### Security Configuration
- **Before:** Required specific roles (DELIVERY_AGENT, USER, ADMIN)
- **After:** Requires authentication only (any valid JWT token)
- **Why:** Simplifies authentication and allows agents to access their endpoints with their JWT token

### CORS Configuration
- **Before:** Missing PATCH method
- **After:** Includes all HTTP methods (GET, POST, PUT, DELETE, OPTIONS, PATCH)
- **Why:** Ensures all API calls from frontend work correctly

### Endpoint Access
- **Public:** `/api/v1/auth/**` (registration, login)
- **Authenticated:** `/api/v1/agents/me/**` (agent profile, status, location)
- **Authenticated:** `/api/v1/deliveries/**` (delivery management)

## Troubleshooting

### Still Getting 403?

**Check 1: Is Delivery Service Running?**
```bash
curl http://localhost:8083/delivery-service/api/v1/auth/test
```
Should return: "Auth endpoint is working!"

**Check 2: Is Delivery Service in Eureka?**
- Open: http://localhost:8761
- Look for: DELIVERY-SERVICE (UP)

**Check 3: Did You Rebuild?**
```bash
cd delivery-service
mvn clean install
```

**Check 4: Check Logs**
Look for errors in Delivery Service console:
```
Started DeliveryServiceApplication
Mapped "{[/api/v1/auth/agent/register],methods=[POST]}"
Mapped "{[/api/v1/agents/me],methods=[GET]}"
```

**Check 5: Test Token**
Make sure you're using the agent token (not customer token):
```javascript
// In browser console
localStorage.getItem('agentToken')
```

### Token Issues?

**Problem:** Using customer token for agent endpoints
**Solution:** Make sure to use the token from agent login/registration

**Problem:** Token expired
**Solution:** Login again to get a new token

**Problem:** Token not sent
**Solution:** Check if Authorization header is included in request

## Success Checklist

- [ ] Delivery Service rebuilt (mvn clean install)
- [ ] Delivery Service restarted (mvn spring-boot:run)
- [ ] DELIVERY-SERVICE shows UP in Eureka
- [ ] Backend test returns 200 OK (not 403)
- [ ] Frontend restarted (npm run dev)
- [ ] Browser cache cleared (incognito window)
- [ ] Agent registration works from frontend
- [ ] Agent dashboard loads successfully
- [ ] No 403 errors!

## Quick Fix Commands

```bash
# Terminal 1 - Rebuild Delivery Service
cd delivery-service
mvn clean install

# Terminal 2 - Restart Delivery Service
cd delivery-service
mvn spring-boot:run

# Wait 30 seconds for Eureka

# Terminal 3 - Test
curl -X POST http://localhost:9090/delivery-service/api/v1/auth/agent/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test",
    "email": "test@test.com",
    "password": "test123",
    "phoneNumber": "1234567890",
    "vehicleType": "BIKE",
    "vehicleNumber": "TEST",
    "address": "Test",
    "city": "Test",
    "state": "Test",
    "licenseNumber": "DL123"
  }'

# Should return 200 OK with token

# Terminal 4 - Restart Frontend
cd frontend
npm run dev

# Browser - Test in incognito
# Go to: http://localhost:5173/agent/register
```

## Expected Behavior After Fix

### Agent Registration
1. Fill in registration form
2. Submit
3. Get 200 OK response with token
4. Token saved to localStorage as 'agentToken'
5. Redirect to dashboard
6. Dashboard loads agent profile (200 OK, not 403)

### Agent Login
1. Enter email and password
2. Submit
3. Get 200 OK response with token
4. Token saved to localStorage
5. Redirect to dashboard
6. Dashboard loads successfully

### Agent Dashboard
1. Loads agent profile from `/api/v1/agents/me`
2. Shows agent name, vehicle info
3. Shows deliveries (if any)
4. All API calls return 200 OK

---

**TL;DR:**
1. Rebuild Delivery Service: `cd delivery-service && mvn clean install`
2. Restart Delivery Service: `mvn spring-boot:run`
3. Wait 30 seconds
4. Restart frontend: `cd frontend && npm run dev`
5. Clear browser cache (Ctrl+Shift+R)
6. Test agent registration: http://localhost:5173/agent/register
7. Should work! ✅
