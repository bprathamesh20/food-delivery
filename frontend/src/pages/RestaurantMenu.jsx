import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { MapPin, Clock, Star, Plus, ShoppingCart } from 'lucide-react';
import { restaurantService, menuService } from '../services/api';
import { useCart } from '../context/CartContext';
import Loading from '../components/Loading';
import toast from 'react-hot-toast';

export default function RestaurantMenu() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [restaurant, setRestaurant] = useState(null);
  const [menuItems, setMenuItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const { addToCart, cartItems, getCartCount } = useCart();

  useEffect(() => {
    loadRestaurantAndMenu();
  }, [id]);

  const loadRestaurantAndMenu = async () => {
    try {
      const [restaurantRes, menuRes] = await Promise.all([
        restaurantService.getById(id),
        menuService.getByRestaurant(id),
      ]);
      setRestaurant(restaurantRes.data);
      setMenuItems(menuRes.data);
    } catch (error) {
      console.error('Failed to load restaurant or menu:', error);
      toast.error('Failed to load restaurant details');
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = (item) => {
    if (!item.available) {
      toast.error('This item is currently unavailable');
      return;
    }
    addToCart(item, { id: restaurant.id, name: restaurant.name });
  };

  const getItemQuantityInCart = (itemId) => {
    const cartItem = cartItems.find((item) => item.id === itemId);
    return cartItem ? cartItem.quantity : 0;
  };

  if (loading) {
    return <Loading fullScreen message="Loading menu..." />;
  }

  if (!restaurant) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <p className="text-gray-500 text-lg">Restaurant not found</p>
          <button onClick={() => navigate('/restaurants')} className="btn-primary mt-4">
            Back to Restaurants
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Restaurant Header */}
      <div className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="flex items-start justify-between">
            <div className="flex-1">
              <h1 className="text-3xl font-bold text-gray-900 mb-2">{restaurant.name}</h1>
              <div className="flex items-center text-gray-600 mb-3">
                <MapPin className="h-5 w-5 mr-2" />
                <span>{restaurant.address || 'Address not available'}</span>
              </div>
              <div className="flex items-center space-x-6 text-sm">
                <div className="flex items-center">
                  <Star className="h-5 w-5 text-yellow-400 mr-1" />
                  <span className="font-semibold">4.5</span>
                </div>
                <div className="flex items-center text-gray-600">
                  <Clock className="h-5 w-5 mr-1" />
                  <span>25-35 min</span>
                </div>
                <span className={`badge ${restaurant.active ? 'badge-success' : 'badge-error'}`}>
                  {restaurant.active ? 'Open Now' : 'Closed'}
                </span>
              </div>
            </div>
            {getCartCount() > 0 && (
              <button
                onClick={() => navigate('/cart')}
                className="btn-primary flex items-center space-x-2"
              >
                <ShoppingCart className="h-5 w-5" />
                <span>View Cart ({getCartCount()})</span>
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Menu Items */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <h2 className="text-2xl font-bold text-gray-900 mb-6">Menu</h2>

        {menuItems.length === 0 ? (
          <div className="text-center py-12 text-gray-500">
            No menu items available for this restaurant.
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {menuItems.map((item) => {
              const quantityInCart = getItemQuantityInCart(item.id);
              return (
                <div key={item.id} className="card overflow-hidden">
                  <div className="h-40 bg-gradient-to-br from-orange-300 to-orange-500 flex items-center justify-center">
                    <span className="text-5xl">🍔</span>
                  </div>
                  <div className="p-4">
                    <div className="flex justify-between items-start mb-2">
                      <h3 className="text-lg font-semibold text-gray-900">{item.name}</h3>
                      <span className={`badge ${item.available ? 'badge-success' : 'badge-error'}`}>
                        {item.available ? 'Available' : 'Unavailable'}
                      </span>
                    </div>
                    <p className="text-xl font-bold text-orange-500 mb-3">₹{item.price}</p>
                    <button
                      onClick={() => handleAddToCart(item)}
                      disabled={!item.available}
                      className="w-full btn-primary flex items-center justify-center space-x-2"
                    >
                      <Plus className="h-5 w-5" />
                      <span>Add to Cart</span>
                      {quantityInCart > 0 && (
                        <span className="bg-white text-orange-500 px-2 py-0.5 rounded-full text-sm font-semibold">
                          {quantityInCart}
                        </span>
                      )}
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}
