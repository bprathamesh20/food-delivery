# Debug: GET Request Instead of POST

## Problem
Vite proxy logs show:
```
Sending Request to the Target: GET /api/auth/login
Received Response from the Target: 500 /api/auth/login
```

But it should be POST, not GET!

## Possible Causes

### 1. Browser Navigation/Redirect
The browser might be navigating to `/login` URL instead of making an API call.

### 2. Form Submission Issue
The form might be submitting as a GET request (though unlikely with `e.preventDefault()`).

### 3. 401 Interceptor Redirect
The axios interceptor might be redirecting to `/login` which causes a GET request.

### 4. Browser Extension
A browser extension might be interfering with the request.

## Diagnostic Steps

### Step 1: Check Browser Console
1. Open DevTools (F12)
2. Go to Console tab
3. Try to login
4. Look for these logs:
   ```
   authService.login called with: {emailOrPhone: "...", password: "..."}
   Making POST request to /auth/login
   Login attempt with: {emailOrPhone: "..."}
   ```

**If you DON'T see these logs:**
- The form submission isn't working
- Check if `handleSubmit` is being called

**If you DO see these logs:**
- The POST request is being made
- Something is converting it to GET

### Step 2: Check Network Tab
1. Open DevTools (F12)
2. Go to Network tab
3. Clear all requests
4. Try to login
5. Look for requests to `/api/auth/login`

**What to check:**
- How many requests do you see?
- What are their methods (GET, POST, OPTIONS)?
- What are their status codes?
- Is there a redirect (301, 302, 307, 308)?

**Expected:**
1. OPTIONS `/api/auth/login` - 200 OK (preflight)
2. POST `/api/auth/login` - 200 OK (actual request)

**If you see GET:**
- Check if there's a redirect response
- Check the Initiator column (what triggered the request)

### Step 3: Check for Redirects
In Network tab, click on the first request:
- Check Response Headers for `Location` header
- Check Status Code (301, 302, 307, 308 are redirects)
- Check if there's a redirect chain

### Step 4: Disable Browser Extensions
1. Open incognito/private window (extensions usually disabled)
2. Try login again
3. If it works, a browser extension was interfering

### Step 5: Check Backend Response
The 500 error might be causing issues. Let's check what the backend is returning:

```bash
curl -X POST http://localhost:9090/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"emailOrPhone\":\"test@example.com\",\"password\":\"test123\"}" \
  -v
```

**Look for:**
- Status code (should be 200 or 401, not 500)
- Response body (error message)
- Response headers (any redirects?)

## Common Scenarios

### Scenario 1: User Doesn't Exist (Expected)
**Request:** POST with non-existent user
**Response:** 401 Unauthorized or 404 Not Found
**Frontend:** Shows error toast "Login failed"
**No GET request should happen**

### Scenario 2: Wrong Password (Expected)
**Request:** POST with wrong password
**Response:** 401 Unauthorized
**Frontend:** Shows error toast "Invalid credentials"
**No GET request should happen**

### Scenario 3: Server Error (Current Issue)
**Request:** POST
**Response:** 500 Internal Server Error
**Frontend:** Should show error toast
**Problem:** GET request is happening after the 500

## Solutions

### Solution 1: Fix the 500 Error
The root cause is the 500 error from the backend. Let's check User Service logs:

```bash
# Look for error stack traces in User Service console
# Common issues:
# - Database connection error
# - Null pointer exception
# - Password encoding issue
# - Missing user in database
```

### Solution 2: Prevent Redirect on Error
Update AuthContext to not navigate on error:

The current code already handles this correctly - it returns `{ success: false }` and doesn't navigate.

### Solution 3: Check if User Exists
Before testing login, make sure the user exists:

```bash
# Create a test user
curl -X POST http://localhost:9090/api/auth/signup \
  -H "Content-Type: application/json" \
  -d "{\"fullName\":\"Test User\",\"email\":\"test@example.com\",\"phone\":\"1234567890\",\"password\":\"test123\",\"confirmPassword\":\"test123\"}"
```

Then try login with the same credentials.

## Quick Test

### Test 1: Direct Backend Call
```bash
# First, create a user
curl -X POST http://localhost:9090/api/auth/signup \
  -H "Content-Type: application/json" \
  -d "{\"fullName\":\"Test User\",\"email\":\"test@example.com\",\"phone\":\"1234567890\",\"password\":\"test123\",\"confirmPassword\":\"test123\"}"

# Then, try to login
curl -X POST http://localhost:9090/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"emailOrPhone\":\"test@example.com\",\"password\":\"test123\"}"
```

**Expected:** 200 OK with token and user data

**If you get 500:**
- Check User Service logs
- Check database connection
- Check if user was created

**If you get 401:**
- Password might not match
- User might not exist

### Test 2: Frontend with Logging
1. Open http://localhost:5173/login
2. Open DevTools Console
3. Enter credentials:
   - Email: test@example.com
   - Password: test123
4. Click "Sign In"
5. Watch console for logs

**Expected logs:**
```
authService.login called with: {emailOrPhone: "test@example.com", password: "test123"}
Making POST request to /auth/login
Login attempt with: {emailOrPhone: "test@example.com"}
```

**Then either:**
- Success: "Login response: {data: {...}}"
- Error: "Login error: AxiosError {...}"

## Most Likely Cause

Based on the symptoms, the most likely cause is:

**The 500 error is happening, and then something (possibly the error handler or a redirect) is causing a GET request to `/login`.**

This could be:
1. The error response contains a redirect
2. The browser is trying to navigate after the error
3. The 401 interceptor is triggering (even though it's a 500)

## Immediate Fix

### Step 1: Check User Service Logs
Look for the actual error causing the 500:
```
ERROR: ...
Stack trace: ...
```

### Step 2: Create Test User
Make sure a user exists before testing login:
```bash
curl -X POST http://localhost:9090/api/auth/signup \
  -H "Content-Type: application/json" \
  -d "{\"fullName\":\"Test User\",\"email\":\"test@example.com\",\"phone\":\"1234567890\",\"password\":\"test123\",\"confirmPassword\":\"test123\"}"
```

### Step 3: Test Login from Backend
```bash
curl -X POST http://localhost:9090/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"emailOrPhone\":\"test@example.com\",\"password\":\"test123\"}"
```

If this returns 200 OK, the backend is fine and the issue is in the frontend.

If this returns 500, the backend has an issue that needs to be fixed first.

### Step 4: Test from Frontend
After confirming backend works, test from frontend with console open to see the logs.

## Next Steps

1. Check User Service logs for the 500 error
2. Share the error stack trace
3. Fix the backend error first
4. Then test frontend again

The GET request is likely a symptom, not the root cause. Fix the 500 error first!
