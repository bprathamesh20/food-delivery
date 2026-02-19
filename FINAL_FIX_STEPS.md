# FINAL FIX - Complete Steps

## Current Issues
1. ❌ Request going to `localhost:5173` instead of being proxied to `localhost:9090`
2. ❌ Getting 403 Forbidden error
3. ❌ User Service not restarted with CORS config

## Root Causes
1. **Vite dev server not restarted** - Proxy not active
2. **User Service not restarted** - CORS config not loaded

## Complete Fix (Do ALL Steps)

### Part 1: Fix Backend (User Service)

#### Step 1.1: Stop User Service
- Go to terminal running User Service
- Press `Ctrl+C`

#### Step 1.2: Rebuild User Service
```bash
cd user-service-backend
mvn clean install
```
Wait for "BUILD SUCCESS"

#### Step 1.3: Restart User Service
```bash
mvn spring-boot:run
```
Wait for "Started UserServiceApplication"

#### Step 1.4: Verify Eureka Registration
- Wait 30 seconds
- Open: http://localhost:8761
- Check: USER-SERVICE shows UP (green)

#### Step 1.5: Test Backend Directly
```bash
curl -X POST http://localhost:9090/api/auth/signup -H "Content-Type: application/json" -d "{\"fullName\":\"Test\",\"email\":\"test@test.com\",\"phone\":\"1234567890\",\"password\":\"test123\",\"confirmPassword\":\"test123\"}"
```
**Expected:** 200 OK with user data (NOT 403!)

### Part 2: Fix Frontend (Vite Proxy)

#### Step 2.1: Stop Frontend Dev Server
- Go to terminal running `npm run dev`
- Press `Ctrl+C`

#### Step 2.2: Restart Frontend Dev Server
```bash
cd frontend
npm run dev
```

#### Step 2.3: Verify Vite Started
Terminal should show:
```
VITE v7.x.x  ready in xxx ms
➜  Local:   http://localhost:5173/
```

### Part 3: Fix Browser (Clear Cache)

#### Step 3.1: Clear Browser Cache
**Option A: Hard Refresh**
- Press `Ctrl+Shift+R`

**Option B: Incognito Window (Recommended)**
- Open new incognito/private window
- Go to http://localhost:5173

**Option C: Clear Site Data**
1. Open DevTools (F12)
2. Application tab
3. Click "Clear site data"
4. Refresh page

### Part 4: Test Everything

#### Step 4.1: Open Application
- Go to: http://localhost:5173 (in incognito window)
- Open DevTools (F12)
- Go to Console tab

#### Step 4.2: Try Signup
1. Click "Sign Up"
2. Fill in form:
   - Full Name: Siddhant
   - Email: sid@gmail.com
   - Phone: 9422217456
   - Password: pass123
   - Confirm Password: pass123
3. Click "Sign Up"

#### Step 4.3: Check Vite Terminal
Should see:
```
Sending Request to the Target: POST /api/auth/signup
Received Response from the Target: 200 /api/auth/signup
```

#### Step 4.4: Check Browser Console
Should see:
```
authService.signup called with: {fullName: "Siddhant", ...}
```

#### Step 4.5: Check Network Tab
1. Go to Network tab in DevTools
2. Look for `/api/auth/signup` request
3. Should show:
   - Status: 200 OK (NOT 403!)
   - Response: User data with token

## Success Indicators ✅

When everything is working:

### Vite Terminal:
```
Sending Request to the Target: POST /api/auth/signup
Received Response from the Target: 200 /api/auth/signup
```

### Browser Console:
```
authService.signup called with: {...}
(No 403 errors)
```

### Browser:
- ✅ Success toast: "Account created successfully!"
- ✅ Redirect to home page
- ✅ User name "Siddhant" appears in navbar
- ✅ "My Orders" link visible

## Troubleshooting

### Still Getting 403?

**Check 1: Is User Service Running?**
```bash
curl http://localhost:8000/api/auth/health
```
Should return: "Auth Service is running!"

**Check 2: Is User Service in Eureka?**
- Open: http://localhost:8761
- Look for: USER-SERVICE (UP)

**Check 3: Is Proxy Working?**
Check Vite terminal for proxy logs. If no logs appear, restart frontend.

**Check 4: Is Backend Accessible?**
```bash
curl -X POST http://localhost:9090/api/auth/signup -H "Content-Type: application/json" -d "{\"fullName\":\"Test\",\"email\":\"test@test.com\",\"phone\":\"1234567890\",\"password\":\"test123\",\"confirmPassword\":\"test123\"}"
```
Should return 200 OK.

### Request Still Going to 5173?

**Solution:** Restart frontend dev server
```bash
# Stop frontend (Ctrl+C)
cd frontend
npm run dev
```

### Still Not Working?

**Nuclear Option - Full Restart:**

```bash
# 1. Stop everything (Ctrl+C in all terminals)

# 2. Rebuild User Service
cd user-service-backend
mvn clean install

# 3. Start User Service
mvn spring-boot:run

# 4. Wait 30 seconds

# 5. Test backend
curl -X POST http://localhost:9090/api/auth/signup -H "Content-Type: application/json" -d "{\"fullName\":\"Test\",\"email\":\"test@test.com\",\"phone\":\"1234567890\",\"password\":\"test123\",\"confirmPassword\":\"test123\"}"

# 6. If backend returns 200 OK, start frontend
cd frontend
npm run dev

# 7. Test in incognito window
# Open: http://localhost:5173
```

## Quick Verification Commands

### Test User Service:
```bash
curl http://localhost:8000/api/auth/health
```

### Test API Gateway:
```bash
curl http://localhost:9090/actuator/health
```

### Test Signup (Backend):
```bash
curl -X POST http://localhost:9090/api/auth/signup -H "Content-Type: application/json" -d "{\"fullName\":\"Test\",\"email\":\"test@test.com\",\"phone\":\"1234567890\",\"password\":\"test123\",\"confirmPassword\":\"test123\"}"
```

### Test Proxy (Browser Console):
```javascript
fetch('/api/auth/health').then(r => r.text()).then(console.log)
```

## Checklist

- [ ] User Service stopped
- [ ] User Service rebuilt (mvn clean install)
- [ ] User Service restarted (mvn spring-boot:run)
- [ ] Waited 30 seconds
- [ ] USER-SERVICE shows UP in Eureka
- [ ] Backend test returns 200 OK (not 403)
- [ ] Frontend stopped
- [ ] Frontend restarted (npm run dev)
- [ ] Vite terminal shows startup message
- [ ] Browser cache cleared (incognito window)
- [ ] Tested signup from frontend
- [ ] Vite terminal shows proxy logs
- [ ] Browser shows success (no 403)

## Expected Final Result

After completing all steps:
1. ✅ User Service running with CORS config
2. ✅ Frontend proxy working correctly
3. ✅ Requests proxied from 5173 to 9090
4. ✅ Backend returns 200 OK
5. ✅ Signup works from frontend
6. ✅ User logged in and redirected
7. ✅ No 403 errors!

---

**IMPORTANT:** You must do BOTH parts:
1. Restart User Service (for CORS)
2. Restart Frontend (for Proxy)

Both are required for the fix to work!
