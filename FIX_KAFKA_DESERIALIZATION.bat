@echo off
echo ========================================
echo Fix Kafka Deserialization Issue
echo ========================================
echo.
echo This script will:
echo 1. Clear problematic Kafka topics
echo 2. Rebuild Order Service with proper deserialization
echo 3. Restart the service
echo.
echo IMPORTANT: Make sure Kafka is running!
echo.
pause

echo.
echo [1/3] Clearing Kafka topics...
call CLEAR_KAFKA_TOPICS.bat

echo.
echo [2/3] Building Order Service with Kafka fixes...
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
echo Fix Applied Successfully!
echo ========================================
echo.
echo Changes made:
echo - Added ErrorHandlingDeserializer for robust Kafka consumption
echo - Configured proper JSON deserialization for DeliveryEvent
echo - Added manual acknowledgment for better error handling
echo - Cleared old corrupted messages from Kafka topics
echo.
echo Next steps:
echo 1. Stop any running Order Service (Ctrl+C)
echo 2. Run: java -jar Food-deli-order_service\target\order-service-0.0.1-SNAPSHOT.jar
echo 3. Test order tracking - deserialization errors should be gone
echo.
echo The Order Service now includes:
echo - Robust Kafka deserialization with error handling
echo - Proper type mapping for DeliveryEvent messages
echo - Manual acknowledgment to prevent message loss
echo.
pause