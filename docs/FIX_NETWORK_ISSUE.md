# 🔧 Fix Maven Network Issue

## Problem
Maven can't reach `repo.maven.apache.org` - "No such host is known"

This is a **network/DNS issue** on your system.

---

## ✅ Solutions (Try in Order)

### Solution 1: Check Internet Connection
```bash
# Test if you can reach Maven Central
ping repo.maven.apache.org

# If ping fails, check your internet connection
```

### Solution 2: Flush DNS Cache (Windows)
```bash
# Open Command Prompt as Administrator
ipconfig /flushdns

# Then try building again
```

### Solution 3: Use Google DNS
1. Open Network Settings
2. Change DNS to:
   - Primary: 8.8.8.8
   - Secondary: 8.8.4.4
3. Restart network adapter
4. Try building again

### Solution 4: Clear Maven Cache
```bash
# Delete Maven cache
rmdir /s /q %USERPROFILE%\.m2\repository

# Try building again - Maven will re-download everything
```

### Solution 5: Use Maven Offline Mode (If you have dependencies cached)
```bash
# Build in offline mode
mvn clean package -DskipTests -o
```

### Solution 6: Configure Maven Proxy (If behind corporate firewall)

Create/Edit: `%USERPROFILE%\.m2\settings.xml`

```xml
<settings>
  <proxies>
    <proxy>
      <id>myproxy</id>
      <active>true</active>
      <protocol>http</protocol>
      <host>YOUR_PROXY_HOST</host>
      <port>YOUR_PROXY_PORT</port>
      <username>YOUR_USERNAME</username>
      <password>YOUR_PASSWORD</password>
    </proxy>
  </proxies>
</settings>
```

### Solution 7: Use Alternative Maven Repository

Create/Edit: `%USERPROFILE%\.m2\settings.xml`

```xml
<settings>
  <mirrors>
    <mirror>
      <id>aliyun</id>
      <mirrorOf>central</mirrorOf>
      <name>Aliyun Maven Mirror</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
</settings>
```

---

## 🚀 Quick Fix: Run Pre-Built Services

If network issues persist, I can help you:

1. **Skip the build** - Use pre-compiled JARs if available
2. **Run without Maven** - Use IDE (IntelliJ/Eclipse) to run services
3. **Fix network first** - Then build later

---

## 🔍 Diagnose the Issue

Run these commands to understand the problem:

```bash
# Test DNS resolution
nslookup repo.maven.apache.org

# Test connectivity
curl https://repo.maven.apache.org/maven2/

# Check Maven settings
mvn -X help:effective-settings
```

---

## ⚡ Immediate Workaround

If you need to get started NOW:

1. **Fix the compilation error** (already done)
2. **Run services that already built successfully**:
   - User Service
   - Delivery Service  
   - Notification Service
   - Payment Service
   - Eureka Server

3. **Skip the failing services temporarily**:
   - Order Service (network issue)
   - Restaurant Service (compilation fixed, retry)
   - Gateway (network issue)

---

## 📝 Next Steps

1. Fix your network/DNS issue
2. Run: `BUILD_ALL.bat` again
3. All services should build successfully
4. Run: `RUN_LOCALLY.bat`

---

**Most Common Cause:** DNS resolution failure or firewall blocking Maven Central.

**Quick Test:**
```bash
ping repo.maven.apache.org
```

If ping fails, it's a network issue on your system, not a Maven problem.
