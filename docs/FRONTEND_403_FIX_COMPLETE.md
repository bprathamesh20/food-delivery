# Frontend 403 Error - Complete Fix Guide

## Problem Summary
- ✅ Postman works: `http://localhost:9090/api/auth/login`
- ❌ Frontend fails: `http://localhost:5173/api/auth/login` (403 Forbidden)

## Root Causes Identified

### 1. Missing CORS Configuration in User Service
The User Service didn't have proper CORS configuration to handle browser preflight requests.

### 2. Browser Cache
Browser caches failed CORS preflight responses, causing persistent 403 errors even after backend fixes.

### 3. Frontend Dev Server Not Restarted
Vite dev server needs restart to pick up new proxy behavior after backend changes.

## Complete Solution

### Part 1: Backend Fix (Already Applied ✅)

**Files Created/Modified:**
1. `user-service-backend/src/main/java/com/fooddelivery/auth/config/CorsConfig.java` - NEW
2. `user-service-backend/src/main/java/com/fooddelivery/auth/config/SecurityConfig.java` - UPDATED

**What It Does:**
- Adds CORS filter to handle OPTIONS preflight requests
- Allows all origins, headers, and methods (for development)
- Enables credentials for authentication

### Part 2: Frontend Configuration (Already Applied ✅)

**Files Modified:**
1. `frontend/vite.config.js` - UPDATED with detailed proxy logging

**What It Does:**
- Proxies `/api/*` requests to `http://localhost:9090`
- Logs all proxy requests for debugging
- Handles WebSocket connections

### Part 3: Apply the Fix (YOU NEED TO DO THIS)

#### Step 1: Rebuild User Service
```bash
# Option A: Use the fix script
FIX_USER_SERVICE.bat

# Option B: Manual
cd user-service-backend
mvn clean install
```

#### Step 2: Restart User Service
```bash
# Stop current User Service (Ctrl+C in its terminal)
# Then restart:
cd user-service-backend
mvn spring-boot:run
```

#### Step 3: Wait for Registration
- Wait 30 seconds
- Check http://localhost:8761
- Verify USER-SERVICE shows as UP

#### Step 4: Restart Frontend
```bash
# Stop frontend dev server (Ctrl+C)
cd frontend
npm run dev
```

#### Step 5: Clear Browser Cache
**Choose one method:**

**Method A: Hard Refresh**
- Press `Ctrl+Shift+R` (Chrome/Edge/Firefox)

**Method B: Clear Site Data**
1. Open DevTools (F12)
2. Application tab → Clear site data
3. Refresh page

**Method C: Incognito Window (Easiest)**
1. Open new incognito/private window
2. Go to http://localhost:5173
3. Test signup/login

#### Step 6: Test
1. Open http://localhost:5173
2. Click "Sign Up"
3. Fill in form:
   - Full Name: John Doe
   - Email: john@example.com
   - Phone: 1234567890
   - Password: password123
   - Confirm Password: password123
4. Click "Sign Up"

**Expected Result:** ✅ Success! User created and logged in.

## Verification Commands

### Test Backend CORS
```bash
TEST_CORS.bat
```

This will test:
1. User Service health
2. Gateway health
3. OPTIONS preflight request
4. Signup with CORS headers
5. Login with CORS headers

All should return 200 OK.

### Manual CORS Test
```bash
# Test OPTIONS preflight
curl -X OPTIONS http://localhost:9090/api/auth/login \
  -H "Origin: http://localhost:5173" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  -v

# Should return 200 OK with CORS headers
```

### Test Login
```bash
curl -X POST http://localhost:9090/api/auth/login \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:5173" \
  -d "{\"emailOrPhone\":\"test@example.com\",\"password\":\"test123\"}" \
  -v

# Should return 200 OK with user data and token
```

## Debugging Steps

### Check 1: User Service Logs
Look for:
```
Started UserServiceApplication
Mapped "{[/api/auth/signup],methods=[POST]}"
Mapped "{[/api/auth/login],methods=[POST]}"
```

### Check 2: Browser Console
Open DevTools (F12) → Console tab

**Should NOT see:**
- ❌ `403 Forbidden`
- ❌ `CORS policy: No 'Access-Control-Allow-Origin' header`

**Should see:**
- ✅ Successful POST requests
- ✅ No error messages

### Check 3: Network Tab
Open DevTools (F12) → Network tab

**For each request, you should see TWO requests:**

1. **OPTIONS** `/api/auth/login`
   - Status: 200 OK
   - Type: preflight
   - Response Headers include CORS headers

2. **POST** `/api/auth/login`
   - Status: 200 OK
   - Type: xhr
   - Response includes user data and token

### Check 4: Response Headers
Click on OPTIONS request → Headers tab

**Should include:**
```
access-control-allow-origin: *
access-control-allow-methods: GET, POST, PUT, DELETE, OPTIONS, PATCH
access-control-allow-headers: *
access-control-allow-credentials: true
```

