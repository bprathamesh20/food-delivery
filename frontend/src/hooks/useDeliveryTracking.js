import { useState, useEffect, useCallback } from 'react';
import { deliveryService } from '../services/api';

export function useDeliveryTracking(deliveryId, options = {}) {
  const {
    pollInterval = 15000, // 15 seconds
    autoStart = true,
    onLocationUpdate,
    onStatusUpdate,
    onError
  } = options;

  const [tracking, setTracking] = useState(null);
  const [agentLocation, setAgentLocation] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isPolling, setIsPolling] = useState(false);

  const fetchTrackingData = useCallback(async () => {
    if (!deliveryId) return;

    try {
      setError(null);
      
      // Fetch delivery tracking data
      const trackingRes = await deliveryService.getTracking(deliveryId);
      const trackingData = trackingRes.data;
      
      setTracking(trackingData);
      
      // Extract latest agent location from tracking data
      if (trackingData && trackingData.length > 0) {
        const latestLocation = trackingData[trackingData.length - 1];
        const newLocation = {
          latitude: latestLocation.latitude,
          longitude: latestLocation.longitude,
          timestamp: latestLocation.timestamp,
          status: latestLocation.statusUpdate
        };
        
        setAgentLocation(newLocation);
        
        // Call callbacks
        if (onLocationUpdate) {
          onLocationUpdate(newLocation);
        }
        
        if (onStatusUpdate) {
          onStatusUpdate(latestLocation.statusUpdate);
        }
      }
      
    } catch (err) {
      console.error('Failed to fetch tracking data:', err);
      setError(err);
      if (onError) {
        onError(err);
      }
    } finally {
      setLoading(false);
    }
  }, [deliveryId, onLocationUpdate, onStatusUpdate, onError]);

  const startPolling = useCallback(() => {
    if (isPolling) return;
    
    setIsPolling(true);
    fetchTrackingData(); // Initial fetch
    
    const interval = setInterval(fetchTrackingData, pollInterval);
    
    return () => {
      clearInterval(interval);
      setIsPolling(false);
    };
  }, [fetchTrackingData, pollInterval, isPolling]);

  const stopPolling = useCallback(() => {
    setIsPolling(false);
  }, []);

  // Auto-start polling
  useEffect(() => {
    if (autoStart && deliveryId) {
      const cleanup = startPolling();
      return cleanup;
    }
  }, [autoStart, deliveryId, startPolling]);

  // Manual refresh
  const refresh = useCallback(() => {
    setLoading(true);
    fetchTrackingData();
  }, [fetchTrackingData]);

  return {
    tracking,
    agentLocation,
    loading,
    error,
    isPolling,
    startPolling,
    stopPolling,
    refresh
  };
}