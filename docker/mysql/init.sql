-- Create databases for all microservices
CREATE DATABASE IF NOT EXISTS fooddelivery;
CREATE DATABASE IF NOT EXISTS food_db;
CREATE DATABASE IF NOT EXISTS order_db;
CREATE DATABASE IF NOT EXISTS payment_db;
CREATE DATABASE IF NOT EXISTS delivery_db;
CREATE DATABASE IF NOT EXISTS notification_db;

-- Grant privileges
GRANT ALL PRIVILEGES ON fooddelivery.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON food_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON order_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON payment_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON delivery_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON notification_db.* TO 'root'@'%';

FLUSH PRIVILEGES;
