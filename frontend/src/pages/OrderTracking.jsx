import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { MapPin, Phone, User, Clock, CheckCircle, Package, Truck, Home, Navigation, RefreshCw } from 'lucide-react';
import { orderService, deliveryService } from '../services/api';
import Loading from '../components/Loading';
import DeliveryMap from '../components/DeliveryMap';
import { useDeliveryTracking } from '../hooks/useDeliveryTracking';
import toast from 'react-hot-toast';

const orderStatuses = [
  { key: 'PENDING', label: 'Order Placed', icon: Clock },
  { key: 'CONFIRMED', label: 'Confirmed', icon: CheckCircle },
  { key: 'PREPARING', label: 'Preparing', icon: Package },
  { key: 'READY', label: 'Ready', icon: CheckCircle },
  { key: 'PICKED_UP', label: 'Picked Up', icon: Truck },
  { key: 'DELIVERED', label: 'Delivered', icon: Home },
];

export default function OrderTracking() {
  const { orderId } = useParams();
  const navigate = useNavigate();
  const [order, setOrder] = useState(null);
  const [delivery, setDelivery] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showMap, setShowMap] = useState(true);

  // Use delivery tracking hook for real-time updates
  const {
    agentLocation,
    tracking,
    isPolling,
    refresh: refreshTracking
  } = useDeliveryTracking(delivery?.id, {
    autoStart: true,
    onLocationUpdate: (location) => {
      console.log('Agent location updated:', location);
    },
    onError: (error) => {
      console.error('Tracking error:', error);
    }
  });

  useEffect(() => {
    loadOrderAndDelivery();
    // Poll for updates every 5 seconds (reduced from 10 for faster updates)
    const interval = setInterval(loadOrderAndDelivery, 5000);
    return () => clearInterval(interval);
  }, [orderId]);

  const loadOrderAndDelivery = async () => {
    try {
      console.log('Loading order and delivery for orderId:', orderId);
      const orderRes = await orderService.getById(orderId);
      console.log('Order response:', orderRes.data);
      setOrder(orderRes.data);

      // Try to load delivery info
      try {
        const deliveryRes = await deliveryService.getByOrderId(orderId);
        console.log('Delivery response:', deliveryRes.data);
        setDelivery(deliveryRes.data);
      } catch (error) {
        console.log('Delivery not found or not assigned yet:', error.response?.status);
        // Delivery might not be assigned yet
        setDelivery(null);
      }
    } catch (error) {
      console.error('Failed to load order:', error);
      console.error('Error details:', error.response?.data);
      toast.error('Failed to load order details');
    } finally {
      setLoading(false);
    }
  };

  const getCurrentStatusIndex = () => {
    if (!order) return 0;
    return orderStatuses.findIndex((s) => s.key === order.orderStatus);
  };

  const getEstimatedTime = () => {
    if (!delivery?.estimatedDeliveryTime) return null;
    const eta = new Date(delivery.estimatedDeliveryTime);
    const now = new Date();
    const diffMinutes = Math.max(0, Math.floor((eta - now) / (1000 * 60)));
    
    if (diffMinutes === 0) return 'Arriving now';
    if (diffMinutes < 60) return `${diffMinutes} min`;
    
    const hours = Math.floor(diffMinutes / 60);
    const minutes = diffMinutes % 60;
    return `${hours}h ${minutes}m`;
  };

  const handleRefreshTracking = () => {
    refreshTracking();
    loadOrderAndDelivery();
    toast.success('Tracking refreshed');
  };

  if (loading) {
    return <Loading fullScreen message="Loading order tracking..." />;
  }

  if (!order) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <p className="text-gray-500 text-lg mb-4">Order not found</p>
          <button onClick={() => navigate('/orders')} className="btn-primary">
            View All Orders
          </button>
        </div>
      </div>
    );
  }

  const currentStatusIndex = getCurrentStatusIndex();

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-2">Track Your Order</h1>
            <p className="text-gray-600">Order ID: #{order.id}</p>
          </div>
          <div className="flex items-center gap-3">
            {delivery && (
              <button
                onClick={handleRefreshTracking}
                className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
                disabled={loading}
              >
                <RefreshCw className={`h-4 w-4 ${isPolling ? 'animate-spin' : ''}`} />
                Refresh
              </button>
            )}
            {delivery && (
              <button
                onClick={() => setShowMap(!showMap)}
                className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-orange-500 rounded-lg hover:bg-orange-600 transition-colors"
              >
                <Navigation className="h-4 w-4" />
                {showMap ? 'Hide Map' : 'Show Map'}
              </button>
            )}
          </div>
        </div>

        {/* Live Delivery Map */}
        {delivery && showMap && (
          <div className="card p-6 mb-6">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-xl font-semibold text-gray-900 flex items-center">
                <Navigation className="h-5 w-5 mr-2 text-orange-500" />
                Live Tracking
              </h2>
              {agentLocation && (
                <div className="flex items-center gap-2 text-sm text-gray-600">
                  <div className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></div>
                  <span>Live location</span>
                  {getEstimatedTime() && (
                    <>
                      <span className="mx-2">•</span>
                      <Clock className="h-4 w-4" />
                      <span className="font-medium text-orange-600">{getEstimatedTime()}</span>
                    </>
                  )}
                </div>
              )}
            </div>
            
            <DeliveryMap
              delivery={delivery}
              agentLocation={agentLocation}
              className="h-80 w-full rounded-lg border border-gray-200"
              showRoute={true}
              autoFit={true}
            />
            
            {!agentLocation && delivery.status !== 'DELIVERED' && (
              <div className="mt-4 p-3 bg-yellow-50 border border-yellow-200 rounded-lg">
                <p className="text-sm text-yellow-800">
                  📍 Waiting for delivery agent to start tracking...
                </p>
              </div>
            )}
          </div>
        )}

        {/* Order Status Timeline */}
        <div className="card p-6 mb-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-6">Order Status</h2>
          <div className="relative">
            {orderStatuses.map((status, index) => {
              const StatusIcon = status.icon;
              const isCompleted = index <= currentStatusIndex;
              const isCurrent = index === currentStatusIndex;
              const isCancelled = order.orderStatus === 'CANCELLED';

              return (
                <div key={status.key} className="flex items-start mb-8 last:mb-0">
                  {/* Timeline Line */}
                  {index < orderStatuses.length - 1 && (
                    <div
                      className={`absolute left-6 top-12 w-0.5 h-16 ${
                        isCompleted && !isCancelled ? 'bg-green-500' : 'bg-gray-300'
                      }`}
                      style={{ marginTop: '-0.5rem' }}
                    />
                  )}

                  {/* Icon */}
                  <div
                    className={`relative z-10 flex items-center justify-center w-12 h-12 rounded-full flex-shrink-0 ${
                      isCompleted && !isCancelled
                        ? 'bg-green-500 text-white'
                        : isCurrent && !isCancelled
                        ? 'bg-orange-500 text-white'
                        : 'bg-gray-200 text-gray-400'
                    }`}
                  >
                    <StatusIcon className="h-6 w-6" />
                  </div>

                  {/* Content */}
                  <div className="ml-4 flex-1">
                    <h3
                      className={`font-semibold ${
                        isCompleted || isCurrent ? 'text-gray-900' : 'text-gray-400'
                      }`}
                    >
                      {status.label}
                    </h3>
                    {isCurrent && !isCancelled && (
                      <p className="text-sm text-orange-500 font-medium mt-1">In Progress</p>
                    )}
                    {isCompleted && index < currentStatusIndex && !isCancelled && (
                      <p className="text-sm text-green-600 mt-1">Completed</p>
                    )}
                  </div>
                </div>
              );
            })}

            {/* Cancelled Status */}
            {order.orderStatus === 'CANCELLED' && (
              <div className="flex items-start">
                <div className="flex items-center justify-center w-12 h-12 rounded-full bg-red-500 text-white flex-shrink-0">
                  <span className="text-2xl">✕</span>
                </div>
                <div className="ml-4">
                  <h3 className="font-semibold text-gray-900">Order Cancelled</h3>
                  <p className="text-sm text-red-600 mt-1">This order has been cancelled</p>
                </div>
              </div>
            )}
          </div>
        </div>

        {/* Delivery Agent Info */}
        {delivery && delivery.deliveryAgent && (
          <div className="card p-6 mb-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">Delivery Agent</h2>
            <div className="flex items-center space-x-4">
              <div className="w-16 h-16 bg-orange-100 rounded-full flex items-center justify-center">
                <User className="h-8 w-8 text-orange-500" />
              </div>
              <div className="flex-1">
                <h3 className="font-semibold text-gray-900">{delivery.deliveryAgent.name}</h3>
                <div className="flex items-center gap-2 mt-1">
                  <span className="text-sm text-gray-600">{delivery.deliveryAgent.vehicleType}</span>
                  {delivery.deliveryAgent.vehicleNumber && (
                    <>
                      <span className="text-gray-400">•</span>
                      <span className="text-sm text-gray-600">{delivery.deliveryAgent.vehicleNumber}</span>
                    </>
                  )}
                </div>
                {delivery.deliveryAgent.rating && (
                  <div className="flex items-center gap-1 mt-1">
                    <span className="text-yellow-400">⭐</span>
                    <span className="text-sm font-medium text-gray-700">{delivery.deliveryAgent.rating}</span>
                    <span className="text-sm text-gray-500">({delivery.deliveryAgent.totalDeliveries || 0} deliveries)</span>
                  </div>
                )}
              </div>
              <div className="flex flex-col gap-2">
                <a
                  href={`tel:${delivery.deliveryAgent.phoneNumber}`}
                  className="btn-primary flex items-center space-x-2"
                >
                  <Phone className="h-5 w-5" />
                  <span>Call</span>
                </a>
                {agentLocation && (
                  <div className="text-xs text-center text-gray-500">
                    Last updated: {new Date(agentLocation.timestamp).toLocaleTimeString()}
                  </div>
                )}
              </div>
            </div>
          </div>
        )}

        {/* Delivery Address */}
        <div className="card p-6 mb-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
            <MapPin className="h-5 w-5 mr-2 text-orange-500" />
            Delivery Address
          </h2>
          <p className="text-gray-700">{order.deliveryAddress}</p>
          {order.specialInstructions && (
            <div className="mt-3 pt-3 border-t">
              <p className="text-sm text-gray-600 mb-1">Special Instructions:</p>
              <p className="text-gray-700">{order.specialInstructions}</p>
            </div>
          )}
        </div>

        {/* Order Summary */}
        <div className="card p-6 mb-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">Order Summary</h2>
          <div className="space-y-3 mb-4">
            {order.items.map((item) => (
              <div key={item.id} className="flex justify-between">
                <span className="text-gray-700">
                  {item.menuItemName} x {item.quantity}
                </span>
                <span className="text-gray-900 font-semibold">₹{item.subtotal.toFixed(2)}</span>
              </div>
            ))}
          </div>
          <div className="border-t pt-3 flex justify-between text-lg font-bold text-gray-900">
            <span>Total</span>
            <span>₹{order.totalAmount.toFixed(2)}</span>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="flex flex-col sm:flex-row gap-4">
          <button onClick={() => navigate('/orders')} className="flex-1 btn-outline">
            View All Orders
          </button>
          <button onClick={() => navigate('/restaurants')} className="flex-1 btn-primary">
            Order Again
          </button>
        </div>
      </div>
    </div>
  );
}
