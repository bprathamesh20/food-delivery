@echo off
echo ========================================
echo Testing CORS Configuration
echo ========================================
echo.

echo Test 1: User Service Health Check
echo ----------------------------------
curl -s http://localhost:8000/api/auth/health
echo.
echo.

echo Test 2: Gateway Health Check
echo ----------------------------------
curl -s http://localhost:9090/actuator/health
echo.
echo.

echo Test 3: OPTIONS Preflight Request (CORS)
echo ----------------------------------
curl -X OPTIONS http://localhost:9090/api/auth/login ^
  -H "Origin: http://localhost:5173" ^
  -H "Access-Control-Request-Method: POST" ^
  -H "Access-Control-Request-Headers: Content-Type" ^
  -v
echo.
echo.

echo Test 4: Signup Request with CORS Headers
echo ----------------------------------
curl -X POST http://localhost:9090/api/auth/signup ^
  -H "Content-Type: application/json" ^
  -H "Origin: http://localhost:5173" ^
  -d "{\"fullName\":\"Test User\",\"email\":\"test@example.com\",\"phone\":\"1234567890\",\"password\":\"test123\",\"confirmPassword\":\"test123\"}" ^
  -v
echo.
echo.

echo Test 5: Login Request with CORS Headers
echo ----------------------------------
curl -X POST http://localhost:9090/api/auth/login ^
  -H "Content-Type: application/json" ^
  -H "Origin: http://localhost:5173" ^
  -d "{\"emailOrPhone\":\"test@example.com\",\"password\":\"test123\"}" ^
  -v
echo.
echo.

echo ========================================
echo Test Complete
echo ========================================
echo.
echo If all tests pass (200 OK), the backend is working correctly.
echo If frontend still fails, restart frontend dev server and clear browser cache.
echo.
echo See RESTART_FRONTEND.md for detailed instructions.
echo ========================================
pause
