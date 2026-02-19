# Fix Agent Login 500 Error - Complete Guide

## Issues Identified

1. **500 Internal Server Error** on agent login - Backend issue
2. **403 Forbidden** on agent profile - Delivery Service not restarted
3. **Wrong token** being used - Frontend using customer token instead of agent token

## Solutions Applied

### 1. Frontend Token Management (✅ Fixed)

**File:** `frontend/src/services/api.js`

**Problem:** Delivery service was using customer token instead of agent token

**Solution:** 
- Delivery API now checks for `agentToken` first, then falls back to `token`
- Separate error handlers for customer and agent services
- Agent pages don't redirect to customer login

**Changes:**
```javascript
// Before:
const addAuthToken = (config) => {
  const token = localStorage.getItem('token');
  // ...
};
deliveryApi.interceptors.request.use(addAuthToken, ...);

// After:
const addAuthTokenDelivery = (config) => {
  const token = localStorage.getItem('agentToken') || localStorage.getItem('token');
  // ...
};
deliveryApi.interceptors.request.use(addAuthTokenDelivery, ...);
```

### 2. Backend Security Configuration (✅ Fixed)

**File:** `delivery-service/src/main/java/com/foodDelivery/config/SecurityConfig.java`

Already updated to allow authenticated access without strict role requirements.

### 3. Backend 500 Error (⚠️ Needs Investigation)

The 500 error on `/delivery-service/api/v1/auth/agent/login` indicates a backend problem.

**Possible causes:**
- Database connection issue
- Password encoding problem
- Missing agent in database
- JWT token generation error
- Null pointer exception

## Complete Fix Steps

### Step 1: Restart Frontend (For Token Fix)

```bash
# Stop frontend (Ctrl+C)
cd frontend
npm run dev
```

Clear browser cache (Ctrl+Shift+R) or use incognito window.

### Step 2: Rebuild and Restart Delivery Service

```bash
# Terminal 1 - Rebuild
cd delivery-service
mvn clean install

# Terminal 2 - Restart
cd delivery-service
mvn spring-boot:run
```

**Watch the console for errors!**

### Step 3: Check Delivery Service Logs

Look for these in the Delivery Service console:

**Good signs:**
```
Started DeliveryServiceApplication
Mapped "{[/api/v1/auth/agent/register],methods=[POST]}"
Mapped "{[/api/v1/auth/agent/login],methods=[POST]}"
Mapped "{[/api/v1/agents/me],methods=[GET]}"
```

**Bad signs (errors to look for):**
```
ERROR: Could not connect to database
ERROR: NullPointerException
ERROR: Failed to encode password
ERROR: JWT token generation failed
```

### Step 4: Test Backend Directly

#### Test 1: Agent Registration
```bash
curl -X POST http://localhost:9090/delivery-service/api/v1/auth/agent/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Agent",
    "email": "testagent@test.com",
    "password": "test123",
    "phoneNumber": "9876543210",
    "vehicleType": "BIKE",
    "vehicleNumber": "TEST123",
    "address": "Test Address",
    "city": "Mumbai",
    "state": "Maharashtra",
    "licenseNumber": "DL123456"
  }' \
  -v
```

**Expected:** 200 OK with token and agent data

**If you get 500:**
- Check Delivery Service logs for stack trace
- Look for database errors
- Check if all required fields are present

#### Test 2: Agent Login
```bash
curl -X POST http://localhost:9090/delivery-service/api/v1/auth/agent/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testagent@test.com",
    "password": "test123"
  }' \
  -v
```

**Expected:** 200 OK with token

**If you get 500:**
- Agent might not exist (register first)
- Password encoding issue
- Database connection problem

#### Test 3: Agent Profile (with token from step 1 or 2)
```bash
curl -X GET http://localhost:9090/delivery-service/api/v1/agents/me \
  -H "Authorization: Bearer YOUR_AGENT_TOKEN" \
  -v
```

**Expected:** 200 OK with agent profile

**If you get 403:**
- Token is invalid or expired
- Delivery Service not restarted
- Security configuration not loaded

### Step 5: Check Database

The Delivery Service uses its own database. Check if it's configured correctly:

**File:** `delivery-service/src/main/resources/application.properties` or `application.yml`

