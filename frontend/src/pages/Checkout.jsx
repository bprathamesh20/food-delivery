import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { MapPin, FileText, Loader2 } from 'lucide-react';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { orderService } from '../services/api';
import toast from 'react-hot-toast';

export default function Checkout() {
  const navigate = useNavigate();
  const { cartItems, restaurant, getCartTotal, clearCart } = useCart();
  const { user } = useAuth();
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    deliveryAddress: '',
    specialInstructions: '',
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.deliveryAddress.trim()) {
      toast.error('Please enter a delivery address');
      return;
    }

    setLoading(true);
    console.log('Starting checkout process...');

    try {
      const orderData = {
        customerId: user.id,
        restaurantId: restaurant.id,
        deliveryAddress: formData.deliveryAddress,
        specialInstructions: formData.specialInstructions || null,
        items: cartItems.map((item) => ({
          menuItemId: item.id,
          quantity: item.quantity,
        })),
      };

      console.log('Creating order with data:', orderData);
      const response = await orderService.create(orderData);
      const order = response.data;
      console.log('Order created successfully:', order);

      toast.success('Order placed successfully!');
      
      console.log('Navigating to payment page...');
      
      // Navigate first using replace to avoid back button issues
      navigate(`/payment/${order.id}`, { replace: true });
      
      // Clear cart after a short delay to ensure navigation completes
      setTimeout(() => {
        console.log('Clearing cart after navigation...');
        clearCart();
      }, 500);
    } catch (error) {
      console.error('Failed to create order:', error);
      toast.error(error.response?.data?.message || 'Failed to place order. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  if (cartItems.length === 0 && !loading) {
    console.log('Redirecting to cart - cartItems empty, loading:', loading);
    navigate('/cart');
    return null;
  }

  const subtotal = getCartTotal();
  const deliveryFee = 40;
  const taxes = subtotal * 0.05;
  const total = subtotal + deliveryFee + taxes;

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-8">Checkout</h1>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Checkout Form */}
          <div className="lg:col-span-2">
            <form onSubmit={handleSubmit} className="space-y-6">
              {/* Delivery Address */}
              <div className="card p-6">
                <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                  <MapPin className="h-5 w-5 mr-2 text-orange-500" />
                  Delivery Address
                </h2>
                <textarea
                  name="deliveryAddress"
                  required
                  rows={3}
                  value={formData.deliveryAddress}
                  onChange={handleChange}
                  className="input-field resize-none"
                  placeholder="Enter your complete delivery address..."
                />
              </div>

              {/* Special Instructions */}
              <div className="card p-6">
                <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                  <FileText className="h-5 w-5 mr-2 text-orange-500" />
                  Special Instructions (Optional)
                </h2>
                <textarea
                  name="specialInstructions"
                  rows={3}
                  value={formData.specialInstructions}
                  onChange={handleChange}
                  className="input-field resize-none"
                  placeholder="Any special requests? (e.g., extra spicy, no onions, ring doorbell twice)"
                />
              </div>

              <button
                type="submit"
                disabled={loading}
                className="w-full btn-primary flex items-center justify-center"
              >
                {loading ? (
                  <>
                    <Loader2 className="animate-spin h-5 w-5 mr-2" />
                    Placing Order...
                  </>
                ) : (
                  'Place Order & Continue to Payment'
                )}
              </button>
            </form>
          </div>

          {/* Order Summary */}
          <div className="lg:col-span-1">
            <div className="card p-6 sticky top-20">
              <h2 className="text-xl font-semibold text-gray-900 mb-4">Order Summary</h2>
              
              <div className="mb-4">
                <p className="text-sm text-gray-600 mb-2">Ordering from:</p>
                <p className="font-semibold text-gray-900">{restaurant?.name}</p>
              </div>

              <div className="border-t pt-4 mb-4">
                <p className="text-sm text-gray-600 mb-3">Items ({cartItems.length})</p>
                <div className="space-y-2 mb-4">
                  {cartItems.map((item) => (
                    <div key={item.id} className="flex justify-between text-sm">
                      <span className="text-gray-700">
                        {item.name} x {item.quantity}
                      </span>
                      <span className="text-gray-900">₹{(item.price * item.quantity).toFixed(2)}</span>
                    </div>
                  ))}
                </div>
              </div>

              <div className="space-y-3 border-t pt-4">
                <div className="flex justify-between text-gray-600">
                  <span>Subtotal</span>
                  <span>₹{subtotal.toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-gray-600">
                  <span>Delivery Fee</span>
                  <span>₹{deliveryFee.toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-gray-600">
                  <span>Taxes (5%)</span>
                  <span>₹{taxes.toFixed(2)}</span>
                </div>
                <div className="border-t pt-3 flex justify-between text-lg font-bold text-gray-900">
                  <span>Total</span>
                  <span>₹{total.toFixed(2)}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
