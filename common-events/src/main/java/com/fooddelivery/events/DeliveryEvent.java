package com.fooddelivery.events;

/**
 * Event published when delivery-related actions occur.
 * Event types: DELIVERY_ASSIGNED, DELIVERY_PICKED_UP, DELIVERY_IN_TRANSIT, 
 *              DELIVERY_COMPLETED, DELIVERY_CANCELLED
 */
public class DeliveryEvent extends BaseEvent {
    
    private static final long serialVersionUID = 1L;
    
    private Long deliveryId;
    private Long orderId;
    private Long deliveryAgentId;
    private String deliveryAgentName;
    private String deliveryAgentPhone;
    private String status;
    private String deliveryAddress;
    private Double currentLatitude;
    private Double currentLongitude;
    private String estimatedDeliveryTime;
    private String notes;
    
    public DeliveryEvent() {
        super();
    }
    
    public DeliveryEvent(String eventType, Long deliveryId, Long orderId, String status) {
        super(eventType);
        this.deliveryId = deliveryId;
        this.orderId = orderId;
        this.status = status;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final DeliveryEvent event = new DeliveryEvent();
        
        public Builder eventType(String eventType) {
            event.setEventType(eventType);
            return this;
        }
        
        public Builder deliveryId(Long deliveryId) {
            event.deliveryId = deliveryId;
            return this;
        }
        
        public Builder orderId(Long orderId) {
            event.orderId = orderId;
            return this;
        }
        
        public Builder deliveryAgentId(Long deliveryAgentId) {
            event.deliveryAgentId = deliveryAgentId;
            return this;
        }
        
        public Builder deliveryAgentName(String deliveryAgentName) {
            event.deliveryAgentName = deliveryAgentName;
            return this;
        }
        
        public Builder deliveryAgentPhone(String deliveryAgentPhone) {
            event.deliveryAgentPhone = deliveryAgentPhone;
            return this;
        }
        
        public Builder status(String status) {
            event.status = status;
            return this;
        }
        
        public Builder deliveryAddress(String deliveryAddress) {
            event.deliveryAddress = deliveryAddress;
            return this;
        }
        
        public Builder currentLatitude(Double currentLatitude) {
            event.currentLatitude = currentLatitude;
            return this;
        }
        
        public Builder currentLongitude(Double currentLongitude) {
            event.currentLongitude = currentLongitude;
            return this;
        }
        
        public Builder estimatedDeliveryTime(String estimatedDeliveryTime) {
            event.estimatedDeliveryTime = estimatedDeliveryTime;
            return this;
        }
        
        public Builder notes(String notes) {
            event.notes = notes;
            return this;
        }
        
        public DeliveryEvent build() {
            return event;
        }
    }
    
    // Getters and Setters
    public Long getDeliveryId() {
        return deliveryId;
    }
    
    public void setDeliveryId(Long deliveryId) {
        this.deliveryId = deliveryId;
    }
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public Long getDeliveryAgentId() {
        return deliveryAgentId;
    }
    
    public void setDeliveryAgentId(Long deliveryAgentId) {
        this.deliveryAgentId = deliveryAgentId;
    }
    
    public String getDeliveryAgentName() {
        return deliveryAgentName;
    }
    
    public void setDeliveryAgentName(String deliveryAgentName) {
        this.deliveryAgentName = deliveryAgentName;
    }
    
    public String getDeliveryAgentPhone() {
        return deliveryAgentPhone;
    }
    
    public void setDeliveryAgentPhone(String deliveryAgentPhone) {
        this.deliveryAgentPhone = deliveryAgentPhone;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getDeliveryAddress() {
        return deliveryAddress;
    }
    
    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    
    public Double getCurrentLatitude() {
        return currentLatitude;
    }
    
    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }
    
    public Double getCurrentLongitude() {
        return currentLongitude;
    }
    
    public void setCurrentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }
    
    public String getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }
    
    public void setEstimatedDeliveryTime(String estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public String toString() {
        return "DeliveryEvent{" +
                "eventType='" + getEventType() + '\'' +
                ", deliveryId=" + deliveryId +
                ", orderId=" + orderId +
                ", deliveryAgentId=" + deliveryAgentId +
                ", status='" + status + '\'' +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}
