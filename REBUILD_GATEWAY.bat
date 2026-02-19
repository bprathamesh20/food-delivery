@echo off
echo ========================================
echo REBUILDING API GATEWAY
echo ========================================
echo.

cd payments-service\gateway
echo Building API Gateway...
call mvn clean package -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo ✅ BUILD SUCCESSFUL!
    echo ========================================
    echo.
    echo Now restart the API Gateway:
    echo 1. Stop the current gateway (Ctrl+C)
    echo 2. Run: java -jar payments-service\gateway\target\gateway-0.0.1-SNAPSHOT.jar
    echo.
    echo Changes applied:
    echo - Added /notification-service/** route with StripPrefix=1
    echo - Notifications should now be accessible from frontend
    echo.
) else (
    echo.
    echo ========================================
    echo ❌ BUILD FAILED!
    echo ========================================
)

cd ..\..
pause
