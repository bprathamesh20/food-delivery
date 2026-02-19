@echo off
echo ========================================
echo Building Order Service with Delivery Event Consumer
echo ========================================

echo.
echo [1/1] Building Order Service...
cd Food-deli-order_service
call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Order Service build failed!
    cd ..
    pause
    exit /b 1
)
cd ..

echo.
echo ========================================
echo Build Complete!
echo ========================================
echo.
echo JAR file created:
echo - Food-deli-order_service\target\order-service-0.0.1-SNAPSHOT.jar
echo.
echo Next steps:
echo 1. Stop any running Order Service (Ctrl+C)
echo 2. Run: java -jar Food-deli-order_service\target\order-service-0.0.1-SNAPSHOT.jar
echo 3. Test order tracking functionality
echo.
echo The Order Service now includes:
echo - DeliveryEventConsumer to listen for delivery status updates
echo - Automatic order status synchronization with delivery status
echo - Enhanced event publishing for status updates
echo.
pause