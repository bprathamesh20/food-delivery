import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Clock, MapPin, Package } from 'lucide-react';
import { orderService } from '../services/api';
import { useAuth } from '../context/AuthContext';
import Loading from '../components/Loading';
import toast from 'react-hot-toast';

export default function OrderHistory() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const { user } = useAuth();

  useEffect(() => {
    loadOrders();
  }, []);

  const loadOrders = async () => {
    try {
      const response = await orderService.getByCustomer(user.id);
      // Sort by most recent first
      const sortedOrders = response.data.sort((a, b) => 
        new Date(b.createdAt) - new Date(a.createdAt)
      );
      setOrders(sortedOrders);
    } catch (error) {
      console.error('Failed to load orders:', error);
      toast.error('Failed to load order history');
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadgeClass = (status) => {
    switch (status) {
      case 'DELIVERED':
        return 'badge-success';
      case 'CANCELLED':
        return 'badge-error';
      case 'PENDING':
      case 'CONFIRMED':
        return 'badge-warning';
      default:
        return 'badge-info';
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-IN', {
      day: 'numeric',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  if (loading) {
    return <Loading fullScreen message="Loading your orders..." />;
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-8">My Orders</h1>

        {orders.length === 0 ? (
          <div className="text-center py-12">
            <Package className="h-24 w-24 text-gray-300 mx-auto mb-4" />
            <h2 className="text-2xl font-bold text-gray-900 mb-2">No orders yet</h2>
            <p className="text-gray-600 mb-6">Start ordering delicious food from your favorite restaurants!</p>
            <Link to="/restaurants" className="btn-primary">
              Browse Restaurants
            </Link>
          </div>
        ) : (
          <div className="space-y-4">
            {orders.map((order) => (
              <Link
                key={order.id}
                to={`/orders/${order.id}`}
                className="card p-6 hover:shadow-lg transition-shadow block"
              >
                <div className="flex flex-col md:flex-row md:items-center md:justify-between">
                  <div className="flex-1 mb-4 md:mb-0">
                    <div className="flex items-center space-x-3 mb-2">
                      <h3 className="text-lg font-semibold text-gray-900">
                        Order #{order.id}
                      </h3>
                      <span className={`badge ${getStatusBadgeClass(order.orderStatus)}`}>
                        {order.orderStatus.replace('_', ' ')}
                      </span>
                    </div>
                    <div className="flex items-center text-gray-600 text-sm mb-2">
                      <Clock className="h-4 w-4 mr-1" />
                      <span>{formatDate(order.createdAt)}</span>
                    </div>
                    <div className="flex items-start text-gray-600 text-sm">
                      <MapPin className="h-4 w-4 mr-1 mt-0.5 flex-shrink-0" />
                      <span className="line-clamp-1">{order.deliveryAddress}</span>
                    </div>
                  </div>

                  <div className="flex flex-col md:items-end space-y-2">
                    <div className="text-2xl font-bold text-orange-500">
                      ₹{order.totalAmount.toFixed(2)}
                    </div>
                    <div className="text-sm text-gray-600">
                      {order.items.length} item{order.items.length !== 1 ? 's' : ''}
                    </div>
                    <div className={`badge ${
                      order.paymentStatus === 'COMPLETED' ? 'badge-success' :
                      order.paymentStatus === 'PENDING' ? 'badge-warning' :
                      'badge-error'
                    }`}>
                      Payment: {order.paymentStatus}
                    </div>
                  </div>
                </div>

                {/* Order Items Preview */}
                <div className="mt-4 pt-4 border-t">
                  <p className="text-sm text-gray-600 mb-2">Items:</p>
                  <div className="flex flex-wrap gap-2">
                    {order.items.slice(0, 3).map((item) => (
                      <span key={item.id} className="text-sm bg-gray-100 px-3 py-1 rounded-full text-gray-700">
                        {item.menuItemName} x{item.quantity}
                      </span>
                    ))}
                    {order.items.length > 3 && (
                      <span className="text-sm bg-gray-100 px-3 py-1 rounded-full text-gray-700">
                        +{order.items.length - 3} more
                      </span>
                    )}
                  </div>
                </div>

                {/* Action Buttons */}
                <div className="mt-4 flex space-x-3">
                  {order.orderStatus !== 'DELIVERED' && order.orderStatus !== 'CANCELLED' && (
                    <button
                      onClick={(e) => {
                        e.preventDefault();
                        e.stopPropagation();
                        window.location.href = `/track/${order.id}`;
                      }}
                      className="text-orange-500 hover:text-orange-600 font-semibold text-sm bg-transparent border-none cursor-pointer"
                    >
                      Track Order →
                    </button>
                  )}
                  {order.paymentStatus === 'PENDING' && (
                    <button
                      onClick={(e) => {
                        e.preventDefault();
                        e.stopPropagation();
                        window.location.href = `/payment/${order.id}`;
                      }}
                      className="text-blue-500 hover:text-blue-600 font-semibold text-sm bg-transparent border-none cursor-pointer"
                    >
                      Complete Payment →
                    </button>
                  )}
                </div>
              </Link>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
