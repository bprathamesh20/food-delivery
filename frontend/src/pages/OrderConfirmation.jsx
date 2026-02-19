import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { CheckCircle, MapPin, Clock, Package, Truck, Home, X } from 'lucide-react';
import { orderService } from '../services/api';
import Loading from '../components/Loading';
import toast from 'react-hot-toast';

const statusIcons = {
  PENDING: Clock,
  CONFIRMED: CheckCircle,
  PREPARING: Package,
  READY: CheckCircle,
  PICKED_UP: Truck,
  DELIVERED: Home,
  CANCELLED: X,
};

const statusColors = {
  PENDING: 'text-yellow-500',
  CONFIRMED: 'text-blue-500',
  PREPARING: 'text-orange-500',
  READY: 'text-green-500',
  PICKED_UP: 'text-purple-500',
  DELIVERED: 'text-green-600',
  CANCELLED: 'text-red-500',
};

export default function OrderConfirmation() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);

  console.log('OrderConfirmation component rendered with id:', id);

  useEffect(() => {
    if (id) {
      loadOrder();
    } else {
      console.error('No order ID provided');
      setLoading(false);
    }
  }, [id]);

  const loadOrder = async () => {
    try {
      console.log('Loading order with ID:', id);
      const response = await orderService.getById(id);
      console.log('Order response:', response.data);
      setOrder(response.data);
    } catch (error) {
      console.error('Failed to load order:', error);
      console.error('Error details:', error.response?.data);
      toast.error('Failed to load order details');
    } finally {
      setLoading(false);
    }
  };
  // console.log("Order Status:", order.orderStatus);
  // console.log("StatusIcon:", StatusIcon);


  if (loading) {
    return <Loading fullScreen message="Loading order details..." />;
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

  const StatusIcon = order ? statusIcons[order.orderStatus] : Clock;

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Success Header */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-green-100 rounded-full mb-4">
            <CheckCircle className="h-10 w-10 text-green-500" />
          </div>
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Order Placed Successfully!</h1>
          <p className="text-gray-600">Order ID: #{order.id}</p>
        </div>

        {/* Order Status */}
        <div className="card p-6 mb-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-xl font-semibold text-gray-900">Order Status</h2>
            <Link to={`/track/${order.id}`} className="text-orange-500 hover:text-orange-600 font-semibold">
              Track Order →
            </Link>
          </div>
          <div className="flex items-center space-x-3">
            {typeof StatusIcon === 'function' ? (
              <StatusIcon className={`h-8 w-8 ${statusColors[order.orderStatus] || 'text-gray-500'}`} />
            ) : (
              <StatusIcon
                className={`h-8 w-8 ${statusColors[order.orderStatus] || 'text-gray-500'}`}
              />
            )}
            <div>
              <p className="font-semibold text-gray-900">{order.orderStatus?.replace('_', ' ') || 'Unknown'}</p>
              <p className="text-sm text-gray-600">
                {order.orderStatus === 'PENDING' && 'Waiting for restaurant confirmation'}
                {order.orderStatus === 'CONFIRMED' && 'Restaurant is preparing your order'}
                {order.orderStatus === 'PREPARING' && 'Your food is being prepared'}
                {order.orderStatus === 'READY' && 'Order is ready for pickup'}
                {order.orderStatus === 'PICKED_UP' && 'Delivery agent is on the way'}
                {order.orderStatus === 'DELIVERED' && 'Order has been delivered'}
                {order.orderStatus === 'CANCELLED' && 'Order has been cancelled'}
              </p>
            </div>
          </div>
        </div>

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

        {/* Order Items */}
        <div className="card p-6 mb-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">Order Items</h2>
          <div className="space-y-3">
            {order.items?.map((item) => (
              <div key={item.id} className="flex justify-between items-center py-2 border-b last:border-b-0">
                <div>
                  <p className="font-medium text-gray-900">{item.menuItemName}</p>
                  <p className="text-sm text-gray-600">Quantity: {item.quantity}</p>
                </div>
                <div className="text-right">
                  <p className="font-semibold text-gray-900">₹{item.subtotal?.toFixed(2) || '0.00'}</p>
                  <p className="text-sm text-gray-600">₹{item.pricePerUnit} each</p>
                </div>
              </div>
            )) || []}
          </div>
        </div>

        {/* Payment Summary */}
        <div className="card p-6 mb-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">Payment Summary</h2>
          <div className="space-y-2">
            <div className="flex justify-between text-gray-600">
              <span>Total Amount</span>
              <span>₹{order.totalAmount?.toFixed(2) || '0.00'}</span>
            </div>
            <div className="flex justify-between items-center pt-2 border-t">
              <span className="font-semibold text-gray-900">Payment Status</span>
              <span className={`badge ${order.paymentStatus === 'COMPLETED' ? 'badge-success' :
                order.paymentStatus === 'PENDING' ? 'badge-warning' :
                  order.paymentStatus === 'FAILED' ? 'badge-error' : 'badge-info'
                }`}>
                {order.paymentStatus || 'Unknown'}
              </span>
            </div>
          </div>
          {order.paymentStatus === 'PENDING' && (
            <button
              onClick={() => navigate(`/payment/${order.id}`)}
              className="w-full btn-primary mt-4"
            >
              Complete Payment
            </button>
          )}
        </div>

        {/* Action Buttons */}
        <div className="flex flex-col sm:flex-row gap-4">
          <button
            onClick={() => navigate('/orders')}
            className="flex-1 btn-outline"
          >
            View All Orders
          </button>
          <button
            onClick={() => navigate('/restaurants')}
            className="flex-1 btn-primary"
          >
            Order Again
          </button>
        </div>
      </div>
    </div>
  );
}
