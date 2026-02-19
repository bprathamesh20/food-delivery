@echo off
echo ========================================
echo Food Delivery System - Local Startup
echo ========================================
echo.
echo This will start all 8 services locally
echo Make sure MySQL (3306) and Kafka (9098) are running!
echo.
pause

echo Starting Eureka Server...
start "Eureka Server" cmd /k "cd payments-service\eureka && mvn spring-boot:run"
timeout /t 30

echo Starting User Service...
start "User Service" cmd /k "cd user-service-backend && mvn spring-boot:run"
timeout /t 10

echo Starting Restaurant Service...
start "Restaurant Service" cmd /k "cd restaurant-service && mvn spring-boot:run"
timeout /t 10

echo Starting Order Service...
start "Order Service" cmd /k "cd Food-deli-order_service && mvn spring-boot:run"
timeout /t 10

echo Starting Payment Service...
start "Payment Service" cmd /k "cd payments-service\payment\demo && mvn spring-boot:run"
timeout /t 10

echo Starting Delivery Service...
start "Delivery Service" cmd /k "cd delivery-service && mvn spring-boot:run"
timeout /t 10

echo Starting Notification Service...
start "Notification Service" cmd /k "cd notification-service\notification && mvn spring-boot:run"
timeout /t 10

echo Starting API Gateway...
start "API Gateway" cmd /k "cd payments-service\gateway && mvn spring-boot:run"

echo.
echo ========================================
echo All services are starting!
echo Check Eureka: http://localhost:8761
echo API Gateway: http://localhost:9090
echo ========================================
