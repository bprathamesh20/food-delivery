# Fix: Frontend 403 Error - Restart Required

## Problem
- Postman works: `http://localhost:9090/api/auth/login` ✅
- Frontend fails: `http://localhost:5173/api/auth/login` ❌ (403 Forbidden)

## Root Cause
The Vite dev server needs to be restarted after backend CORS changes. The proxy is working, but the browser is caching the failed CORS preflight response.

## Solution

### Step 1: Stop Frontend Dev Server
In the terminal running `npm run dev`, press `Ctrl+C`

### Step 2: Clear Browser Cache
**Option A: Hard Refresh**
- Chrome/Edge: `Ctrl+Shift+R` or `Ctrl+F5`
- Firefox: `Ctrl+Shift+R`

**Option B: Clear Site Data (Recommended)**
1. Open DevTools (F12)
2. Go to Application tab (Chrome) or Storage tab (Firefox)
3. Click "Clear site data" or "Clear storage"
4. Refresh page

**Option C: Incognito/Private Window**
- Open a new incognito/private window
- Navigate to http://localhost:5173

### Step 3: Restart Frontend
```bash
cd frontend
npm run dev
```

### Step 4: Test Again
1. Open http://localhost:5173 (or refresh incognito window)
2. Click "Sign Up" or "Login"
3. Fill in form and submit
4. Should work now! ✅

## Why This Happens

### CORS Preflight Caching
1. Browser sends OPTIONS request (preflight)
2. If server responds with 403, browser caches this
3. Even after fixing backend, browser uses cached response
4. Clearing cache forces new preflight request

### Vite Proxy
The Vite proxy forwards requests:
- Frontend: `http://localhost:5173/api/auth/login`
- Proxy to: `http://localhost:9090/api/auth/login`
- Backend: User Service via Gateway

## Verification Steps

### 1. Check Vite Dev Server is Running
Terminal should show:
```
VITE v7.x.x  ready in xxx ms
➜  Local:   http://localhost:5173/
```

### 2. Check Browser Console
Should NOT see:
- ❌ 403 Forbidden errors
- ❌ CORS policy errors

Should see:
- ✅ Successful POST requests
- ✅ 200 OK responses

### 3. Check Network Tab
1. Open DevTools (F12)
2. Go to Network tab
3. Try login/signup
4. Look for two requests:
   - **OPTIONS** `/api/auth/login` - Status: 200 (preflight)
   - **POST** `/api/auth/login` - Status: 200 (actual request)

### 4. Check Response Headers
Click on the OPTIONS request, check Response Headers:
```
access-control-allow-origin: *
access-control-allow-methods: GET, POST, PUT, DELETE, OPTIONS, PATCH
access-control-allow-headers: *
access-control-allow-credentials: true
```

## Still Not Working?

### Full Reset Procedure

1. **Stop Everything**
   ```bash
   # Stop frontend (Ctrl+C)
   # Stop User Service (Ctrl+C)
   ```

2. **Rebuild User Service**
   ```bash
   cd user-service-backend
   mvn clean install
   ```

3. **Restart User Service**
   ```bash
   mvn spring-boot:run
   ```

4. **Wait for Eureka Registration**
   - Wait 30 seconds
   - Check http://localhost:8761
   - USER-SERVICE should be UP

5. **Clear Browser Completely**
   - Close all browser windows
   - Reopen browser
   - Or use incognito mode

6. **Restart Frontend**
   ```bash
   cd frontend
   npm run dev
   ```

7. **Test in Fresh Browser Tab**
   - Open http://localhost:5173
   - Try signup/login

## Alternative: Direct Backend Testing

If frontend still has issues, test backend directly:

### Test Signup
```bash
curl -X POST http://localhost:9090/api/auth/signup \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:5173" \
  -d "{\"fullName\":\"Test User\",\"email\":\"test@example.com\",\"phone\":\"1234567890\",\"password\":\"test123\",\"confirmPassword\":\"test123\"}" \
  -v
```

### Test Login
```bash
curl -X POST http://localhost:9090/api/auth/login \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:5173" \
  -d "{\"emailOrPhone\":\"test@example.com\",\"password\":\"test123\"}" \
  -v
```

Both should return 200 OK with CORS headers.

## Quick Checklist

- [ ] User Service rebuilt with CORS config
- [ ] User Service restarted and running
- [ ] User Service registered in Eureka (UP status)
- [ ] Frontend dev server restarted
- [ ] Browser cache cleared
- [ ] Testing in fresh browser tab/incognito
- [ ] Network tab shows 200 for OPTIONS request
- [ ] Network tab shows 200 for POST request

## Success Indicators ✅

When working:
1. No 403 errors in console
2. OPTIONS request returns 200
3. POST request returns 200
4. Success toast notification appears
5. User redirected to home page
6. User name appears in navbar

## Common Mistakes

### ❌ Mistake 1: Not Restarting Frontend
Backend changes don't affect running frontend dev server.
**Fix:** Always restart frontend after backend changes.

### ❌ Mistake 2: Not Clearing Cache
Browser caches CORS preflight responses.
**Fix:** Hard refresh or clear site data.

### ❌ Mistake 3: Testing in Same Tab
Old cache might persist in current tab.
**Fix:** Open new tab or incognito window.

### ❌ Mistake 4: User Service Not Restarted
CORS config not loaded if service not restarted.
**Fix:** Rebuild and restart User Service.

### ❌ Mistake 5: Wrong Port
Testing on wrong port.
**Fix:** Frontend must be on 5173, backend on 9090.

## Need More Help?

1. Check User Service logs for CORS-related messages
2. Check API Gateway logs for routing issues
3. Check browser console for detailed error messages
4. Verify all services are UP in Eureka dashboard
5. Test with curl to isolate frontend vs backend issues

---

**TL;DR:**
1. Stop frontend (Ctrl+C)
2. Clear browser cache (Ctrl+Shift+R)
3. Restart frontend (`npm run dev`)
4. Test in fresh tab or incognito
5. Should work! ✅
