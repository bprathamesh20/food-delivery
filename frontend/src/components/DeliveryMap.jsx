import { useEffect, useRef, useState } from 'react';
import { MapContainer, TileLayer, Marker, Popup, Polyline, useMap } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

// Fix for default markers in react-leaflet
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
});

// Custom icons
const createCustomIcon = (color, symbol) => {
  return L.divIcon({
    className: 'custom-div-icon',
    html: `
      <div style="
        background-color: ${color};
        width: 30px;
        height: 30px;
        border-radius: 50%;
        border: 3px solid white;
        box-shadow: 0 2px 8px rgba(0,0,0,0.3);
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 14px;
        color: white;
        font-weight: bold;
      ">
        ${symbol}
      </div>
    `,
    iconSize: [30, 30],
    iconAnchor: [15, 15],
  });
};

const agentIcon = createCustomIcon('#f97316', '🏍️');
const pickupIcon = createCustomIcon('#10b981', '🏪');
const deliveryIcon = createCustomIcon('#3b82f6', '🏠');

// Component to fit map bounds
function MapBounds({ positions }) {
  const map = useMap();
  
  useEffect(() => {
    if (positions && positions.length > 0) {
      const validPositions = positions.filter(pos => pos && pos.length === 2);
      if (validPositions.length > 0) {
        const bounds = L.latLngBounds(validPositions);
        map.fitBounds(bounds, { padding: [20, 20] });
      }
    }
  }, [positions, map]);
  
  return null;
}

export default function DeliveryMap({ 
  delivery, 
  agentLocation, 
  className = "h-96 w-full rounded-lg",
  showRoute = true,
  autoFit = true 
}) {
  const [route, setRoute] = useState([]);
  const mapRef = useRef();

  // Default center (you can adjust based on your service area)
  const defaultCenter = [28.6139, 77.2090]; // Delhi coordinates

  // Prepare positions for map
  const positions = [];
  const markers = [];

  // Add pickup location
  if (delivery?.pickupLatitude && delivery?.pickupLongitude) {
    const pickupPos = [delivery.pickupLatitude, delivery.pickupLongitude];
    positions.push(pickupPos);
    markers.push({
      position: pickupPos,
      icon: pickupIcon,
      popup: {
        title: "Pickup Location",
        content: delivery.pickupAddress || "Restaurant"
      }
    });
  }

  // Add delivery location
  if (delivery?.deliveryLatitude && delivery?.deliveryLongitude) {
    const deliveryPos = [delivery.deliveryLatitude, delivery.deliveryLongitude];
    positions.push(deliveryPos);
    markers.push({
      position: deliveryPos,
      icon: deliveryIcon,
      popup: {
        title: "Delivery Location",
        content: delivery.deliveryAddress || "Customer Address"
      }
    });
  }

  // Add agent location
  if (agentLocation?.latitude && agentLocation?.longitude) {
    const agentPos = [agentLocation.latitude, agentLocation.longitude];
    positions.push(agentPos);
    markers.push({
      position: agentPos,
      icon: agentIcon,
      popup: {
        title: "Delivery Agent",
        content: `${delivery?.deliveryAgent?.name || 'Agent'} - ${delivery?.deliveryAgent?.vehicleType || 'Vehicle'}`
      }
    });
  }

  // Create route line
  useEffect(() => {
    if (showRoute && positions.length >= 2) {
      // Simple route - in production, you'd use a routing service
      setRoute(positions);
    }
  }, [positions, showRoute]);

  // Map center
  const mapCenter = positions.length > 0 ? positions[0] : defaultCenter;

  return (
    <div className={className}>
      <MapContainer
        center={mapCenter}
        zoom={13}
        style={{ height: '100%', width: '100%' }}
        ref={mapRef}
      >
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        
        {/* Auto-fit bounds */}
        {autoFit && <MapBounds positions={positions} />}
        
        {/* Markers */}
        {markers.map((marker, index) => (
          <Marker
            key={index}
            position={marker.position}
            icon={marker.icon}
          >
            <Popup>
              <div className="text-center">
                <h3 className="font-semibold text-gray-900">{marker.popup.title}</h3>
                <p className="text-sm text-gray-600 mt-1">{marker.popup.content}</p>
              </div>
            </Popup>
          </Marker>
        ))}
        
        {/* Route line */}
        {showRoute && route.length >= 2 && (
          <Polyline
            positions={route}
            color="#f97316"
            weight={4}
            opacity={0.7}
            dashArray="10, 10"
          />
        )}
      </MapContainer>
    </div>
  );
}