# RESTART DELIVERY SERVICE - STEP BY STEP

## The Problem
You're still seeing the "pickup_address cannot be null" error because the running Delivery Service is using OLD compiled code. The source code has been fixed, but you need to rebuild and restart.

## Step-by-Step Fix

### Step 1: Stop the Running Service
1. Go to the terminal/command prompt where Delivery Service is running
2. Press `Ctrl+C` to stop it
3. Wait for it to fully stop

### Step 2: Rebuild
Run this command from the project root:
```bash
cd delivery-service
mvn clean package -DskipTests
cd ..
```

OR use the quick script:
```bash
QUICK_FIX_DELIVERY.bat
```

### Step 3: Restart
```bash
java -jar delivery-service\target\delivery-service-0.0.1-SNAPSHOT.jar
```

### Step 4: Verify
Check the startup logs for:
```
Started DeliveryServiceApplication in X.XXX seconds
```

### Step 5: Test
1. Place a new order from the customer frontend
2. Check Delivery Service logs - should see:
   ```
   Received order event: OrderEvent{eventType='ORDER_CONFIRMED', orderId=X, ...}
   Handling ORDER_CREATED/ORDER_CONFIRMED event for orderId: X
   Pickup address not provided in event, using default: Restaurant #1
   Attempting to auto-assign delivery X to available agent
   ✅ Auto-assigned delivery X to agent John Doe
   Delivery created successfully: deliveryId=Y
   ```

## What Was Fixed

The `OrderEventConsumer.handleOrderCreated()` method now:
- ✅ Checks if `pickupAddress` is null or empty
- ✅ Uses default "Restaurant #X" if not provided
- ✅ Uses default coordinates (Pune, India) if not provided
- ✅ Uses default delivery fee (50.0) if not provided
- ✅ Validates that delivery address is present

## Why You're Still Seeing the Error

The error is from the OLD code that's currently running. The NEW code with null-safe handling is in the source files but hasn't been compiled and deployed yet.

**You MUST rebuild and restart for the fix to take effect!**

## Quick Checklist

- [ ] Stop the running Delivery Service (Ctrl+C)
- [ ] Rebuild: `mvn clean package -DskipTests` in delivery-service folder
- [ ] Restart: `java -jar delivery-service\target\delivery-service-0.0.1-SNAPSHOT.jar`
- [ ] Place a new order to test
- [ ] Check logs for "Delivery created successfully"

## If Still Not Working

1. Make sure you stopped the old service completely
2. Check that the build completed successfully (no errors)
3. Make sure you're running the newly built JAR file
4. Check the JAR timestamp to confirm it's new:
   ```bash
   dir delivery-service\target\delivery-service-0.0.1-SNAPSHOT.jar
   ```
   The timestamp should be recent (just now)
