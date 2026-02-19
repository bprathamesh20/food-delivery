@echo off
REM Food Delivery System - API Testing Script
REM This script tests all microservices endpoints

echo ========================================
echo Food Delivery System - API Testing
echo ========================================
echo.

REM Check if curl is available
where curl >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: curl is not installed or not in PATH
    echo Please install curl or use Postman for testing
    pause
    exit /b 1
)

echo [1/10] Testing Eureka Server...
curl -s http://localhost:8761/actuator/health
echo.
echo.

echo [2/10] Testing API Gateway...
curl -s http://localhost:9090/actuator/health
echo.
echo.

echo [3/10] Testing User Service - Health Check...
curl -s http://localhost:8000/actuator/health
echo.
echo.

echo [4/10] Testing Restaurant Service - Get All Restaurants...
curl -s http://localhost:9090/api/restaurants
echo.
echo.

echo [5/10] Testing Order Service - Health Check...
curl -s http://localhost:8084/actuator/health
echo.
echo.

echo [6/10] Testing Payment Service - Health Check...
curl -s http://localhost:8085/actuator/health
echo.
echo.

echo [7/10] Testing Delivery Service - Health Check...
curl -s http://localhost:8083/delivery-service/actuator/health
echo.
echo.

echo [8/10] Testing Notification Service - Health Check...
curl -s http://localhost:8086/actuator/health
echo.
echo.

echo [9/10] Checking Eureka Registered Services...
curl -s -H "Accept: application/json" http://localhost:8761/eureka/apps | findstr "USER-SERVICE RESTAURANT-SERVICE ORDER-SERVICE PAYMENT-SERVICE DELIVERY-SERVICE NOTIFICATION-SERVICE"
echo.
echo.

echo [10/10] Testing Gateway Routes...
curl -s http://localhost:9090/actuator/gateway/routes
echo.
echo.

echo ========================================
echo Testing Complete!
echo ========================================
echo.
echo Next Steps:
echo 1. Check if all health checks returned "UP" status
echo 2. Verify all 6 services are registered in Eureka
echo 3. Run INTEGRATION_TEST.bat for full flow testing
echo.
pause
