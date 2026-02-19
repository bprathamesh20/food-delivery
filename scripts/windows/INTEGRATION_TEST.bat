@echo off
REM Complete Integration Test - Full User Flow
REM Tests: User Registration → Restaurant → Order → Payment → Delivery

setlocal enabledelayedexpansion

echo ========================================
echo INTEGRATION TEST - Full User Flow
echo ========================================
echo.

REM Check if curl is available
where curl >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: curl is not installed
    pause
    exit /b 1
)

echo Step 1: Register a New User
echo ----------------------------
curl -X POST http://localhost:9090/api/auth/signup ^
  -H "Content-Type: application/json" ^
  -d "{\"fullName\":\"Test User\",\"email\":\"test@example.com\",\"phone\":\"1234567890\",\"password\":\"password123\",\"confirmPassword\":\"password123\"}" ^
  -o user_response.json
echo.
echo Response saved to user_response.json
type user_response.json
echo.
echo.

echo Step 2: Login User
echo -------------------
curl -X POST http://localhost:9090/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"emailOrPhone\":\"test@example.com\",\"password\":\"password123\"}" ^
  -o login_response.json
echo.
echo Response saved to login_response.json
type login_response.json
echo.
echo.

echo Step 3: Register a Restaurant
echo ------------------------------
curl -X POST http://localhost:9090/api/restaurants/register ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Test Restaurant\",\"email\":\"restaurant@test.com\",\"password\":\"password123\",\"address\":\"123 Main St\",\"phone\":\"9876543210\"}" ^
  -o restaurant_response.json
echo.
echo Response saved to restaurant_response.json
type restaurant_response.json
echo.
echo.

echo Step 4: Get All Restaurants
echo ---------------------------
curl http://localhost:9090/api/restaurants -o restaurants_list.json
echo.
echo Response saved to restaurants_list.json
type restaurants_list.json
echo.
echo.

echo Step 5: Add Menu Item to Restaurant
echo ------------------------------------
echo NOTE: Replace {restaurantId} with actual ID from restaurant_response.json
echo Example command:
echo curl -X POST http://localhost:9090/api/menu/1 ^
echo   -H "Content-Type: application/json" ^
echo   -d "{\"name\":\"Pizza\",\"description\":\"Cheese Pizza\",\"price\":12.99,\"category\":\"Main\",\"available\":true}"
echo.
echo.

echo Step 6: Create Order
echo --------------------
echo NOTE: Replace customerId, restaurantId, and menuItemId with actual values
echo Example command:
echo curl -X POST http://localhost:9090/api/orders ^
echo   -H "Content-Type: application/json" ^
echo   -d "{\"customerId\":1,\"restaurantId\":1,\"deliveryAddress\":\"456 Oak Ave\",\"items\":[{\"menuItemId\":1,\"quantity\":2}]}"
echo.
echo.

echo Step 7: Create Payment Order
echo ----------------------------
echo NOTE: Replace orderId with actual order ID
echo Example command:
echo curl -X POST http://localhost:9090/api/payments/razorpay/order ^
echo   -H "Content-Type: application/json" ^
echo   -d "{\"orderId\":\"1\",\"amount\":25.98,\"paymentMethod\":\"RAZORPAY\"}"
echo.
echo.

echo Step 8: Register Delivery Agent
echo --------------------------------
curl -X POST http://localhost:9090/delivery-service/api/agents/register ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Test Driver\",\"email\":\"driver@test.com\",\"phone\":\"5551234567\",\"password\":\"password123\",\"vehicleType\":\"BIKE\",\"vehicleNumber\":\"ABC-1234\",\"licenseNumber\":\"DL123456\"}" ^
  -o agent_response.json
echo.
echo Response saved to agent_response.json
type agent_response.json
echo.
echo.

echo Step 9: Check Delivery Status
echo ------------------------------
echo NOTE: Replace orderId with actual order ID
echo Example command:
echo curl http://localhost:9090/delivery-service/api/deliveries/order/1
echo.
echo.

echo ========================================
echo INTEGRATION TEST COMPLETE
echo ========================================
echo.
echo Review the response files:
echo - user_response.json
echo - login_response.json
echo - restaurant_response.json
echo - restaurants_list.json
echo - agent_response.json
echo.
echo Use the IDs from these responses for subsequent API calls
echo.
pause
