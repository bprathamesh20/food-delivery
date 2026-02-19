@echo off
echo ========================================
echo Testing Delivery Service
echo ========================================
echo.

echo Step 1: Register Delivery Agent
echo ----------------------------------
curl -X POST http://localhost:9090/delivery-service/api/v1/auth/agent/register -H "Content-Type: application/json" -d "{\"name\":\"John Delivery\",\"email\":\"john@delivery.com\",\"password\":\"pass123\",\"phoneNumber\":\"9876543210\",\"vehicleType\":\"BIKE\",\"vehicleNumber\":\"MH12AB1234\",\"address\":\"123 Agent St\",\"city\":\"Mumbai\",\"state\":\"Maharashtra\",\"licenseNumber\":\"DL123456\"}"
echo.
echo.
echo IMPORTANT: Copy the token from the response above!
echo.
pause

echo.
echo Step 2: Login as Delivery Agent
echo ----------------------------------
curl -X POST http://localhost:9090/delivery-service/api/v1/auth/agent/login -H "Content-Type: application/json" -d "{\"email\":\"john@delivery.com\",\"password\":\"pass123\"}"
echo.
echo.
echo IMPORTANT: Copy the token from the response above!
echo Set it as: set AGENT_TOKEN=your_token_here
echo.
pause

echo.
echo Step 3: Get Agent Profile
echo ----------------------------------
echo Replace YOUR_AGENT_TOKEN with actual token:
echo curl -X GET http://localhost:9090/delivery-service/api/v1/agents/me -H "Authorization: Bearer YOUR_AGENT_TOKEN"
echo.
pause

echo.
echo Step 4: Set Agent Status to AVAILABLE
echo ----------------------------------
echo Replace YOUR_AGENT_TOKEN with actual token:
echo curl -X PUT http://localhost:9090/delivery-service/api/v1/agents/me/status -H "Content-Type: application/json" -H "Authorization: Bearer YOUR_AGENT_TOKEN" -d "{\"status\":\"AVAILABLE\"}"
echo.
pause

echo.
echo Step 5: Create Delivery for Order
echo ----------------------------------
echo Replace orderId, restaurantId, customerId with actual values:
echo curl -X POST http://localhost:9090/delivery-service/api/v1/deliveries -H "Content-Type: application/json" -d "{\"orderId\":1,\"restaurantId\":1,\"customerId\":1,\"pickupAddress\":\"Restaurant Address\",\"deliveryAddress\":\"Customer Address\",\"deliveryFee\":40.0}"
echo.
pause

echo.
echo Step 6: Assign Delivery to Agent
echo ----------------------------------
echo Replace deliveryId and agentId with actual values:
echo curl -X POST http://localhost:9090/delivery-service/api/v1/deliveries/1/assign -H "Content-Type: application/json" -d "{\"agentId\":1}"
echo.
pause

echo.
echo Step 7: Update Delivery Status
echo ----------------------------------
echo Replace YOUR_AGENT_TOKEN and deliveryId with actual values:
echo.
echo Mark as PICKED_UP:
echo curl -X PUT http://localhost:9090/delivery-service/api/v1/deliveries/1/status -H "Content-Type: application/json" -H "Authorization: Bearer YOUR_AGENT_TOKEN" -d "{\"status\":\"PICKED_UP\"}"
echo.
echo Mark as IN_TRANSIT:
echo curl -X PUT http://localhost:9090/delivery-service/api/v1/deliveries/1/status -H "Content-Type: application/json" -H "Authorization: Bearer YOUR_AGENT_TOKEN" -d "{\"status\":\"IN_TRANSIT\"}"
echo.
echo Mark as DELIVERED:
echo curl -X PUT http://localhost:9090/delivery-service/api/v1/deliveries/1/status -H "Content-Type: application/json" -H "Authorization: Bearer YOUR_AGENT_TOKEN" -d "{\"status\":\"DELIVERED\"}"
echo.
pause

echo.
echo Step 8: Track Delivery
echo ----------------------------------
echo Replace orderId with actual value:
echo curl -X GET http://localhost:9090/delivery-service/api/v1/deliveries/order/1
echo.
pause

echo.
echo ========================================
echo Testing Complete!
echo ========================================
echo.
echo See DELIVERY_SERVICE_GUIDE.md for detailed documentation
echo ========================================
pause
