@echo off
echo ========================================
echo QUICK FIX - DELIVERY SERVICE
echo ========================================
echo.
echo This will rebuild the Delivery Service with the null-safe fixes.
echo.
echo IMPORTANT: 
echo 1. Stop the running Delivery Service first (Ctrl+C in its window)
echo 2. Then run this script
echo 3. Then restart: java -jar delivery-service\target\delivery-service-0.0.1-SNAPSHOT.jar
echo.
pause
echo.

cd delivery-service
echo Cleaning and building...
call mvn clean package -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo ✅ BUILD SUCCESSFUL!
    echo ========================================
    echo.
    echo Now run this command to start the service:
    echo java -jar delivery-service\target\delivery-service-0.0.1-SNAPSHOT.jar
    echo.
    echo Then place a new order to test.
    echo.
) else (
    echo.
    echo ========================================
    echo ❌ BUILD FAILED!
    echo ========================================
    echo Check the errors above.
)

cd ..
pause
