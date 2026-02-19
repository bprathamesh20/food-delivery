@echo off
echo ========================================
echo BUILDING NOTIFICATION SERVICE
echo ========================================
echo.

cd notification-service\notification
echo Building Notification Service...
call mvn clean package -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo ✅ BUILD SUCCESSFUL!
    echo ========================================
    echo.
    echo Now restart the Notification Service:
    echo 1. Stop the current service (Ctrl+C)
    echo 2. Run: java -jar notification-service\notification\target\notification-0.0.1-SNAPSHOT.jar
    echo.
) else (
    echo.
    echo ========================================
    echo ❌ BUILD FAILED!
    echo ========================================
)

cd ..\..
pause
