# Notification UI Fix - API Gateway Route ✅

## Problem
Notifications are being created in Kafka and database, but not showing in the UI.

## Root Cause
The API Gateway didn't have a route for `/notification-service/**` pattern that the frontend is using.

**Frontend calls**: `/notification-service/api/v1/notifications`
**Gateway had**: `/api/notifications/**` only

## Solution Applied

### 1. Updated API Gateway Routes
**File**: `payments-service/gateway/src/main/resources/application.yaml`

Added a new route for the notification service:
```yaml
# Notification Service Routes
- id: notification-service-direct
  uri: lb://NOTIFICATION-SERVICE
  predicates:
    - Path=/notification-service/**
  filters:
    - StripPrefix=1  # Removes /notification-service prefix
    
- id: notification-service
  uri: lb://NOTIFICATION-SERVICE
  predicates:
    - Path=/api/notifications/**
  filters:
    - StripPrefix=0
```

**How it works**:
- Request: `http://localhost:9090/notification-service/api/v1/notifications?userId=13&userType=CUSTOMER`
- After StripPrefix=1: `/api/v1/notifications?userId=13&userType=CUSTOMER`
- Forwarded to: `http://NOTIFICATION-SERVICE/api/v1/notifications?userId=13&userType=CUSTOMER`

### 2. Added Debug Logging to Frontend
**File**: `frontend/src/components/NotificationBell.jsx`

Added console.log statements to help debug:
```javascript
console.log('Fetching unread count for userId:', userId, 'userType:', userType);
console.log('Unread count response:', response.data);
console.log('Error details:', error.response?.data);
```

## How to Apply

### Step 1: Rebuild API Gateway
```bash
REBUILD_GATEWAY.bat
```

Or manually:
```bash
cd payments-service\gateway
mvn clean package -DskipTests
cd ..\..
```

### Step 2: Restart API Gateway
Stop the current gateway (Ctrl+C) and restart:
```bash
java -jar payments-service\gateway\target\gateway-0.0.1-SNAPSHOT.jar
```

### Step 3: Test in Browser
1. Open browser console (F12)
2. Login as customer
3. Click the notification bell icon
4. Check console logs for:
   ```
   Fetching unread count for userId: 13 userType: CUSTOMER
   Unread count response: {count: 2}
   Fetching notifications for userId: 13 userType: CUSTOMER
   Notifications response: [{id: 1, title: "Order Confirmed", ...}, ...]
   ```

## Verification Steps

### Test 1: Check API Gateway Routes
```bash
curl http://localhost:9090/actuator/gateway/routes | jq
```

Should show the notification-service-direct route.

### Test 2: Direct API Call
```bash
curl "http://localhost:9090/notification-service/api/v1/notifications/unread/count?userId=13&userType=CUSTOMER"
```

Should return:
```json
{"count": 2}
```

### Test 3: Frontend UI
1. Login as customer
2. Place an order
3. Wait 30 seconds (auto-refresh)
4. Bell icon should show red badge with count
5. Click bell to see notifications dropdown

### Test 4: Database Check
```sql
SELECT * FROM notification_db.notifications 
WHERE user_id = 13 AND user_type = 'CUSTOMER' 
ORDER BY created_at DESC;
```

## Common Issues

### Issue 1: 404 Not Found
**Symptom**: `GET /notification-service/api/v1/notifications 404`
**Solution**: Make sure API Gateway is restarted with new routes

### Issue 2: 503 Service Unavailable
**Symptom**: `GET /notification-service/api/v1/notifications 503`
**Solution**: 
- Check Notification Service is running
- Check Eureka registration: http://localhost:8761
- Verify service name is `NOTIFICATION-SERVICE`

### Issue 3: Empty Response
**Symptom**: `{count: 0}` or `[]` even though notifications exist in DB
**Solution**:
- Check userId matches the logged-in user
- Check userType is "CUSTOMER" (not "customer")
- Verify notifications in DB have correct user_id and user_type

### Issue 4: CORS Error
**Symptom**: `Access-Control-Allow-Origin` error in console
**Solution**:
- API Gateway has global CORS enabled
- NotificationController has `@CrossOrigin(origins = "*")`
- Should not be an issue, but restart both if needed

## API Endpoints

All accessible through API Gateway at `http://localhost:9090`:

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/notification-service/api/v1/notifications?userId={id}&userType={type}` | GET | Get all notifications |
| `/notification-service/api/v1/notifications/unread?userId={id}&userType={type}` | GET | Get unread only |
| `/notification-service/api/v1/notifications/unread/count?userId={id}&userType={type}` | GET | Get unread count |
| `/notification-service/api/v1/notifications/{id}/read` | PUT | Mark as read |
| `/notification-service/api/v1/notifications/read-all?userId={id}&userType={type}` | PUT | Mark all as read |

## Summary

✅ Added API Gateway route for `/notification-service/**`
✅ Added debug logging to frontend
✅ Notifications now accessible from UI
✅ Auto-refresh every 30 seconds
✅ Click to mark as read
✅ Unread count badge

After restarting the API Gateway, notifications should appear in the UI!
