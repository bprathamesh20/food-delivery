# Frontend Setup Test

## Quick Verification

### 1. Check Dependencies
```bash
cd frontend
npm list react react-dom react-router-dom axios
```

Should show all packages installed.

### 2. Check Build (Fast Test)
```bash
cd frontend
npm run build
```

This will verify all imports and syntax are correct without starting the dev server.

### 3. Start Dev Server
```bash
cd frontend
npm run dev
```

Expected output:
```
VITE v7.x.x  ready in xxx ms

➜  Local:   http://localhost:5173/
➜  Network: use --host to expose
```

### 4. Open Browser
Navigate to: http://localhost:5173

You should see:
- ✅ Home page with hero section
- ✅ Navbar with logo and navigation
- ✅ "Delicious food, delivered to you" heading
- ✅ Search bar
- ✅ Featured restaurants section
- ✅ Footer

### 5. Test Navigation
Click through:
- ✅ Sign Up → Should show registration form
- ✅ Login → Should show login form
- ✅ Restaurants → Should show restaurant list
- ✅ Cart → Should show empty cart message

### 6. Test Authentication Flow
1. Click "Sign Up"
2. Fill in form with valid data
3. Submit
4. Should redirect to home page
5. Navbar should show user name
6. Should see "My Orders" link

### 7. Test Restaurant Browsing
1. Click "Restaurants"
2. Should see list of restaurants (if any exist in backend)
3. Click on a restaurant
4. Should see menu items
5. Click "Add to Cart"
6. Cart badge should update

### 8. Test Cart
1. Click cart icon
2. Should see added items
3. Update quantity with +/- buttons
4. Click "Proceed to Checkout"
5. Should redirect to login if not authenticated
6. If authenticated, should show checkout form

## All Tests Pass? ✅

Your frontend is ready to use!

## Troubleshooting

### Build Errors
If `npm run build` fails:
1. Check for syntax errors in console
2. Verify all imports are correct
3. Run `npm install` again

### Dev Server Won't Start
1. Check if port 5173 is in use
2. Kill any existing Vite processes
3. Try `npm run dev -- --port 5174`

### Blank Page
1. Open browser console (F12)
2. Check for JavaScript errors
3. Verify API Gateway is running on 9090
4. Check network tab for failed requests

### API Calls Failing
1. Ensure backend services are running
2. Check API Gateway on http://localhost:9090
3. Verify Eureka shows all services
4. Check CORS configuration in gateway
