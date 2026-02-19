@echo off
echo ========================================
echo Rebuilding Delivery and Notification Services
echo ========================================

echo.
echo [1/2] Building Delivery Service...
cd delivery-service
call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Delivery Service build failed!
    pause
    exit /b 1
)
cd ..

echo.
echo [2/2] Building Notification Service...
cd notification-service\notification
call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Notification Service build failed!
    pause
    exit /b 1
)
cd ..\..

echo.
echo ========================================
echo Build Complete!
echo ========================================
echo.
echo Next steps:
echo 1. Stop the running Delivery Service (Ctrl+C in its terminal)
echo 2. Stop the running Notification Service (Ctrl+C in its terminal)
echo 3. Run: java -jar delivery-service\target\delivery-service-0.0.1-SNAPSHOT.jar
echo 4. Run: java -jar notification-service\notification\target\notification-0.0.1-SNAPSHOT.jar
echo.
pause