### Check 5: Vite Proxy Logs
In the terminal running `npm run dev`, you should see:
```
Sending Request to the Target: POST /api/auth/login
Received Response from the Target: 200 /api/auth/login
```

## Common Issues & Solutions

### Issue 1: Still Getting 403
**Cause:** Browser cache not cleared
**Solution:** 
- Use incognito window
- Or clear all site data
- Or try different browser

### Issue 2: User Service Not Starting
**Cause:** Port 8000 already in use
**Solution:**
```bash
# Windows - Find and kill process
netstat -ano | findstr :8000
taskkill /PID <process_id> /F
```

### Issue 3: Service Not in Eureka
**Cause:** Eureka registration delay or failure
**Solution:**
- Wait 30-60 seconds
- Check User Service logs for errors
- Restart User Service
- Verify `eureka.client.enabled=true` in application.properties

### Issue 4: Gateway Returns 503
**Cause:** User Service not registered in Eureka
**Solution:**
- Check Eureka dashboard (http://localhost:8761)
- Verify USER-SERVICE is UP
- Wait for registration to complete

### Issue 5: Vite Proxy Not Working
**Cause:** Frontend dev server not restarted
**Solution:**
- Stop frontend (Ctrl+C)
- Restart: `npm run dev`
- Check terminal for proxy logs

## Success Checklist

- [ ] User Service rebuilt with CORS config
- [ ] User Service restarted and running on port 8000
- [ ] User Service shows UP in Eureka (http://localhost:8761)
- [ ] Frontend dev server restarted
- [ ] Browser cache cleared (or using incognito)
- [ ] Testing in fresh browser tab
- [ ] Network tab shows OPTIONS request: 200 OK
- [ ] Network tab shows POST request: 200 OK
- [ ] No 403 errors in console
- [ ] Success toast notification appears
- [ ] User redirected to home page
- [ ] User name appears in navbar

## Files Reference

### Documentation
- `RESTART_FRONTEND.md` - Detailed restart instructions
- `TROUBLESHOOTING_403_ERROR.md` - Complete troubleshooting guide
- `FIX_APPLIED.md` - Summary of backend changes

### Scripts
- `FIX_USER_SERVICE.bat` - Rebuild User Service
- `TEST_CORS.bat` - Test CORS configuration
- `RUN_LOCALLY.bat` - Restart all services

### Code Files
- `user-service-backend/src/main/java/com/fooddelivery/auth/config/CorsConfig.java`
- `user-service-backend/src/main/java/com/fooddelivery/auth/config/SecurityConfig.java`
- `frontend/vite.config.js`

## Quick Fix (TL;DR)

```bash
# 1. Rebuild User Service
cd user-service-backend
mvn clean install

# 2. Restart User Service (in its terminal)
mvn spring-boot:run

# 3. Wait 30 seconds for Eureka

# 4. Restart Frontend (in its terminal)
cd frontend
npm run dev

# 5. Clear browser cache (Ctrl+Shift+R)

# 6. Test in incognito window
# Open http://localhost:5173
# Try signup/login
```

## Still Not Working?

### Nuclear Option: Full Reset

```bash
# 1. Stop ALL services (Ctrl+C in all terminals)

# 2. Clean build everything
cd user-service-backend
mvn clean install

cd ../payments-service/gateway
mvn clean install

cd ../eureka-server
mvn clean install

# 3. Start in order (separate terminals)
# Terminal 1
cd payments-service/eureka-server
mvn spring-boot:run

# Wait 30 seconds, then Terminal 2
cd payments-service/gateway
mvn spring-boot:run

# Wait 30 seconds, then Terminal 3
cd user-service-backend
mvn spring-boot:run

# Wait 30 seconds, then Terminal 4
cd frontend
npm run dev

# 4. Test in incognito window
```

## Expected Behavior After Fix

### Signup Flow
1. User fills signup form
2. Browser sends OPTIONS preflight → 200 OK
3. Browser sends POST request → 200 OK
4. Response includes JWT token and user data
5. Token saved to localStorage
6. User redirected to home page
7. Navbar shows user name
8. Success toast appears

### Login Flow
1. User fills login form
2. Browser sends OPTIONS preflight → 200 OK
3. Browser sends POST request → 200 OK
4. Response includes JWT token and user data
5. Token saved to localStorage
6. User redirected to previous page or home
7. Navbar shows user name
8. Success toast appears

## Support

If you're still experiencing issues after following all steps:

1. Run `TEST_CORS.bat` and share output
2. Check User Service logs for errors
3. Check browser console for error messages
4. Check Network tab for failed requests
5. Verify all services are UP in Eureka

---

**Status:** Fix ready to apply
**Last Updated:** February 13, 2026

**Next Step:** Run the commands in "Quick Fix (TL;DR)" section above!
