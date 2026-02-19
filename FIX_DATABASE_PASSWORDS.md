# 🔧 Fix Database Password Issues

## Problem
Services have different MySQL passwords configured:

| Service | Password in Config |
|---------|-------------------|
| User Service | `Welcome@123` |
| Restaurant Service | `root` |
| Order Service | `root` |
| Payment Service | `root` |
| Delivery Service | `12345678` |

**Your MySQL root password is likely different from these!**

---

## ✅ Solution: Update All Passwords

### Option 1: Find Your MySQL Password

```bash
# Try connecting to MySQL
mysql -u root -p

# If you don't remember the password, you may need to reset it
```

### Option 2: Update Configuration Files

Once you know your MySQL password, update these files:

#### 1. User Service
**File:** `user-service-backend/src/main/resources/application.properties`
```properties
spring.datasource.password=YOUR_ACTUAL_PASSWORD
```

#### 2. Restaurant Service
**File:** `restaurant-service/src/main/resources/application.yaml`
```yaml
datasource:
  password: YOUR_ACTUAL_PASSWORD
```

#### 3. Order Service
**File:** `Food-deli-order_service/src/main/resources/application.yml`
```yaml
datasource:
  password: YOUR_ACTUAL_PASSWORD
```

#### 4. Payment Service
**File:** `payments-service/payment/demo/src/main/resources/application.yaml`
```yaml
datasource:
  password: YOUR_ACTUAL_PASSWORD
```

#### 5. Delivery Service
**File:** `delivery-service/src/main/resources/application.yaml`
```yaml
datasource:
  password: YOUR_ACTUAL_PASSWORD
```

---

## 🚀 Quick Fix Script

I'll create a script to help you update all passwords at once.

**What's your MySQL root password?**

Common defaults:
- `root`
- `password`
- `admin`
- `Welcome@123`
- `12345678`
- Empty (no password)

---

## 🔍 How to Find Your MySQL Password

### Method 1: Check MySQL Configuration
```bash
# Windows - Check MySQL config
type "C:\ProgramData\MySQL\MySQL Server 8.0\my.ini"
```

### Method 2: Reset MySQL Password (if forgotten)

1. Stop MySQL service
2. Start MySQL in safe mode
3. Reset password
4. Restart MySQL

**Detailed steps:** https://dev.mysql.com/doc/refman/8.0/en/resetting-permissions.html

---

## ⚡ Temporary Workaround

If you can't find/reset the password, create a new MySQL user:

```sql
-- Connect to MySQL as root (if you can)
mysql -u root -p

-- Create new user for the application
CREATE USER 'fooddelivery'@'localhost' IDENTIFIED BY 'fooddelivery123';

-- Grant all privileges
GRANT ALL PRIVILEGES ON *.* TO 'fooddelivery'@'localhost';
FLUSH PRIVILEGES;
```

Then update all services to use:
- Username: `fooddelivery`
- Password: `fooddelivery123`

---

## 📝 Next Steps

1. Find your MySQL root password
2. Tell me the password
3. I'll update all configuration files
4. Retry running the services

**Or** tell me what password you want to use, and I'll help you set it up!
