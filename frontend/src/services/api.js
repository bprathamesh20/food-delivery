import axios from 'axios';

const API_BASE_URL = '/api';
const DELIVERY_API_BASE = '/delivery-service/api';

// Create axios instance
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Create delivery service axios instance
const deliveryApi = axios.create({
  baseURL: DELIVERY_API_BASE,
  headers: {
    'Content-Type': 'application/json',
  },
});

const addAuthToken = (config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
};

// Request interceptor to add auth token (use agentToken for delivery service)
const addAuthTokenDelivery = (config) => {
  const token = localStorage.getItem('agentToken') || localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
};

api.interceptors.request.use(addAuthToken, (error) => Promise.reject(error));
deliveryApi.interceptors.request.use(addAuthTokenDelivery, (error) => Promise.reject(error));

// Response interceptor to handle errors
const handleResponseError = (error) => {
  // Don't redirect to login if we're already on login/signup pages
  const isAuthPage = window.location.pathname === '/login' || 
                     window.location.pathname === '/signup' ||
                     window.location.pathname.startsWith('/agent');
  
  if (error.response?.status === 401 && !isAuthPage) {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '/login';
  }
  return Promise.reject(error);
};

const handleResponseErrorDelivery = (error) => {
  // For delivery service, redirect to agent login on 401
  if (error.response?.status === 401 && window.location.pathname.startsWith('/agent/dashboard')) {
    localStorage.removeItem('agentToken');
    localStorage.removeItem('agent');
    window.location.href = '/agent/login';
  }
  return Promise.reject(error);
};

api.interceptors.response.use((response) => response, handleResponseError);
deliveryApi.interceptors.response.use((response) => response, handleResponseErrorDelivery);

// ================== AUTH SERVICES ==================
export const authService = {
  signup: (data) => {
    console.log('authService.signup called with:', data);
    return api.post('/auth/signup', data);
  },
  login: (data) => {
    console.log('authService.login called with:', data);
    console.log('Making POST request to /auth/login');
    return api.post('/auth/login', data);
  },
  getUser: (id) => api.get(`/users/${id}`),
};

// ================== RESTAURANT SERVICES ==================
export const restaurantService = {
  getAll: () => api.get('/restaurants'),
  getById: (id) => api.get(`/restaurants/${id}`),
  register: (data) => api.post('/restaurants/register', data),
  disable: (id) => api.put(`/restaurants/${id}/disable`),
  delete: (id) => api.delete(`/restaurants/${id}`),
};

// ================== MENU SERVICES ==================
export const menuService = {
  getByRestaurant: (restaurantId) => api.get(`/menus/${restaurantId}`),
  getItem: (menuItemId) => api.get(`/menus/item/${menuItemId}`),
  addItem: (restaurantId, data) => api.post(`/menus/${restaurantId}`, data),
  disableItem: (menuItemId) => api.put(`/menus/item/${menuItemId}/disable`),
  deleteItem: (menuItemId) => api.delete(`/menus/item/${menuItemId}`),
};

// ================== ORDER SERVICES ==================
export const orderService = {
  create: (data) => api.post('/orders', data),
  getById: (id) => api.get(`/orders/${id}`),
  getByCustomer: (customerId) => api.get(`/orders/customer/${customerId}`),
  getByRestaurant: (restaurantId) => api.get(`/orders/restaurant/${restaurantId}`),
  getAll: () => api.get('/orders'),
  updateStatus: (id, status) => api.put(`/orders/${id}/status`, { status }),
  cancel: (id) => api.delete(`/orders/${id}`),
};

// ================== PAYMENT SERVICES ==================
export const paymentService = {
  createRazorpayOrder: (data) => api.post('/payments/razorpay/order', data),
  verifyRazorpayPayment: (data) => api.post('/payments/razorpay/verify', data),
  getByOrderId: (orderId) => api.get(`/payments/order/${orderId}`),
  getById: (paymentId) => api.get(`/payments/${paymentId}`),
  refund: (orderId) => api.post(`/payments/refund/${orderId}`),
};

// ================== DELIVERY SERVICES ==================
export const deliveryService = {
  getById: (id) => deliveryApi.get(`/v1/deliveries/${id}`),
  getByOrderId: (orderId) => deliveryApi.get(`/v1/deliveries/order/${orderId}`),
  getByAgent: (agentId) => deliveryApi.get(`/v1/deliveries/agent/${agentId}`),
  getByCustomer: (customerId) => deliveryApi.get(`/v1/deliveries/customer/${customerId}`),
  getTracking: (deliveryId) => deliveryApi.get(`/v1/deliveries/${deliveryId}/tracking`),
  updateStatus: (deliveryId, status, remarks) => 
    deliveryApi.put(`/v1/deliveries/${deliveryId}/status`, { status, remarks }),
  updateLocation: (deliveryId, latitude, longitude, remarks) =>
    deliveryApi.post(`/v1/deliveries/${deliveryId}/location`, { latitude, longitude, remarks }),
};

// ================== DELIVERY AGENT SERVICES ==================
export const agentService = {
  register: (data) => deliveryApi.post('/v1/auth/agent/register', data),
  login: (data) => deliveryApi.post('/v1/auth/agent/login', data),
  getProfile: () => deliveryApi.get('/v1/agents/me'),
  getAll: () => deliveryApi.get('/v1/agents'),
  getAvailable: () => deliveryApi.get('/v1/agents/available'),
  getById: (id) => deliveryApi.get(`/v1/agents/${id}`),
  updateStatus: (status) => deliveryApi.put('/v1/agents/me/status', { status }),
  updateLocation: (latitude, longitude) => 
    deliveryApi.put('/v1/agents/me/location', { latitude, longitude }),
  getAgentDeliveries: (agentId) => deliveryApi.get(`/v1/deliveries/agent/${agentId}`),
};

export { api, deliveryApi };
