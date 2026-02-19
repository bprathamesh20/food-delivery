@echo off
echo ========================================
echo REBUILDING DELIVERY SERVICE
echo ========================================
echo.
echo FIXES APPLIED:
echo - Kafka package mismatch fixed
echo - ORDER_CONFIRMED event type now handled
echo - Pickup address null handling with defaults
echo.

cd delivery-service
echo Building Delivery Service...
call mvn clean package -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo BUILD SUCCESSFUL!
    echo ========================================
    echo.
    echo Now restart the Delivery Service:
    echo 1. Stop the current Delivery Service process (Ctrl+C)
    echo 2. Run: java -jar delivery-service\target\delivery-service-0.0.1-SNAPSHOT.jar
    echo.
    echo After restart, place a new order and check:
    echo - Delivery Service logs should show "Delivery created successfully"
    echo - Agent dashboard should show the new delivery with auto-assigned agent
    echo.
) else (
    echo.
    echo ========================================
    echo BUILD FAILED!
    echo ========================================
    echo Please check the error messages above.
)

cd ..
pause
