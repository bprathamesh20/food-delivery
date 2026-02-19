# Apply the CORS Fix NOW - Step by Step

## Current Status
❌ Frontend getting 403 Forbidden on signup/login
✅ CORS fix code is ready
⏳ User Service needs to be rebuilt and restarted

## Follow These Steps EXACTLY

### Step 1: Stop User Service
In the terminal running User Service, press `Ctrl+C`

### Step 2: Rebuild User Service
Open a new terminal and run:
```bash
cd user-service-backend
mvn clean install
```

Wait for "BUILD SUCCESS" message.

### Step 3: Restart User Service
In the same terminal:
```bash
mvn spring-boot:run
```

Wait for these messages:
```
Started UserServiceApplication in X seconds
Mapped "{[/api/auth/signup],methods=[POST]}"
Mapped "{[/api/auth/login],methods=[POST]}"
```

### Step 4: Wait for Eureka Registration
Wait 30 seconds, then check: http://localhost:8761

Look for USER-SERVICE with status UP (green).

### Step 5: Test Backend Directly
Open a new terminal and run:
```bash
curl -X POST http://localhost:9090/api/auth/signup -H "Content-Type: application/json" -d "{\"fullName\":\"Test User\",\"email\":\"test@example.com\",\"phone\":\"1234567890\",\"password\":\"test123\",\"confirmPassword\":\"test123\"}"
```

**Expected:** 200 OK with user data and token (not 403!)

### Step 6: Restart Frontend
In the terminal running frontend, press `Ctrl+C`, then:
```bash
npm run dev
```

### Step 7: Clear Browser Cache
Press `Ctrl+Shift+R` or open an incognito window

### Step 8: Test Signup
1. Go to http://localhost:5173
2. Click "Sign Up"
3. Fill in form:
   - Full Name: Siddhant
   - Email: sid@gmail.com
   - Phone: 9422217456
   - Password: pass123
   - Confirm Password: pass123
4. Click "Sign Up"

**Expected:** ✅ Success! User created and logged in.

## If Still Getting 403

### Check 1: Is User Service Running?
```bash
curl http://localhost:8000/api/auth/health
```
Should return: "Auth Service is running!"

### Check 2: Is User Service in Eureka?
Open: http://localhost:8761
Look for: USER-SERVICE (status UP)

### Check 3: Test CORS Headers
```bash
curl -X OPTIONS http://localhost:9090/api/auth/signup -H "Origin: http://localhost:5173" -H "Access-Control-Request-Method: POST" -v
```
Should return 200 OK with CORS headers.

### Check 4: Did You Rebuild?
Make sure you ran `mvn clean install` in user-service-backend folder.

### Check 5: Check User Service Logs
Look for:
```
CorsFilter bean created
CorsConfiguration loaded
```

## Quick Verification Checklist

- [ ] User Service stopped (Ctrl+C)
- [ ] Ran `mvn clean install` in user-service-backend
- [ ] Saw "BUILD SUCCESS"
- [ ] Ran `mvn spring-boot:run`
- [ ] Saw "Started UserServiceApplication"
- [ ] Waited 30 seconds
- [ ] USER-SERVICE shows UP in Eureka (http://localhost:8761)
- [ ] Backend test with curl returns 200 OK (not 403)
- [ ] Frontend restarted
- [ ] Browser cache cleared (Ctrl+Shift+R or incognito)
- [ ] Tested signup from frontend

## Still Not Working?

Run the full reset:

```bash
# Terminal 1 - Stop User Service (Ctrl+C)

# Terminal 2 - Rebuild
cd user-service-backend
mvn clean install -DskipTests

# Terminal 3 - Restart
cd user-service-backend
mvn spring-boot:run

# Wait 30 seconds

# Terminal 4 - Test
curl -X POST http://localhost:9090/api/auth/signup -H "Content-Type: application/json" -d "{\"fullName\":\"Test\",\"email\":\"test@test.com\",\"phone\":\"1234567890\",\"password\":\"test123\",\"confirmPassword\":\"test123\"}"

# Should return 200 OK with user data
```

## The Fix is Ready!

The CORS configuration files are already created:
- ✅ `user-service-backend/src/main/java/com/fooddelivery/auth/config/CorsConfig.java`
- ✅ `user-service-backend/src/main/java/com/fooddelivery/auth/config/SecurityConfig.java`

You just need to rebuild and restart the User Service to load them!

---

**TL;DR:**
1. Stop User Service (Ctrl+C)
2. `cd user-service-backend && mvn clean install`
3. `mvn spring-boot:run`
4. Wait 30 seconds
5. Test with curl (should get 200, not 403)
6. Restart frontend
7. Clear browser cache
8. Try signup again
