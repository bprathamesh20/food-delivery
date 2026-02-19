# Fix: Vite Proxy Not Working

## Problem
Request going to: `http://localhost:5173/api/auth/signup` (frontend)
Should go to: `http://localhost:9090/api/auth/signup` (backend via proxy)

## Root Cause
The Vite dev server needs to be restarted for the proxy configuration to take effect.

## Solution

### Step 1: Stop Frontend Dev Server
In the terminal running `npm run dev`, press `Ctrl+C`

### Step 2: Restart Frontend Dev Server
```bash
cd frontend
npm run dev
```

### Step 3: Verify Proxy is Working
You should see in the terminal:
```
VITE v7.x.x  ready in xxx ms

➜  Local:   http://localhost:5173/
➜  Network: use --host to expose
➜  press h + enter to show help
```

### Step 4: Test Signup
1. Open http://localhost:5173 (or refresh)
2. Open DevTools Console (F12)
3. Click "Sign Up"
4. Fill in form
5. Click "Sign Up"

**In the Vite terminal, you should now see:**
```
Sending Request to the Target: POST /api/auth/signup
Received Response from the Target: 200 /api/auth/signup
```

**In the browser Network tab, you should see:**
- Request URL: `http://localhost:5173/api/auth/signup`
- But it's proxied to: `http://localhost:9090/api/auth/signup`

## How Vite Proxy Works

1. Browser makes request to: `http://localhost:5173/api/auth/signup`
2. Vite dev server intercepts the request (because it matches `/api`)
3. Vite forwards it to: `http://localhost:9090/api/auth/signup`
4. Backend responds
5. Vite forwards response back to browser

The browser URL shows `localhost:5173` but the actual request goes to `localhost:9090`.

## Verification

### Check 1: Vite Terminal Shows Proxy Logs
After trying signup, you should see:
```
Sending Request to the Target: POST /api/auth/signup
Received Response from the Target: 200 /api/auth/signup
```

If you DON'T see these logs:
- Proxy is not working
- Restart frontend dev server

### Check 2: Browser Network Tab
1. Open DevTools (F12)
2. Go to Network tab
3. Try signup
4. Click on the `/api/auth/signup` request
5. Check "General" section:
   - Request URL: `http://localhost:5173/api/auth/signup`
   - Request Method: POST
   - Status Code: 200 OK (not 403!)

### Check 3: Backend is Running
Make sure API Gateway is running on 9090:
```bash
curl http://localhost:9090/actuator/health
```

Should return health status.

## Common Issues

### Issue 1: Proxy Not Working After Config Change
**Symptom:** Request still going to 5173, not proxied
**Solution:** Restart frontend dev server (Ctrl+C, then npm run dev)

### Issue 2: Backend Not Running
**Symptom:** Proxy works but gets connection refused
**Solution:** Start API Gateway on port 9090

### Issue 3: Wrong Port
**Symptom:** Proxy trying to connect to wrong port
**Solution:** Check vite.config.js has `target: 'http://localhost:9090'`

### Issue 4: CORS Still Failing
**Symptom:** Proxy works but still getting 403
**Solution:** Restart User Service with CORS config (see APPLY_FIX_NOW.md)

## Complete Fix Sequence

To fix everything:

### 1. Restart User Service (for CORS)
```bash
cd user-service-backend
mvn clean install
mvn spring-boot:run
```

### 2. Wait for Eureka Registration
Wait 30 seconds, check http://localhost:8761

### 3. Restart Frontend (for Proxy)
```bash
cd frontend
npm run dev
```

### 4. Clear Browser Cache
Press `Ctrl+Shift+R` or use incognito window

### 5. Test
Go to http://localhost:5173 and try signup

## Expected Behavior

### Vite Terminal:
```
Sending Request to the Target: POST /api/auth/signup
Received Response from the Target: 200 /api/auth/signup
```

### Browser Console:
```
authService.signup called with: {fullName: "...", email: "...", ...}
Login response: {data: {token: "...", id: 1, ...}}
```

### Browser Network Tab:
- Request URL: `http://localhost:5173/api/auth/signup`
- Status: 200 OK
- Response: User data with token

### Result:
✅ Success toast notification
✅ Redirect to home page
✅ User name in navbar

## Quick Test

After restarting frontend, open browser console and run:
```javascript
fetch('/api/auth/health')
  .then(r => r.text())
  .then(console.log)
```

Should print: "Auth Service is running!"

If it works, the proxy is working correctly!

---

**TL;DR:**
1. Stop frontend (Ctrl+C)
2. Restart frontend (npm run dev)
3. Clear browser cache (Ctrl+Shift+R)
4. Try signup again
5. Check Vite terminal for proxy logs
