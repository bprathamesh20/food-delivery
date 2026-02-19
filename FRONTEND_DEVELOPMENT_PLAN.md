# Frontend Development Plan - Food Delivery System

**Tech Stack:** React + JavaScript + Tailwind CSS

---

## Phase 1: Project Setup & Core Infrastructure
**Goal:** Set up React project with routing, API client, and authentication

### Tasks:
1. Create React app with Vite
2. Install dependencies (React Router, Axios, Tailwind CSS)
3. Configure Tailwind CSS
4. Create folder structure
5. Set up API client with Axios interceptors
6. Create authentication context
7. Create protected route component
8. Create basic layout components (Header, Footer, Sidebar)

### Deliverables:
- `/frontend` folder with React app
- API client configured for `http://localhost:9090`
- Auth context with login/logout/token management
- Basic routing structure
- Reusable layout components

---

## Phase 2: User Authentication & Registration
**Goal:** Implement user signup, login, and profile management

### Pages:
1. **Login Page** (`/login`)
   - Email/Phone input
   - Password input
   - Login button
   - Link to signup

2. **Signup Page** (`/signup`)
   - Full name input
   - Email input
   - Phone input (10 digits)
   - Password input (min 6 chars)
   - Confirm password input
   - Signup button
   - Link to login

3. **User Profile Page** (`/profile`)
   - Display user info
   - Edit profile (future)
   - Logout button

### API Endpoints Used:
- POST `/api/auth/signup`
- POST `/api/auth/login`
- GET `/api/users/{id}`

### Deliverables:
- Login form with validation
- Signup form with validation
- JWT token storage in localStorage
- Automatic token inclusion in API requests
- Profile page showing user data

---

## Phase 3: Restaurant Browsing & Menu
**Goal:** Display restaurants and their menus

### Pages:
1. **Home/Restaurant List Page** (`/`)
   - Grid of restaurant cards
   - Restaurant name, address
   - Click to view menu

2. **Restaurant Menu Page** (`/restaurant/{id}`)
   - Restaurant details
   - Menu items grid/list
   - Item name, price, availability
   - Add to cart button
   - Cart summary sidebar

### Components:
- RestaurantCard
- MenuItem
- CartSidebar

### API Endpoints Used:
- GET `/api/restaurants`
- GET `/api/restaurants/{id}`
- GET `/api/menus/{restaurantId}`

### Deliverables:
- Restaurant listing page
- Restaurant menu page
- Shopping cart functionality (local state)
- Add/remove items from cart

---

## Phase 4: Order Placement & Checkout
**Goal:** Allow users to place orders

### Pages:
1. **Cart/Checkout Page** (`/checkout`)
   - Cart items review
   - Delivery address input
   - Special instructions textarea
   - Order summary (items, total)
   - Place order button

2. **Order Confirmation Page** (`/order/{id}`)
   - Order details
   - Order status
   - Payment status
   - Delivery tracking link

### API Endpoints Used:
- POST `/api/orders`
- GET `/api/orders/{id}`
- GET `/api/orders/customer/{customerId}`

### Deliverables:
- Checkout page with form
- Order placement functionality
- Order confirmation page
- Order history page

---

## Phase 5: Payment Integration
**Goal:** Integrate Razorpay payment gateway

### Pages:
1. **Payment Page** (`/payment/{orderId}`)
   - Order summary
   - Payment method selection
   - Razorpay integration
   - Payment success/failure handling

### API Endpoints Used:
- POST `/api/payments/razorpay/order`
- POST `/api/payments/razorpay/verify`
- GET `/api/payments/order/{orderId}`

### Deliverables:
- Razorpay integration
- Payment initiation
- Payment verification
- Success/failure pages

---

## Phase 6: Order Tracking & Delivery
**Goal:** Track order and delivery status

### Pages:
1. **Order Tracking Page** (`/track/{orderId}`)
   - Order status timeline
   - Delivery agent info
   - Live location (if available)
   - Estimated delivery time
   - Contact delivery agent

### Components:
- OrderStatusTimeline
- DeliveryAgentCard
- MapView (optional)

### API Endpoints Used:
- GET `/api/orders/{id}`
- GET `/delivery-service/api/v1/deliveries/order/{orderId}`
- GET `/delivery-service/api/v1/deliveries/{deliveryId}/tracking`