Look for:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/delivery_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=12345678
```

**Test database connection:**
```bash
mysql -u root -p12345678 -e "SHOW DATABASES;"
```

Should show `delivery_db` in the list.

### Step 6: Test from Frontend

1. **Clear everything:**
   ```javascript
   // In browser console
   localStorage.clear()
   ```

2. **Go to agent registration:**
   - http://localhost:5173/agent/register

3. **Fill in form and submit**

4. **Check browser console for errors**

5. **Check Network tab:**
   - Look for the POST request to `/delivery-service/api/v1/auth/agent/register`
   - Check status code (should be 200, not 500)
   - Check response body

## Debugging the 500 Error

### Check Delivery Service Logs

The 500 error will have a stack trace in the Delivery Service console. Look for:

**Common errors:**

1. **Database Connection:**
   ```
   ERROR: Could not open JDBC Connection
   ERROR: Access denied for user 'root'@'localhost'
   ```
   **Fix:** Check database credentials in application.properties

2. **Password Encoding:**
   ```
   ERROR: No PasswordEncoder mapped for id "null"
   ERROR: BCryptPasswordEncoder not found
   ```
   **Fix:** Check if PasswordEncoder bean is configured

3. **Null Pointer:**
   ```
   ERROR: NullPointerException at line X
   ```
   **Fix:** Check which field is null in the request

4. **JWT Token:**
   ```
   ERROR: Failed to generate JWT token
   ERROR: Secret key not configured
   ```
   **Fix:** Check JWT configuration in application.properties

### Enable Debug Logging

Add to `delivery-service/src/main/resources/application.properties`:
```properties
logging.level.com.foodDelivery=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.web=DEBUG
```

Restart Delivery Service and try again. You'll see detailed logs.

## Common Issues & Solutions

### Issue 1: Database Not Created
**Symptom:** 500 error with "Unknown database 'delivery_db'"

**Solution:**
```bash
mysql -u root -p12345678 -e "CREATE DATABASE IF NOT EXISTS delivery_db;"
```

### Issue 2: Password Encoder Missing
**Symptom:** 500 error with "No PasswordEncoder"

**Solution:** Check if `SecurityConfig` has:
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

### Issue 3: JWT Secret Not Configured
**Symptom:** 500 error with "JWT secret"

**Solution:** Add to application.properties:
```properties
jwt.secret=your-secret-key-here
jwt.expiration=86400000
```

### Issue 4: Agent Already Exists
**Symptom:** 500 error on registration with "Duplicate entry"

**Solution:** Use a different email or delete existing agent:
```bash
mysql -u root -p12345678 delivery_db -e "DELETE FROM delivery_agents WHERE email='testagent@test.com';"
```

### Issue 5: Frontend Using Wrong Token
**Symptom:** 403 error even after login

**Solution:** 
- Clear localStorage
- Make sure agentToken is saved (check browser console)
- Restart frontend

## Verification Checklist

- [ ] Frontend restarted
- [ ] Browser cache cleared
- [ ] Delivery Service rebuilt (mvn clean install)
- [ ] Delivery Service restarted (mvn spring-boot:run)
- [ ] No errors in Delivery Service console
- [ ] DELIVERY-SERVICE shows UP in Eureka (http://localhost:8761)
- [ ] Database exists and is accessible
- [ ] Backend registration test returns 200 OK (not 500)
- [ ] Backend login test returns 200 OK (not 500)
- [ ] Backend profile test returns 200 OK (not 403)
- [ ] Frontend registration works
- [ ] Frontend login works
- [ ] Dashboard loads successfully

## Quick Test Script

```bash
# 1. Test database
mysql -u root -p12345678 -e "SHOW DATABASES;" | grep delivery_db

# 2. Test registration
curl -X POST http://localhost:9090/delivery-service/api/v1/auth/agent/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Quick Test",
    "email": "quicktest@test.com",
    "password": "test123",
    "phoneNumber": "1234567890",
    "vehicleType": "BIKE",
    "vehicleNumber": "QT123",
    "address": "Test",
    "city": "Test",
    "state": "Test",
    "licenseNumber": "DL123"
  }'

# 3. If registration works, test login
curl -X POST http://localhost:9090/delivery-service/api/v1/auth/agent/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "quicktest@test.com",
    "password": "test123"
  }'

# 4. If both work, test from frontend
# Go to: http://localhost:5173/agent/register
```

## Next Steps

1. **Restart Delivery Service** and watch console for errors
2. **Test backend directly** with curl commands above
3. **Share the error** from Delivery Service console if 500 persists
4. **Check database** connection and configuration
5. **Enable debug logging** if needed

The frontend token issue is fixed. Now we need to fix the backend 500 error by checking the Delivery Service logs!

---

**Status:** Frontend fixed ✅, Backend needs investigation ⚠️
**Next:** Restart Delivery Service and check logs for 500 error details
