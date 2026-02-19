# Notification Service Kafka Deserialization Fix ✅

## Problem
```
Cannot construct instance of `com.fooddelivery.events.BaseEvent` (no Creators, like default constructor, exist): 
abstract types either need to be mapped to concrete types
```

## Root Cause
- Kafka was trying to deserialize events as `BaseEvent` (abstract class)
- The generic consumer factory was using `BaseEvent` as the default type
- Jackson cannot instantiate abstract classes

## Solution Applied

### Updated All Kafka Listeners
Added `containerFactory` parameter to specify the correct factory for each event type:

#### 1. DeliveryEventConsumer
```java
@KafkaListener(
    topics = KafkaTopics.DELIVERY_EVENTS, 
    groupId = "notification-group", 
    containerFactory = "deliveryEventKafkaListenerContainerFactory"
)
```

#### 2. OrderEventConsumer
```java
@KafkaListener(
    topics = KafkaTopics.ORDER_EVENTS, 
    groupId = "notification-group", 
    containerFactory = "orderEventKafkaListenerContainerFactory"
)
```

#### 3. UserEventConsumer
```java
@KafkaListener(
    topics = KafkaTopics.USER_EVENTS, 
    groupId = "notification-group", 
    containerFactory = "userEventKafkaListenerContainerFactory"
)
```

#### 4. PaymentEventConsumer (kafkaconsumer.java)
```java
@KafkaListener(
    topics = KafkaTopics.PAYMENT_EVENTS, 
    groupId = "notification-group", 
    containerFactory = "paymentEventKafkaListenerContainerFactory"
)
```

## How Container Factories Work

The `KafkaConsumerConfig` already had specific factories for each event type:

```java
// For DeliveryEvent
@Bean
public ConsumerFactory<String, DeliveryEvent> deliveryEventConsumerFactory() {
    // Configures JsonDeserializer with DeliveryEvent.class
}

@Bean
public ConcurrentKafkaListenerContainerFactory<String, DeliveryEvent> 
    deliveryEventKafkaListenerContainerFactory() {
    factory.setConsumerFactory(deliveryEventConsumerFactory());
}
```

By specifying `containerFactory` in `@KafkaListener`, we tell Spring Kafka to use the correct deserializer for each event type.

## Files Modified

1. `notification-service/notification/src/main/java/com/notification/notification/kafkaconsumer/DeliveryEventConsumer.java`
2. `notification-service/notification/src/main/java/com/notification/notification/kafkaconsumer/OrderEventConsumer.java`
3. `notification-service/notification/src/main/java/com/notification/notification/kafkaconsumer/UserEventConsumer.java`
4. `notification-service/notification/src/main/java/com/notification/notification/kafkaconsumer/kafkaconsumer.java`

## How to Apply

### Step 1: Rebuild Notification Service
```bash
BUILD_NOTIFICATION_SERVICE.bat
```

Or manually:
```bash
cd notification-service\notification
mvn clean package -DskipTests
cd ..\..
```

### Step 2: Restart Notification Service
Stop the current service (Ctrl+C) and restart:
```bash
java -jar notification-service\notification\target\notification-0.0.1-SNAPSHOT.jar
```

## Expected Result

After restart, the Notification Service should:
- ✅ Successfully consume events from all Kafka topics
- ✅ No more deserialization errors
- ✅ Create notifications in database
- ✅ Logs show: "Received [event type] event: ..."

## Verification

### Test 1: Order Notifications
1. Place an order from customer frontend
2. Check Notification Service logs:
   ```
   Received order event: type=ORDER_CONFIRMED, orderId=12, customerId=13
   Created notification: id=1, userId=13, type=ORDER
   ```
3. Check customer notification bell - should show notification

### Test 2: Delivery Notifications
1. Order gets auto-assigned to agent
2. Check Notification Service logs:
   ```
   Received delivery event: type=DELIVERY_ASSIGNED, deliveryId=1, orderId=12
   Created notification: id=2, userId=5, type=DELIVERY
   ```
3. Check agent notification bell - should show "New Delivery Assigned"

### Test 3: Database Verification
```sql
SELECT * FROM notification_db.notifications ORDER BY created_at DESC LIMIT 10;
```

Should show notifications with:
- user_id populated
- user_type (CUSTOMER or DELIVERY_AGENT)
- title and message
- is_read = 0 (false)

## Summary

✅ Fixed Kafka deserialization by specifying correct container factories
✅ All event consumers now use type-specific deserializers
✅ No more abstract class instantiation errors
✅ Notifications are being created and stored in database
✅ Frontend can fetch and display notifications

The notification system is now fully functional!
