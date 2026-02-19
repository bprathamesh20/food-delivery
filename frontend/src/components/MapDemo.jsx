import { useState } from 'react';
import DeliveryMap from './DeliveryMap';
import { MapPin, Navigation, RefreshCw } from 'lucide-react';

export default function MapDemo() {
  const [demoData, setDemoData] = useState({
    delivery: {
      id: 1,
      pickupLatitude: 28.6139,
      pickupLongitude: 77.2090,
      pickupAddress: "Connaught Place, New Delhi",
      deliveryLatitude: 28.5355,
      deliveryLongitude: 77.3910,
      deliveryAddress: "Sector 18, Noida",
      deliveryAgent: {
        name: "Rajesh Kumar",
        vehicleType: "BIKE",
        vehicleNumber: "DL-01-AB-1234",
        phoneNumber: "+91-9876543210",
        rating: 4.8,
        totalDeliveries: 150
      }
    },
    agentLocation: {
      latitude: 28.5755,
      longitude: 77.3200,
      timestamp: new Date().toISOString()
    }
  });

  const moveAgent = () => {
    // Simulate agent movement
    const newLat = demoData.agentLocation.latitude + (Math.random() - 0.5) * 0.01;
    const newLng = demoData.agentLocation.longitude + (Math.random() - 0.5) * 0.01;
    
    setDemoData(prev => ({
      ...prev,
      agentLocation: {
        latitude: newLat,
        longitude: newLng,
        timestamp: new Date().toISOString()
      }
    }));
  };

  const resetDemo = () => {
    setDemoData(prev => ({
      ...prev,
      agentLocation: {
        latitude: 28.5755,
        longitude: 77.3200,
        timestamp: new Date().toISOString()
      }
    }));
  };

  return (
    <div className="max-w-4xl mx-auto p-6">
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-gray-900 mb-2">Delivery Tracking Map Demo</h2>
        <p className="text-gray-600">
          This demo shows how the delivery tracking map works with live agent location updates.
        </p>
      </div>

      {/* Demo Controls */}
      <div className="card p-4 mb-6">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-4">
            <div className="flex items-center space-x-2">
              <div className="w-3 h-3 bg-orange-500 rounded-full"></div>
              <span className="text-sm text-gray-600">Agent Location</span>
            </div>
            <div className="flex items-center space-x-2">
              <div className="w-3 h-3 bg-green-500 rounded-full"></div>
              <span className="text-sm text-gray-600">Pickup (Restaurant)</span>
            </div>
            <div className="flex items-center space-x-2">
              <div className="w-3 h-3 bg-blue-500 rounded-full"></div>
              <span className="text-sm text-gray-600">Delivery Address</span>
            </div>
          </div>
          <div className="flex items-center space-x-2">
            <button
              onClick={moveAgent}
              className="flex items-center space-x-2 px-3 py-2 text-sm bg-orange-100 hover:bg-orange-200 text-orange-700 rounded-lg transition-colors"
            >
              <Navigation className="h-4 w-4" />
              <span>Move Agent</span>
            </button>
            <button
              onClick={resetDemo}
              className="flex items-center space-x-2 px-3 py-2 text-sm bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg transition-colors"
            >
              <RefreshCw className="h-4 w-4" />
              <span>Reset</span>
            </button>
          </div>
        </div>
      </div>

      {/* Map */}
      <div className="card p-6">
        <DeliveryMap
          delivery={demoData.delivery}
          agentLocation={demoData.agentLocation}
          className="h-96 w-full rounded-lg border border-gray-200"
          showRoute={true}
          autoFit={true}
        />
      </div>

      {/* Agent Info */}
      <div className="card p-6 mt-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Delivery Agent Information</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <p className="text-sm text-gray-600">Name</p>
            <p className="font-medium text-gray-900">{demoData.delivery.deliveryAgent.name}</p>
          </div>
          <div>
            <p className="text-sm text-gray-600">Vehicle</p>
            <p className="font-medium text-gray-900">
              {demoData.delivery.deliveryAgent.vehicleType} - {demoData.delivery.deliveryAgent.vehicleNumber}
            </p>
          </div>
          <div>
            <p className="text-sm text-gray-600">Rating</p>
            <p className="font-medium text-gray-900">
              ⭐ {demoData.delivery.deliveryAgent.rating} ({demoData.delivery.deliveryAgent.totalDeliveries} deliveries)
            </p>
          </div>
          <div>
            <p className="text-sm text-gray-600">Current Location</p>
            <p className="font-medium text-gray-900">
              {demoData.agentLocation.latitude.toFixed(4)}, {demoData.agentLocation.longitude.toFixed(4)}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}