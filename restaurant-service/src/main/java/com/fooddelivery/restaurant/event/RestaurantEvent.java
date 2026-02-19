package com.fooddelivery.restaurant.event;

import java.time.LocalDateTime;

public class RestaurantEvent {
    
    private String eventType; // RESTAURANT_REGISTERED, MENU_UPDATED, RESTAURANT_STATUS_CHANGED
    private Long restaurantId;
    private String restaurantName;
    private String address;
    private Boolean isActive;
    private LocalDateTime timestamp;

    public RestaurantEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public RestaurantEvent(String eventType, Long restaurantId, String restaurantName) {
        this.eventType = eventType;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "RestaurantEvent{" +
                "eventType='" + eventType + '\'' +
                ", restaurantId=" + restaurantId +
                ", restaurantName='" + restaurantName + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
