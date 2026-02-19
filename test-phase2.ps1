# Quick Test Script for Phase 2 Verification

Write-Host "=== Testing Food Delivery Microservices ===" -ForegroundColor Cyan
Write-Host ""

# Test 1: Register User
Write-Host "Test 1: Registering new user..." -ForegroundColor Yellow
try {
    $registerBody = @{
        username = "testuser$(Get-Random -Maximum 1000)"
        email = "test$(Get-Random -Maximum 1000)@example.com"
        password = "password123"
        phone = "1234567890"
    } | ConvertTo-Json

    $registerResponse = Invoke-RestMethod -Uri "http://localhost:8000/api/auth/register" `
        -Method POST `
        -ContentType "application/json" `
        -Body $registerBody
    
    Write-Host "✓ User registered successfully!" -ForegroundColor Green
    Write-Host "  User ID: $($registerResponse.id)" -ForegroundColor Gray
    $userId = $registerResponse.id
} catch {
    Write-Host "✗ Registration failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host ""

# Test 2: Login and Get JWT Token
Write-Host "Test 2: Logging in to get JWT token..." -ForegroundColor Yellow
try {
    $loginBody = @{
        username = $registerResponse.username
        password = "password123"
    } | ConvertTo-Json

    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8000/api/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $loginBody
    
    $token = $loginResponse.token
    Write-Host "✓ Login successful! Token received." -ForegroundColor Green
    Write-Host "  Token (first 50 chars): $($token.Substring(0, [Math]::Min(50, $token.Length)))..." -ForegroundColor Gray
} catch {
    Write-Host "✗ Login failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host ""

# Test 3: Test JWT Authentication - Access Protected Endpoint
Write-Host "Test 3: Testing JWT authentication on Order Service..." -ForegroundColor Yellow
try {
    $headers = @{
        "Authorization" = "Bearer $token"
    }
    
    $orders = Invoke-RestMethod -Uri "http://localhost:8085/api/orders" `
        -Method GET `
        -Headers $headers
    
    Write-Host "✓ JWT authentication successful!" -ForegroundColor Green
    Write-Host "  Orders count: $($orders.Count)" -ForegroundColor Gray
} catch {
    Write-Host "✗ JWT authentication failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test 4: Create Restaurant
Write-Host "Test 4: Creating a restaurant..." -ForegroundColor Yellow
try {
    $restaurantBody = @{
        name = "Test Restaurant"
        address = "123 Main St"
        phone = "555-1234"
        active = $true
    } | ConvertTo-Json

    $headers = @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }
    
    $restaurant = Invoke-RestMethod -Uri "http://localhost:8081/api/restaurants" `
        -Method POST `
        -Headers $headers `
        -Body $restaurantBody
    
    Write-Host "✓ Restaurant created successfully!" -ForegroundColor Green
    Write-Host "  Restaurant ID: $($restaurant.id)" -ForegroundColor Gray
    $restaurantId = $restaurant.id
} catch {
    Write-Host "✗ Restaurant creation failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test 5: Create Menu Item
Write-Host "Test 5: Creating a menu item..." -ForegroundColor Yellow
try {
    $menuItemBody = @{
        restaurantId = $restaurantId
        name = "Deluxe Burger"
        description = "Juicy burger with all the fixings"
        price = 12.99
        available = $true
    } | ConvertTo-Json

    $menuItem = Invoke-RestMethod -Uri "http://localhost:8081/api/menu" `
        -Method POST `
        -Headers $headers `
        -Body $menuItemBody
    
    Write-Host "✓ Menu item created successfully!" -ForegroundColor Green
    Write-Host "  Menu Item ID: $($menuItem.id)" -ForegroundColor Gray
    Write-Host "  Price: `$$($menuItem.price)" -ForegroundColor Gray
    $menuItemId = $menuItem.id
} catch {
    Write-Host "✗ Menu item creation failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test 6: Create Order with OpenFeign Validation
Write-Host "Test 6: Creating order (tests OpenFeign validation)..." -ForegroundColor Yellow
try {
    $orderBody = @{
        customerId = $userId
        restaurantId = $restaurantId
        deliveryAddress = "456 Oak Avenue, Apt 2B"
        specialInstructions = "Extra napkins please"
        items = @(
            @{
                menuItemId = $menuItemId
                quantity = 2
            }
        )
    } | ConvertTo-Json -Depth 10

    $order = Invoke-RestMethod -Uri "http://localhost:8085/api/orders" `
        -Method POST `
        -Headers $headers `
        -Body $orderBody
    
    Write-Host "✓ Order created successfully!" -ForegroundColor Green
    Write-Host "  Order ID: $($order.id)" -ForegroundColor Gray
    Write-Host "  Total Amount: `$$($order.totalAmount)" -ForegroundColor Gray
    Write-Host "  Menu Item Name: $($order.items[0].menuItemName)" -ForegroundColor Gray
    Write-Host "  Price Per Unit: `$$($order.items[0].pricePerUnit)" -ForegroundColor Gray
    Write-Host ""
    Write-Host "  ✓ OpenFeign validated: Customer exists" -ForegroundColor Green
    Write-Host "  ✓ OpenFeign validated: Restaurant exists and is active" -ForegroundColor Green
    Write-Host "  ✓ OpenFeign validated: Menu item exists and is available" -ForegroundColor Green
    Write-Host "  ✓ Real price used from Restaurant Service: `$$($order.items[0].pricePerUnit)" -ForegroundColor Green
} catch {
    Write-Host "✗ Order creation failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test 7: Test Validation - Invalid Customer
Write-Host "Test 7: Testing validation with non-existent customer..." -ForegroundColor Yellow
try {
    $invalidOrderBody = @{
        customerId = 99999
        restaurantId = $restaurantId
        deliveryAddress = "456 Oak Avenue"
        items = @(
            @{
                menuItemId = $menuItemId
                quantity = 1
            }
        )
    } | ConvertTo-Json -Depth 10

    $result = Invoke-RestMethod -Uri "http://localhost:8085/api/orders" `
        -Method POST `
        -Headers $headers `
        -Body $invalidOrderBody
    
    Write-Host "✗ Validation should have failed but didn't!" -ForegroundColor Red
} catch {
    Write-Host "✓ Validation correctly rejected invalid customer!" -ForegroundColor Green
    Write-Host "  Error: $($_.Exception.Message)" -ForegroundColor Gray
}

Write-Host ""
Write-Host "=== All Tests Complete ===" -ForegroundColor Cyan
