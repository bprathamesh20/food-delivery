# Order Management Service

## Overview
The Order Management Service is a core microservice responsible for handling the complete order lifecycle in the Food Delivery System. It manages order creation, tracking, status updates, and coordinates with other services through REST APIs and Kafka events.

## Features
- ✅ Complete REST API with CRUD operations
- ✅ Order lifecycle management (PENDING → CONFIRMED → PREPARING → READY → PICKED_UP → DELIVERED)
- ✅ Kafka integration for event-driven communication
- ✅ Payment event processing
- ✅ Order status tracking and validation
- ✅ Comprehensive test suite
- ✅ Docker support

## Quick Start

### Using Docker Compose
```bash
# From project root
docker-compose up --build
```

Access:
- Order Service: http://localhost:8085
- API Gateway: http://localhost:8080
- Eureka Server: http://localhost:8761

### Local Development
```bash
cd order-service
mvn spring-boot:run
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders` | Create new order |
| GET | `/api/orders/{id}` | Get order by ID |
| GET | `/api/orders/customer/{customerId}` | Get customer's orders |
| GET | `/api/orders/restaurant/{restaurantId}` | Get restaurant's orders |
| GET | `/api/orders` | Get all orders (admin) |
| PUT | `/api/orders/{id}/status` | Update order status |
| DELETE | `/api/orders/{id}` | Cancel order |

## API Examples

### Create Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "restaurantId": 1,
    "deliveryAddress": "123 Main St, City",
    "items": [
      {"menuItemId": 101, "quantity": 2},
      {"menuItemId": 102, "quantity": 1}
    ]
  }'
```

### Get Order by ID
```bash
curl http://localhost:8080/api/orders/1
```

### Update Order Status
```bash
curl -X PUT http://localhost:8080/api/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "CONFIRMED"}'
```

### Cancel Order
```bash
curl -X DELETE http://localhost:8080/api/orders/1
```

## Order Status Flow

```
PENDING → CONFIRMED → PREPARING → READY → PICKED_UP → DELIVERED
   ↓          ↓           ↓
CANCELLED  CANCELLED  CANCELLED
```

**Status Descriptions:**
- **PENDING**: Order created, awaiting payment
- **CONFIRMED**: Payment successful, order confirmed
- **PREPARING**: Restaurant is preparing the order
- **READY**: Order ready for pickup
- **PICKED_UP**: Delivery agent picked up the order
- **DELIVERED**: Order delivered to customer
- **CANCELLED**: Order cancelled

## Database Schema

```sql
CREATE TABLE orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  customer_id BIGINT NOT NULL,
  restaurant_id BIGINT NOT NULL,
  order_status VARCHAR(50) NOT NULL,
  payment_status VARCHAR(50) NOT NULL,
  total_amount DECIMAL(10,2) NOT NULL,
  delivery_address VARCHAR(500) NOT NULL,
  special_instructions TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE order_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  menu_item_id BIGINT NOT NULL,
  menu_item_name VARCHAR(255) NOT NULL,
  quantity INT NOT NULL,
  price_per_unit DECIMAL(10,2) NOT NULL,
  subtotal DECIMAL(10,2) NOT NULL,
  FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);
```

## Kafka Integration

### Consumer Topics
- **payment-events**: Listens for payment success/failure events
  - On success: Updates order to CONFIRMED, publishes ORDER_CONFIRMED event
  - On failure: Updates payment status to FAILED

### Producer Topics
- **order-events**: Publishes order lifecycle events
  - ORDER_CREATED
  - ORDER_CONFIRMED (triggers delivery assignment)
  - STATUS_CHANGED
  - ORDER_CANCELLED

## Testing

### Run All Tests
```bash
cd order-service
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=OrderServiceTest
mvn test -Dtest=OrderControllerTest
```

## Configuration

**application.yml**:
- **Server**: Port 8085
- **Database**: MySQL on port 3307, database `order_db`
- **Eureka**: Registers as `ORDER-SERVICE`
- **Kafka**: Consumer group `order-group`

## Tech Stack

- Java 17
- Spring Boot 3.2.3
- Spring Data JPA
- Spring Kafka
- Spring Cloud Eureka Client
- MySQL 8.0
- Lombok
- JUnit 5 & Mockito

## Project Structure

```
order-service/
├── src/main/java/com/fooddel/order/
│   ├── config/          # Kafka configuration
│   ├── controller/      # REST API controllers
│   ├── dto/             # Data Transfer Objects
│   ├── entity/          # JPA entities
│   ├── enums/           # Order and payment status enums
│   ├── exception/       # Custom exceptions and handlers
│   ├── kafka/           # Kafka consumers and producers
│   ├── repository/      # JPA repositories
│   └── service/         # Business logic
└── src/test/java/       # Test suite
```

## Integration with Other Services

- **User Service**: Validates customer IDs
- **Restaurant Service**: Validates restaurant and menu items
- **Payment Service**: Receives payment events via Kafka
- **Delivery Service**: Receives order confirmed events via Kafka

## Error Handling

The service provides consistent error responses:

```json
{
  "timestamp": "2026-02-10T15:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Order not found with id: 123",
  "path": "/api/orders/123"
}
```

## Business Rules

1. Orders can only be cancelled if status is PENDING, CONFIRMED, or PREPARING
2. Status transitions must follow the defined flow
3. Payment must be completed before order moves to CONFIRMED
4. Total amount is automatically calculated from order items

## License

MIT License
