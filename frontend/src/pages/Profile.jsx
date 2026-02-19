import { useNavigate } from 'react-router-dom';
import { User, Mail, Phone, LogOut, ShoppingBag } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export default function Profile() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-8">My Profile</h1>

        {/* Profile Card */}
        <div className="card p-6 mb-6">
          <div className="flex items-center space-x-4 mb-6">
            <div className="w-20 h-20 bg-orange-100 rounded-full flex items-center justify-center">
              <User className="h-10 w-10 text-orange-500" />
            </div>
            <div>
              <h2 className="text-2xl font-bold text-gray-900">{user?.fullName}</h2>
              <p className="text-gray-600">Customer</p>
            </div>
          </div>

          <div className="space-y-4">
            <div className="flex items-center space-x-3 text-gray-700">
              <Mail className="h-5 w-5 text-gray-400" />
              <div>
                <p className="text-sm text-gray-600">Email</p>
                <p className="font-medium">{user?.email}</p>
              </div>
            </div>

            <div className="flex items-center space-x-3 text-gray-700">
              <Phone className="h-5 w-5 text-gray-400" />
              <div>
                <p className="text-sm text-gray-600">Phone</p>
                <p className="font-medium">{user?.phone}</p>
              </div>
            </div>

            <div className="flex items-center space-x-3 text-gray-700">
              <User className="h-5 w-5 text-gray-400" />
              <div>
                <p className="text-sm text-gray-600">User ID</p>
                <p className="font-medium">#{user?.id}</p>
              </div>
            </div>
          </div>
        </div>

        {/* Quick Actions */}
        <div className="card p-6 mb-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">Quick Actions</h2>
          <div className="space-y-3">
            <button
              onClick={() => navigate('/orders')}
              className="w-full flex items-center justify-between p-4 hover:bg-gray-50 rounded-lg transition-colors"
            >
              <div className="flex items-center space-x-3">
                <ShoppingBag className="h-5 w-5 text-orange-500" />
                <span className="font-medium text-gray-900">My Orders</span>
              </div>
              <span className="text-gray-400">→</span>
            </button>

            <button
              onClick={() => navigate('/restaurants')}
              className="w-full flex items-center justify-between p-4 hover:bg-gray-50 rounded-lg transition-colors"
            >
              <div className="flex items-center space-x-3">
                <span className="text-xl">🍽️</span>
                <span className="font-medium text-gray-900">Browse Restaurants</span>
              </div>
              <span className="text-gray-400">→</span>
            </button>
          </div>
        </div>

        {/* Account Settings */}
        <div className="card p-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">Account Settings</h2>
          <button
            onClick={handleLogout}
            className="w-full flex items-center justify-center space-x-2 p-4 bg-red-50 hover:bg-red-100 text-red-600 rounded-lg transition-colors font-semibold"
          >
            <LogOut className="h-5 w-5" />
            <span>Logout</span>
          </button>
        </div>
      </div>
    </div>
  );
}