### Deliverables:
- Order tracking page
- Status timeline component
- Delivery agent information display
- Real-time status updates (polling or WebSocket)

---

## Phase 7: Delivery Agent Dashboard (Optional)
**Goal:** Separate interface for delivery agents

### Pages:
1. **Agent Login** (`/agent/login`)
2. **Agent Dashboard** (`/agent/dashboard`)
   - Available deliveries
   - Assigned deliveries
   - Update status
   - Update location

### API Endpoints Used:
- POST `/delivery-service/api/v1/auth/agent/login`
- GET `/delivery-service/api/v1/agents/me`
- GET `/delivery-service/api/v1/deliveries/agent/{agentId}`
- PUT `/delivery-service/api/v1/deliveries/{deliveryId}/status`
- PUT `/delivery-service/api/v1/agents/me/location`

### Deliverables:
- Agent login page
- Agent dashboard
- Delivery management interface

---

## Phase 8: Admin Dashboard (Optional)
**Goal:** Admin interface for managing system

### Pages:
1. **Admin Dashboard** (`/admin`)
   - Statistics overview
   - Manage restaurants
   - Manage orders
   - Manage delivery agents

### Deliverables:
- Admin dashboard
- CRUD operations for restaurants
- Order management
- Agent management

---

## Folder Structure

```
frontend/
├── public/
├── src/
│   ├── api/
│   │   ├── client.js          # Axios instance
│   │   ├── auth.js            # Auth API calls
│   │   ├── restaurants.js     # Restaurant API calls
│   │   ├── orders.js          # Order API calls
│   │   ├── payments.js        # Payment API calls
│   │   └── delivery.js        # Delivery API calls
│   ├── components/
│   │   ├── layout/
│   │   │   ├── Header.jsx
│   │   │   ├── Footer.jsx
│   │   │   └── Sidebar.jsx
│   │   ├── auth/
│   │   │   ├── LoginForm.jsx
│   │   │   └── SignupForm.jsx
│   │   ├── restaurant/
│   │   │   ├── RestaurantCard.jsx
│   │   │   └── MenuItem.jsx
│   │   ├── order/
│   │   │   ├── CartSidebar.jsx
│   │   │   ├── OrderCard.jsx
│   │   │   └── OrderStatusTimeline.jsx
│   │   └── common/
│   │       ├── Button.jsx
│   │       ├── Input.jsx
│   │       └── Card.jsx
│   ├── context/
│   │   ├── AuthContext.jsx
│   │   └── CartContext.jsx
│   ├── hooks/
│   │   ├── useAuth.js
│   │   └── useCart.js
│   ├── pages/
│   │   ├── auth/
│   │   │   ├── Login.jsx
│   │   │   └── Signup.jsx
│   │   ├── restaurant/
│   │   │   ├── RestaurantList.jsx
│   │   │   └── RestaurantMenu.jsx
│   │   ├── order/
│   │   │   ├── Checkout.jsx
│   │   │   ├── OrderConfirmation.jsx
│   │   │   ├── OrderHistory.jsx
│   │   │   └── OrderTracking.jsx
│   │   ├── payment/
│   │   │   └── Payment.jsx
│   │   └── Profile.jsx
│   ├── utils/
│   │   ├── constants.js
│   │   └── helpers.js
│   ├── App.jsx
│   ├── main.jsx
│   └── index.css
├── package.json
├── tailwind.config.js
├── vite.config.js
└── README.md
```

---

## Design Guidelines

### Colors (Tailwind):
- Primary: `orange-500` (food delivery theme)
- Secondary: `gray-700`
- Success: `green-500`
- Error: `red-500`
- Warning: `yellow-500`

### Typography:
- Headings: `font-bold`
- Body: `font-normal`
- Small text: `text-sm`

### Components:
- Cards: `rounded-lg shadow-md`
- Buttons: `rounded-md px-4 py-2`
- Inputs: `border rounded-md px-3 py-2`

---

## Next Steps

**Ready to start Phase 1?**
- Confirm the plan
- I'll create the React project structure
- Set up Tailwind CSS
- Create API client and auth context
- Build basic layout components

Let me know if you want to proceed with Phase 1!
