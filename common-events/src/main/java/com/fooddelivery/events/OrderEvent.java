package com.fooddelivery.events;

import java.math.BigDecimal;

/**
 * Event published when order-related actions occur.
 * Event types: ORDER_CREATED, ORDER_CONFIRMED, ORDER_PREPARING, ORDER_READY_FOR_PICKUP, 
 *              ORDER_PICKED_UP, ORDER_DELIVERED, ORDER_CANCELLED
 */
public class OrderEvent extends BaseEvent {
    
    private static final long serialVersionUID = 1L;
    
    private Long orderId;
    private Long customerId;
    private Long restaurantId;
    private String status;
    private BigDecimal totalAmount;
    
    // Delivery-related fields
    private String pickupAddress;
    private Double pickupLatitude;
    private Double pickupLongitude;
    private String deliveryAddress;
    private Double deliveryLatitude;
    private Double deliveryLongitude;
    private String deliveryInstructions;
    private BigDecimal deliveryFee;
    
    // Customer contact info
    private String customerName;
    private String customerPhone;
    
    public OrderEvent() {
        super();
    }
    
    public OrderEvent(String eventType, Long orderId, Long customerId, Long restaurantId) {
        super(eventType);
        this.orderId = orderId;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
    }
    
    // Builder pattern for easier construction
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final OrderEvent event = new OrderEvent();
        
        public Builder eventType(String eventType) {
            event.setEventType(eventType);
            return this;
        }
        
        public Builder orderId(Long orderId) {
            event.orderId = orderId;
            return this;
        }
        
        public Builder customerId(Long customerId) {
            event.customerId = customerId;
            return this;
        }
        
        public Builder restaurantId(Long restaurantId) {
            event.restaurantId = restaurantId;
            return this;
        }
        
        public Builder status(String status) {
            event.status = status;
            return this;
        }
        
        public Builder totalAmount(BigDecimal totalAmount) {
            event.totalAmount = totalAmount;
            return this;
        }
        
        public Builder pickupAddress(String pickupAddress) {
            event.pickupAddress = pickupAddress;
            return this;
        }
        
        public Builder pickupLatitude(Double pickupLatitude) {
            event.pickupLatitude = pickupLatitude;
            return this;
        }
        
        public Builder pickupLongitude(Double pickupLongitude) {
            event.pickupLongitude = pickupLongitude;
            return this;
        }
        
        public Builder deliveryAddress(String deliveryAddress) {
            event.deliveryAddress = deliveryAddress;
            return this;
        }
        
        public Builder deliveryLatitude(Double deliveryLatitude) {
            event.deliveryLatitude = deliveryLatitude;
            return this;
        }
        
        public Builder deliveryLongitude(Double deliveryLongitude) {
            event.deliveryLongitude = deliveryLongitude;
            return this;
        }
        
        public Builder deliveryInstructions(String deliveryInstructions) {
            event.deliveryInstructions = deliveryInstructions;
            return this;
        }
        
        public Builder deliveryFee(BigDecimal deliveryFee) {
            event.deliveryFee = deliveryFee;
            return this;
        }
        
        public Builder customerName(String customerName) {
            event.customerName = customerName;
            return this;
        }
        
        public Builder customerPhone(String customerPhone) {
            event.customerPhone = customerPhone;
            return this;
        }
        
        public OrderEvent build() {
            return event;
        }
    }
    
    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public Long getRestaurantId() {
        return restaurantId;
    }
    
    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getPickupAddress() {
        return pickupAddress;
    }
    
    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }
    
    public Double getPickupLatitude() {
        return pickupLatitude;
    }
    
    public void setPickupLatitude(Double pickupLatitude) {
        this.pickupLatitude = pickupLatitude;
    }
    
    public Double getPickupLongitude() {
        return pickupLongitude;
    }
    
    public void setPickupLongitude(Double pickupLongitude) {
        this.pickupLongitude = pickupLongitude;
    }
    
    public String getDeliveryAddress() {
        return deliveryAddress;
    }
    
    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    
    public Double getDeliveryLatitude() {
        return deliveryLatitude;
    }
    
    public void setDeliveryLatitude(Double deliveryLatitude) {
        this.deliveryLatitude = deliveryLatitude;
    }
    
    public Double getDeliveryLongitude() {
        return deliveryLongitude;
    }
    
    public void setDeliveryLongitude(Double deliveryLongitude) {
        this.deliveryLongitude = deliveryLongitude;
    }
    
    public String getDeliveryInstructions() {
        return deliveryInstructions;
    }
    
    public void setDeliveryInstructions(String deliveryInstructions) {
        this.deliveryInstructions = deliveryInstructions;
    }
    
    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }
    
    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getCustomerPhone() {
        return customerPhone;
    }
    
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
    
    @Override
    public String toString() {
        return "OrderEvent{" +
                "eventType='" + getEventType() + '\'' +
                ", orderId=" + orderId +
                ", customerId=" + customerId +
                ", restaurantId=" + restaurantId +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                ", deliveryAddress='" + deliveryAddress + '\'' +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}
