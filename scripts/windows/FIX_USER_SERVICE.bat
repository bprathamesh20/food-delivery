@echo off
echo ========================================
echo Fixing User Service - CORS Configuration
echo ========================================
echo.

echo Step 1: Cleaning and building User Service...
cd user-service-backend
call mvn clean install -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo Step 2: Build successful!
echo.
echo ========================================
echo Next Steps:
echo ========================================
echo 1. Stop the currently running User Service (Ctrl+C in its terminal)
echo 2. Restart User Service with: cd user-service-backend ^&^& mvn spring-boot:run
echo 3. Wait 30 seconds for service to register in Eureka
echo 4. Test signup from frontend: http://localhost:5173
echo.
echo Or run RUN_LOCALLY.bat to restart all services
echo ========================================
pause
