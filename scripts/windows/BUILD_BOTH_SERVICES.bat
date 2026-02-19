@echo off
echo ========================================
echo Building Delivery and Notification Services
echo ========================================

echo.
echo [1/2] Building Delivery Service...
cd delivery-service
call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Delivery Service build failed!
    cd ..
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
    cd ..\..
    pause
    exit /b 1
)
cd ..\..

echo.
echo ========================================
echo Build Complete!
echo ========================================
echo.
echo JAR files created:
echo - delivery-service\target\delivery-service-0.0.1-SNAPSHOT.jar
echo - notification-service\notification\target\notification-0.0.1-SNAPSHOT.jar
echo.
echo Next steps:
echo 1. Stop any running Delivery Service (Ctrl+C)
echo 2. Stop any running Notification Service (Ctrl+C)
echo 3. Run: java -jar delivery-service\target\delivery-service-0.0.1-SNAPSHOT.jar
echo 4. Run: java -jar notification-service\notification\target\notification-0.0.1-SNAPSHOT.jar
echo 5. Refresh frontend browser
echo.
pause
