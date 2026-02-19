import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { CreditCard, Loader2 } from 'lucide-react';
import { orderService, paymentService } from '../services/api';
import Loading from '../components/Loading';
import toast from 'react-hot-toast';

export default function Payment() {
  const { orderId } = useParams();
  const navigate = useNavigate();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [processing, setProcessing] = useState(false);

  useEffect(() => {
    loadOrder();
  }, [orderId]);

  const loadOrder = async () => {
    try {
      const response = await orderService.getById(orderId);
      setOrder(response.data);

      // If payment is already completed, redirect to order details
      if (response.data.paymentStatus === 'COMPLETED') {
        toast.success('Payment already completed');
        navigate(`/orders/${orderId}`);
      }
    } catch (error) {
      console.error('Failed to load order:', error);
      toast.error('Failed to load order details');
    } finally {
      setLoading(false);
    }
  };

  const handleRazorpayPayment = async () => {
    setProcessing(true);

    try {
      // Create Razorpay order
      const orderResponse = await paymentService.createRazorpayOrder({
        orderId: orderId,
        amount: order.totalAmount,
        paymentMethod: 'RAZORPAY',
      });

      const { razorpayOrderId, keyId, amount, currency } = orderResponse.data;

      // Razorpay options
      const options = {
        key: keyId,
        amount: amount * 100, // Convert to paise
        currency: currency,
        name: 'FoodDelivery',
        description: `Payment for Order #${orderId}`,
        order_id: razorpayOrderId,
        handler: async function (response) {
          try {
            // Verify payment
            const verifyResponse = await paymentService.verifyRazorpayPayment({
              orderId: orderId,
              razorpayOrderId: response.razorpay_order_id,
              razorpayPaymentId: response.razorpay_payment_id,
              razorpaySignature: response.razorpay_signature,
            });

            if (verifyResponse.data.verified) {
              toast.success('Payment successful!');
              navigate(`/orders/${orderId}`);
            } else {
              toast.error('Payment verification failed');
            }
          } catch (error) {
            console.error('Payment verification failed:', error);
            toast.error('Payment verification failed');
          } finally {
            setProcessing(false);
          }
        },
        prefill: {
          name: order.customerName || '',
          email: '',
          contact: '',
        },
        theme: {
          color: '#f97316',
        },
        modal: {
          ondismiss: function () {
            setProcessing(false);
            toast.error('Payment cancelled');
          },
        },
      };

      const razorpay = new window.Razorpay(options);
      razorpay.open();
    } catch (error) {
      console.error('Failed to initiate payment:', error);
      toast.error('Failed to initiate payment');
      setProcessing(false);
    }
  };

  if (loading) {
    return <Loading fullScreen message="Loading payment details..." />;
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

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-8">Complete Payment</h1>

        {/* Order Summary */}
        <div className="card p-6 mb-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">Order Summary</h2>
          <div className="space-y-3 mb-4">
            <div className="flex justify-between text-gray-600">
              <span>Order ID</span>
              <span>#{order.id}</span>
            </div>
            <div className="flex justify-between text-gray-600">
              <span>Items</span>
              <span>{order.items.length} item{order.items.length !== 1 ? 's' : ''}</span>
            </div>
            <div className="border-t pt-3 flex justify-between text-xl font-bold text-gray-900">
              <span>Total Amount</span>
              <span>₹{order.totalAmount.toFixed(2)}</span>
            </div>
          </div>
        </div>

        {/* Payment Method */}
        <div className="card p-6 mb-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">Payment Method</h2>
          <div className="space-y-4">
            <button
              onClick={handleRazorpayPayment}
              disabled={processing}
              className="w-full card p-4 hover:shadow-lg transition-shadow flex items-center justify-between"
            >
              <div className="flex items-center space-x-3">
                <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                  <CreditCard className="h-6 w-6 text-blue-500" />
                </div>
                <div className="text-left">
                  <p className="font-semibold text-gray-900">Razorpay</p>
                  <p className="text-sm text-gray-600">Credit/Debit Card, UPI, Net Banking</p>
                </div>
              </div>
              {processing ? (
                <Loader2 className="h-6 w-6 text-orange-500 animate-spin" />
              ) : (
                <span className="text-orange-500 font-semibold">Pay Now →</span>
              )}
            </button>
          </div>
        </div>

        {/* Payment Info */}
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
          <p className="text-sm text-blue-800">
            <strong>Secure Payment:</strong> Your payment information is encrypted and secure. We use Razorpay for processing payments.
          </p>
        </div>

        {/* Back Button */}
        <button onClick={() => navigate(`/orders/${orderId}`)} className="w-full btn-outline">
          Back to Order Details
        </button>
      </div>
    </div>
  );
}
