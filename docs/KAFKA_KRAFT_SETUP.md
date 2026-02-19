# 🚀 Kafka KRaft Mode Setup Guide

## What is KRaft?

KRaft (Kafka Raft) is Kafka's new consensus protocol that removes the dependency on Apache Zookeeper. It's the future of Kafka and provides:
- ✅ Simpler architecture
- ✅ Faster startup and recovery
- ✅ Better scalability
- ✅ Reduced operational complexity

---

## 🔧 Running Kafka in KRaft Mode

### Option 1: Using Docker (Recommended)

The provided `docker-compose.yml` already configures Kafka in KRaft mode:

```bash
# Start Kafka with KRaft
docker-compose up -d kafka

# Verify Kafka is running
docker-compose logs kafka

# Check Kafka health
docker-compose ps kafka
```

**Key Configuration in docker-compose.yml:**
```yaml
kafka:
  environment:
    KAFKA_PROCESS_ROLES: broker,controller  # Combined mode
    KAFKA_CONTROLLER_QUORUM_VOTERS: 1@kafka:9093
    KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
    # No KAFKA_ZOOKEEPER_CONNECT - that's the key!
```

---

### Option 2: Manual Kafka Setup (Local Development)

If you're running Kafka manually on your system:

#### Step 1: Generate Cluster ID
```bash
# Generate a unique cluster ID
kafka-storage.sh random-uuid
# Output: MkU3OEVBNTcwNTJENDM2Qk (example)
```

#### Step 2: Format Storage
```bash
# Format the storage directory with the cluster ID
kafka-storage.sh format -t MkU3OEVBNTcwNTJENDM2Qk -c config/kraft/server.properties
```

#### Step 3: Start Kafka
```bash
# Start Kafka in KRaft mode
kafka-server-start.sh config/kraft/server.properties
```

#### Step 4: Verify Kafka is Running
```bash
# List topics (should work without Zookeeper)
kafka-topics.sh --list --bootstrap-server localhost:9098
```

---

## 📋 Required Kafka Topics

Create these topics for the microservices:

```bash
# Create all required topics
kafka-topics.sh --create --topic user-events --bootstrap-server localhost:9098 --partitions 3 --replication-factor 1

kafka-topics.sh --create --topic restaurant-events --bootstrap-server localhost:9098 --partitions 3 --replication-factor 1

kafka-topics.sh --create --topic order-events --bootstrap-server localhost:9098 --partitions 3 --replication-factor 1

kafka-topics.sh --create --topic payment-events --bootstrap-server localhost:9098 --partitions 3 --replication-factor 1

kafka-topics.sh --create --topic delivery-events --bootstrap-server localhost:9098 --partitions 3 --replication-factor 1

kafka-topics.sh --create --topic notification-events --bootstrap-server localhost:9098 --partitions 3 --replication-factor 1
```

**Or create all at once:**
```bash
for topic in user-events restaurant-events order-events payment-events delivery-events notification-events; do
  kafka-topics.sh --create --topic $topic --bootstrap-server localhost:9098 --partitions 3 --replication-factor 1
done
```

---

## ✅ Verify Kafka Setup

### 1. List All Topics
```bash
kafka-topics.sh --list --bootstrap-server localhost:9098
```

**Expected Output:**
```
delivery-events
notification-events
order-events
payment-events
restaurant-events
user-events
```

### 2. Describe a Topic
```bash
kafka-topics.sh --describe --topic order-events --bootstrap-server localhost:9098
```

### 3. Test Producer
```bash
# Send a test message
echo "test message" | kafka-console-producer.sh --topic order-events --bootstrap-server localhost:9098
```

### 4. Test Consumer
```bash
# Consume messages from beginning
kafka-console-consumer.sh --topic order-events --from-beginning --bootstrap-server localhost:9098
```

---

## 🔍 Monitoring Kafka

### Check Broker Status
```bash
kafka-broker-api-versions.sh --bootstrap-server localhost:9098
```

### View Consumer Groups
```bash
kafka-consumer-groups.sh --list --bootstrap-server localhost:9098
```

**Expected Groups:**
- order-group
- restaurant-group
- delivery-service-group
- notification-group

### Check Consumer Lag
```bash
kafka-consumer-groups.sh --describe --group order-group --bootstrap-server localhost:9098
```

---

## 🐛 Troubleshooting

### Issue 1: Kafka Won't Start
**Problem:** Kafka fails to start in KRaft mode

**Solution:**
```bash
# 1. Check if port 9098 is available
netstat -ano | findstr :9098

# 2. Ensure storage is formatted
kafka-storage.sh format -t YOUR_CLUSTER_ID -c config/kraft/server.properties

# 3. Check logs
tail -f logs/server.log
```

### Issue 2: Topics Not Auto-Created
**Problem:** Services can't publish to topics

**Solution:**
```bash
# Enable auto-creation in server.properties
auto.create.topics.enable=true

# Or create topics manually (see above)
```

### Issue 3: Connection Refused
**Problem:** Services can't connect to Kafka

**Solution:**
```bash
# 1. Verify Kafka is listening on 9098
netstat -ano | findstr :9098

# 2. Check firewall settings

# 3. Verify bootstrap-servers in application configs
# Should be: localhost:9098 (or kafka:29092 in Docker)
```

---

## 📊 Kafka Configuration in Microservices

All services are already configured to use Kafka on port 9098:

### User Service
```properties
spring.kafka.bootstrap-servers=localhost:9098
```

### Restaurant Service
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9098
```

### Order Service
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9098
```

### Payment Service
```yaml
spring:
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9098}
```

### Delivery Service
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9098
```

### Notification Service
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9098
```

---

## 🎯 Key Differences: KRaft vs Zookeeper

| Feature | Zookeeper Mode | KRaft Mode |
|---------|---------------|------------|
| Dependencies | Requires Zookeeper | Standalone |
| Startup Time | Slower | Faster |
| Scalability | Limited by Zookeeper | Better |
| Complexity | Higher | Lower |
| Metadata Storage | Zookeeper | Kafka itself |
| Production Ready | Yes | Yes (Kafka 3.3+) |

---

## 🚀 Quick Start Commands

```bash
# Start Kafka (if running manually)
kafka-server-start.sh config/kraft/server.properties

# Create topics
kafka-topics.sh --create --topic order-events --bootstrap-server localhost:9098 --partitions 3 --replication-factor 1

# Start microservices
docker-compose up -d

# Monitor Kafka logs
docker-compose logs -f kafka

# Test event flow
# 1. Create an order
curl -X POST http://localhost:9090/api/orders -H "Content-Type: application/json" -d '{"customerId":1,"restaurantId":1,"deliveryAddress":"123 Main St","items":[{"menuItemId":101,"quantity":2}]}'

# 2. Check if event was consumed
docker-compose logs notification-service | grep "Received order event"
```

---

## ✅ Verification Checklist

- [ ] Kafka is running on port 9098
- [ ] No Zookeeper process running
- [ ] All 6 topics created
- [ ] All microservices can connect to Kafka
- [ ] Events are being published and consumed
- [ ] Consumer groups are active

---

## 📚 Additional Resources

- [Kafka KRaft Documentation](https://kafka.apache.org/documentation/#kraft)
- [KRaft Migration Guide](https://kafka.apache.org/documentation/#kraft_zk_migration)
- [Kafka Commands Cheat Sheet](https://kafka.apache.org/quickstart)

---

**Status:** ✅ KRaft Mode Configured  
**Zookeeper Required:** ❌ No  
**Port:** 9098  
**Mode:** Combined (broker + controller)
