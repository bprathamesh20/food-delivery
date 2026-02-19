@echo off
echo ========================================
echo Clear Kafka Topics (Remove Old Messages)
echo ========================================
echo.
echo This will delete and recreate the delivery-events topic
echo to remove old messages with incorrect format.
echo.
echo IMPORTANT: Make sure Kafka is running!
echo.
pause

set KAFKA_HOME=C:\kafka
set KAFKA_BIN=%KAFKA_HOME%\bin\windows

echo.
echo [1/2] Deleting delivery-events topic...
%KAFKA_BIN%\kafka-topics.bat --bootstrap-server localhost:9098 --delete --topic delivery-events

echo.
echo Waiting 5 seconds...
timeout /t 5 /nobreak

echo.
echo [2/2] Recreating delivery-events topic...
%KAFKA_BIN%\kafka-topics.bat --bootstrap-server localhost:9098 --create --topic delivery-events --partitions 3 --replication-factor 1

echo.
echo ========================================
echo Topic cleared successfully!
echo ========================================
echo.
echo Old messages with incorrect format have been removed.
echo New deliveries will now work without deserialization errors.
echo.
pause
