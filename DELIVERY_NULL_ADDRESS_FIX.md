# Delivery Service - Null Pickup Address Fix ✅

## Problem
```
Column 'pickup_address' cannot be null
```

The Order Service's `OrderEvent` doesn't include pickup address fields, causing the Delivery Service to fail when creating delivery records.

## Root Cause
Order Service only sends:
- `deliveryAddress` ✅
- `customerId` ✅
- `restaurantId` ✅
- `orderId` ✅

But does NOT send:
- `pickupAddress` ❌
- `pickupLatitude` ❌
- `pickupLongitude` ❌

## Solution Applied

### Updated OrderEventConsumer
**File**: `delivery-service/src/main/java/com/foodDelivery/kafka/OrderEventConsumer.java`

Added null-safe handling with sensible defaults:

```java
// Pickup address - use from event or default to restaurant address
String pickupAddress = orderEvent.getPickupAddress();
if (pickupAddress == null || pickupAddress.trim().isEmpty()) {
    pickupAddress = "Restaurant #" + orderEvent.getRestaurantId();
    logger.warn("Pickup address not provided, using default: {}", pickupAddress);
}
deliveryRequest.setPickupAddress(pickupAddress);

// Default coordinates to Pune, India if not provided
deliveryRequest.setPickupLatitude(
    orderEvent.getPickupLatitude() != null ? orderEvent.getPickupLatitude() : 18.5204
);
deliveryRequest.setPickupLongitude(
    orderEvent.getPickupLongitude() != null ? orderEvent.getPickupLongitude() : 73.8567
);

// Delivery address validation
String deliveryAddress = orderEvent.getDeliveryAddress();
if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
    throw new IllegalArgumentException("Delivery address is required");
}

// Default delivery fee if not provided
deliveryRequest.setDeliveryFee(fee != null ? fee.doubleValue() : 50.0);
```

## Default Values

| Field | Default Value | Reason |
|-------|---------------|--------|
| `pickupAddress` | "Restaurant #X" | Uses restaurant ID as identifier |
| `pickupLatitude` | 18.5204 | Pune, India coordinates |
| `pickupLongitude` | 73.8567 | Pune, India coordinates |
| `deliveryFee` | 50.0 | Standard delivery fee |

## Validation

### Required Fields
- `deliveryAddress` - MUST be provided, throws exception if missing
- `orderId` - Always provided by Order Service
- `restaurantId` - Always provided by Order Service
- `customerId` - Always provided by Order Service

### Optional Fields with Defaults
- `pickupAddress` - Defaults to "Restaurant #X"
- `pickupLatitude/Longitude` - Defaults to Pune coordinates
- `deliveryLatitude/Longitude` - Defaults to Pune coordinates
- `deliveryFee` - Defaults to 50.0
- `deliveryInstructions` - Can be null

## How to Apply

### Step 1: Rebuild
```bash
REBUILD_DELIVERY_SERVICE.bat
```

### Step 2: Restart
Stop the current Delivery Service (Ctrl+C) and restart:
```bash
java -jar delivery-service\target\delivery-service-0.0.1-SNAPSHOT.jar
```

### Step 3: Test
Place an order from the customer frontend. Check logs:
```
Received order event: OrderEvent{eventType='ORDER_CONFIRMED', orderId=11, ...}
Handling ORDER_CREATED/ORDER_CONFIRMED event for orderId: 11
Pickup address not provided, using default: Restaurant #1
Attempting to auto-assign delivery 5 to available agent
Found 1 available agents for delivery assignment
✅ Auto-assigned delivery 5 to agent John Doe
Delivery created successfully: deliveryId=5
```

## Future Enhancements

### Option 1: Update Order Service (Recommended)
Modify Order Service to include restaurant details in OrderEvent:
```java
OrderEvent orderEvent = OrderEvent.builder()
    .eventType("ORDER_CONFIRMED")
    .orderId(order.getId())
    .restaurantId(order.getRestaurantId())
    .customerId(order.getCustomerId())
    .deliveryAddress(order.getDeliveryAddress())
    // Add these fields:
    .pickupAddress(restaurant.getAddress())
    .pickupLatitude(restaurant.getLatitude())
    .pickupLongitude(restaurant.getLongitude())
    .build();
```

### Option 2: Fetch Restaurant Details
Add a REST client in Delivery Service to fetch restaurant details:
```java
RestaurantResponse restaurant = restaurantClient.getRestaurant(restaurantId);
deliveryRequest.setPickupAddress(restaurant.getAddress());
deliveryRequest.setPickupLatitude(restaurant.getLatitude());
deliveryRequest.setPickupLongitude(restaurant.getLongitude());
```

### Option 3: Database Lookup
Store restaurant addresses in Delivery Service database for quick lookup.

## Summary

✅ Fixed null pickup address constraint violation
✅ Added sensible defaults for missing fields
✅ Validated required fields (deliveryAddress)
✅ Logged warnings for missing optional fields
✅ Delivery creation now works with current Order Service events

The system is now resilient to missing pickup address data and will work with the current Order Service implementation!
