# Kafka Port Configuration Fix

## Issue
User Service was trying to connect to Kafka on port 9092 instead of 9098, causing connection failures.

## Root Cause
The `KafkaProducerConfig.java` in User Service had a hardcoded bootstrap server:
```java
props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
```

## Fix Applied
Updated `user-service-backend/src/main/java/com/fooddelivery/auth/config/KafkaProducerConfig.java`:

**Before:**
```java
@Configuration
public class KafkaProducerConfig {
    @Bean
    public Map<String, Object> producerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // ...
    }
}
```

**After:**
```java
@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    @Bean
    public Map<String, Object> producerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // ...
    }
}
```

## Verification
All other services already use `@Value("${spring.kafka.bootstrap-servers}")` and read from configuration files:

✅ **User Service** - Fixed (was hardcoded, now reads from application.properties)
✅ **Restaurant Service** - Already correct (reads from application.yaml)
✅ **Order Service** - Already correct (reads from application.yml)
✅ **Payment Service** - Already correct (reads from application.yaml)
✅ **Delivery Service** - Already correct (reads from application.yaml)
✅ **Notification Service** - Already correct (reads from application.yaml)

## Configuration Files Status
All services have correct Kafka port in their configuration:

| Service | Config File | Bootstrap Server |
|---------|------------|------------------|
| User Service | application.properties | localhost:9098 ✅ |
| Restaurant Service | application.yaml | localhost:9098 ✅ |
| Order Service | application.yml | localhost:9098 ✅ |
| Payment Service | application.yaml | localhost:9098 ✅ |
| Delivery Service | application.yaml | localhost:9098 ✅ |
| Notification Service | application.yaml | localhost:9098 ✅ |

## Action Required
Restart the User Service to apply the fix:
```bash
# Stop the current User Service process (Ctrl+C)
# Then restart it
cd user-service-backend
mvn spring-boot:run
```

## Expected Result
After restart, User Service logs should show:
```
✅ Connected to Kafka at localhost:9098
✅ No more "Connection to node -1 (localhost/127.0.0.1:9092) could not be established" errors
```

---
**Fixed:** February 12, 2026
