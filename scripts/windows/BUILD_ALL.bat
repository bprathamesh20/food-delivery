@echo off
echo ========================================
echo Building All Microservices
echo ========================================
echo.

echo [1/8] Building User Service...
cd user-service-backend
call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: User Service build failed!
    pause
    exit /b 1
)
cd ..

echo [2/8] Building Restaurant Service...
cd restaurant-service
call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Restaurant Service build failed!
    pause
    exit /b 1
)
cd ..

echo [3/8] Building Order Service...
cd Food-deli-order_service
call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Order Service build failed!
    pause
    exit /b 1
)
cd ..

echo [4/8] Building Payment Service...
cd payments-service\payment\demo
call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Payment Service build failed!
    pause
    exit /b 1
)
cd ..\..\..

echo [5/8] Building Delivery Service...
cd delivery-service
call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Delivery Service build failed!
    pause
    exit /b 1
)
cd ..

echo [6/8] Building Notification Service...
cd notification-service\notification
call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Notification Service build failed!
    pause
    exit /b 1
)
cd ..\..

echo [7/8] Building Eureka Server...
cd payments-service\eureka
call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Eureka Server build failed!
    pause
    exit /b 1
)
cd ..\..

echo [8/8] Building API Gateway...
cd payments-service\gateway
call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: API Gateway build failed!
    pause
    exit /b 1
)
cd ..\..

echo.
echo ========================================
echo ✅ All services built successfully!
echo ========================================
echo.
echo You can now:
echo 1. Run locally: RUN_LOCALLY.bat
echo 2. Run with Docker: docker-compose -f docker-compose-local.yml up -d
echo.
pause
