import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Package, CheckCircle, Clock, Truck, MapPin, Phone, LogOut, User, Navigation, RefreshCw } from 'lucide-react';
import { agentService, deliveryService } from '../../services/api';
import Loading from '../../components/Loading';
import toast from 'react-hot-toast';

const statusColors = {
  PENDING: 'bg-yellow-100 text-yellow-800',
  ASSIGNED: 'bg-blue-100 text-blue-800',
  PICKED_UP: 'bg-purple-100 text-purple-800',
  IN_TRANSIT: 'bg-orange-100 text-orange-800',
  DELIVERED: 'bg-green-100 text-green-800',
  CANCELLED: 'bg-red-100 text-red-800',
};

export default function AgentDashboard() {
  const [agent, setAgent] = useState(null);
  const [deliveries, setDeliveries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [updatingStatus, setUpdatingStatus] = useState(null);
  const [locationTracking, setLocationTracking] = useState(false);
  const [currentLocation, setCurrentLocation] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const agentToken = localStorage.getItem('agentToken');
    if (!agentToken) {
      navigate('/agent/login');
      return;
    }
    loadAgentData();
  }, []);

  const loadAgentData = async () => {
    try {
      const agentRes = await agentService.getProfile();
      setAgent(agentRes.data);

      const deliveriesRes = await agentService.getAgentDeliveries(agentRes.data.id);
      setDeliveries(deliveriesRes.data);
    } catch (error) {
      console.error('Failed to load agent data:', error);
      if (error.response?.status === 401) {
        localStorage.removeItem('agentToken');
        localStorage.removeItem('agent');
        navigate('/agent/login');
      } else {
        toast.error('Failed to load data');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleStatusChange = async (deliveryId, newStatus) => {
    setUpdatingStatus(deliveryId);
    try {
      await deliveryService.updateStatus(deliveryId, newStatus);
      toast.success(`Status updated to ${newStatus}`);
      loadAgentData();
    } catch (error) {
      console.error('Failed to update status:', error);
      toast.error('Failed to update status');
    } finally {
      setUpdatingStatus(null);
    }
  };

  const handleStatusToggle = async () => {
    try {
      const newStatus = agent.status === 'AVAILABLE' ? 'OFFLINE' : 'AVAILABLE';
      await agentService.updateStatus(newStatus);
      setAgent({ ...agent, status: newStatus });
      toast.success(`Status changed to ${newStatus}`);
    } catch (error) {
      console.error('Failed to update status:', error);
      toast.error('Failed to update status');
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('agentToken');
    localStorage.removeItem('agent');
    toast.success('Logged out successfully');
    navigate('/agent/login');
  };

  const getNextStatus = (currentStatus) => {
    const statusFlow = {
      ASSIGNED: 'PICKED_UP',
      PICKED_UP: 'IN_TRANSIT',
      IN_TRANSIT: 'DELIVERED',
    };
    return statusFlow[currentStatus];
  };

  // Location tracking functions
  const startLocationTracking = () => {
    if (!navigator.geolocation) {
      toast.error('Geolocation is not supported by this browser');
      return;
    }

    setLocationTracking(true);
    
    const watchId = navigator.geolocation.watchPosition(
      (position) => {
        const { latitude, longitude } = position.coords;
        setCurrentLocation({ latitude, longitude });
        updateAgentLocation(latitude, longitude);
      },
      (error) => {
        console.error('Geolocation error:', error);
        toast.error('Failed to get location');
        setLocationTracking(false);
      },
      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 30000
      }
    );

    // Store watchId to clear later
    window.locationWatchId = watchId;
  };

  const stopLocationTracking = () => {
    if (window.locationWatchId) {
      navigator.geolocation.clearWatch(window.locationWatchId);
      window.locationWatchId = null;
    }
    setLocationTracking(false);
    toast.success('Location tracking stopped');
  };

  const updateAgentLocation = async (latitude, longitude) => {
    try {
      await agentService.updateLocation(latitude, longitude);
      
      // Also update location for active deliveries
      const activeDelivery = activeDeliveries[0]; // Update for the first active delivery
      if (activeDelivery) {
        await deliveryService.updateLocation(
          activeDelivery.id, 
          latitude, 
          longitude, 
          'Agent location update'
        );
      }
    } catch (error) {
      console.error('Failed to update location:', error);
    }
  };

  const getCurrentLocationOnce = () => {
    if (!navigator.geolocation) {
      toast.error('Geolocation is not supported by this browser');
      return;
    }

    navigator.geolocation.getCurrentPosition(
      (position) => {
        const { latitude, longitude } = position.coords;
        setCurrentLocation({ latitude, longitude });
        updateAgentLocation(latitude, longitude);
        toast.success('Location updated');
      },
      (error) => {
        console.error('Geolocation error:', error);
        toast.error('Failed to get location');
      },
      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 60000
      }
    );
  };

  if (loading) {
    return <Loading fullScreen message="Loading dashboard..." />;
  }

  const activeDeliveries = deliveries.filter(d => 
    ['ASSIGNED', 'PICKED_UP', 'IN_TRANSIT'].includes(d.status)
  );
  const completedDeliveries = deliveries.filter(d => d.status === 'DELIVERED');

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-blue-600 text-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-4">
              <div className="w-12 h-12 bg-white rounded-full flex items-center justify-center">
                <User className="h-6 w-6 text-blue-600" />
              </div>
              <div>
                <h1 className="text-2xl font-bold">{agent?.name}</h1>
                <p className="text-blue-100">{agent?.vehicleType} - {agent?.vehicleNumber}</p>
              </div>
            </div>
            <button
              onClick={handleLogout}
              className="flex items-center space-x-2 bg-blue-700 hover:bg-blue-800 px-4 py-2 rounded-lg transition-colors"
            >
              <LogOut className="h-5 w-5" />
              <span>Logout</span>
            </button>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Status Card */}
        <div className="card p-6 mb-8">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h2 className="text-xl font-semibold text-gray-900 mb-2">Your Status</h2>
              <span className={`inline-flex items-center px-3 py-1 rounded-full text-sm font-medium ${
                agent?.status === 'AVAILABLE' ? 'bg-green-100 text-green-800' :
                agent?.status === 'BUSY' ? 'bg-orange-100 text-orange-800' :
                'bg-gray-100 text-gray-800'
              }`}>
                {agent?.status}
              </span>
            </div>
            <button
              onClick={handleStatusToggle}
              className={`px-6 py-2 rounded-lg font-semibold transition-colors ${
                agent?.status === 'AVAILABLE'
                  ? 'bg-gray-200 hover:bg-gray-300 text-gray-800'
                  : 'bg-green-500 hover:bg-green-600 text-white'
              }`}
            >
              {agent?.status === 'AVAILABLE' ? 'Go Offline' : 'Go Online'}
            </button>
          </div>
          
          {/* Location Tracking Controls */}
          <div className="border-t pt-4">
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-3">
                <Navigation className="h-5 w-5 text-gray-500" />
                <div>
                  <p className="font-medium text-gray-900">Location Tracking</p>
                  <p className="text-sm text-gray-500">
                    {locationTracking ? 'Live tracking enabled' : 'Enable to share location with customers'}
                  </p>
                  {currentLocation && (
                    <p className="text-xs text-gray-400 mt-1">
                      Last update: {new Date().toLocaleTimeString()}
                    </p>
                  )}
                </div>
              </div>
              <div className="flex items-center space-x-2">
                <button
                  onClick={getCurrentLocationOnce}
                  className="flex items-center space-x-2 px-3 py-2 text-sm bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg transition-colors"
                  disabled={locationTracking}
                >
                  <RefreshCw className="h-4 w-4" />
                  <span>Update</span>
                </button>
                <button
                  onClick={locationTracking ? stopLocationTracking : startLocationTracking}
                  className={`flex items-center space-x-2 px-4 py-2 text-sm rounded-lg font-medium transition-colors ${
                    locationTracking
                      ? 'bg-red-100 hover:bg-red-200 text-red-700'
                      : 'bg-blue-100 hover:bg-blue-200 text-blue-700'
                  }`}
                >
                  {locationTracking ? (
                    <>
                      <div className="w-2 h-2 bg-red-500 rounded-full animate-pulse"></div>
                      <span>Stop Tracking</span>
                    </>
                  ) : (
                    <>
                      <Navigation className="h-4 w-4" />
                      <span>Start Tracking</span>
                    </>
                  )}
                </button>
              </div>
            </div>
          </div>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <div className="card p-6">
            <div className="flex items-center space-x-3">
              <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                <Package className="h-6 w-6 text-blue-600" />
              </div>
              <div>
                <p className="text-sm text-gray-600">Active Deliveries</p>
                <p className="text-2xl font-bold text-gray-900">{activeDeliveries.length}</p>
              </div>
            </div>
          </div>

          <div className="card p-6">
            <div className="flex items-center space-x-3">
              <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
                <CheckCircle className="h-6 w-6 text-green-600" />
              </div>
              <div>
                <p className="text-sm text-gray-600">Completed Today</p>
                <p className="text-2xl font-bold text-gray-900">{completedDeliveries.length}</p>
              </div>
            </div>
          </div>

          <div className="card p-6">
            <div className="flex items-center space-x-3">
              <div className="w-12 h-12 bg-orange-100 rounded-lg flex items-center justify-center">
                <Truck className="h-6 w-6 text-orange-600" />
              </div>
              <div>
                <p className="text-sm text-gray-600">Total Deliveries</p>
                <p className="text-2xl font-bold text-gray-900">{deliveries.length}</p>
              </div>
            </div>
          </div>
        </div>

        {/* Active Deliveries */}
        <div className="mb-8">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">Active Deliveries</h2>
          {activeDeliveries.length === 0 ? (
            <div className="card p-12 text-center">
              <Clock className="h-16 w-16 text-gray-300 mx-auto mb-4" />
              <p className="text-gray-500 text-lg">No active deliveries</p>
              <p className="text-gray-400 mt-2">New deliveries will appear here</p>
            </div>
          ) : (
            <div className="space-y-4">
              {activeDeliveries.map((delivery) => (
                <div key={delivery.id} className="card p-6">
                  <div className="flex items-start justify-between mb-4">
                    <div>
                      <div className="flex items-center space-x-3 mb-2">
                        <h3 className="text-lg font-semibold text-gray-900">
                          Order #{delivery.orderId}
                        </h3>
                        <span className={`px-3 py-1 rounded-full text-sm font-medium ${statusColors[delivery.status]}`}>
                          {delivery.status.replace('_', ' ')}
                        </span>
                      </div>
                      <p className="text-sm text-gray-600">Delivery ID: #{delivery.id}</p>
                    </div>
                    <p className="text-xl font-bold text-green-600">₹{delivery.deliveryFee}</p>
                  </div>

                  <div className="space-y-3 mb-4">
                    <div className="flex items-start space-x-3">
                      <MapPin className="h-5 w-5 text-orange-500 mt-0.5 flex-shrink-0" />
                      <div>
                        <p className="text-sm font-medium text-gray-700">Pickup</p>
                        <p className="text-sm text-gray-600">{delivery.pickupAddress}</p>
                      </div>
                    </div>
                    <div className="flex items-start space-x-3">
                      <MapPin className="h-5 w-5 text-green-500 mt-0.5 flex-shrink-0" />
                      <div>
                        <p className="text-sm font-medium text-gray-700">Delivery</p>
                        <p className="text-sm text-gray-600">{delivery.deliveryAddress}</p>
                      </div>
                    </div>
                    {delivery.deliveryInstructions && (
                      <div className="bg-blue-50 p-3 rounded-lg">
                        <p className="text-sm text-blue-800">
                          <strong>Instructions:</strong> {delivery.deliveryInstructions}
                        </p>
                      </div>
                    )}
                  </div>

                  {getNextStatus(delivery.status) && (
                    <button
                      onClick={() => handleStatusChange(delivery.id, getNextStatus(delivery.status))}
                      disabled={updatingStatus === delivery.id}
                      className="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-4 rounded-lg transition-colors disabled:opacity-50"
                    >
                      {updatingStatus === delivery.id ? 'Updating...' : `Mark as ${getNextStatus(delivery.status).replace('_', ' ')}`}
                    </button>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Completed Deliveries */}
        {completedDeliveries.length > 0 && (
          <div>
            <h2 className="text-2xl font-bold text-gray-900 mb-4">Completed Deliveries</h2>
            <div className="space-y-4">
              {completedDeliveries.map((delivery) => (
                <div key={delivery.id} className="card p-6 opacity-75">
                  <div className="flex items-center justify-between">
                    <div>
                      <h3 className="text-lg font-semibold text-gray-900">Order #{delivery.orderId}</h3>
                      <p className="text-sm text-gray-600">{delivery.deliveryAddress}</p>
                    </div>
                    <div className="text-right">
                      <span className="px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800">
                        DELIVERED
                      </span>
                      <p className="text-lg font-bold text-green-600 mt-2">₹{delivery.deliveryFee}</p>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
