package com.fooddelivery.events;

/**
 * Centralized Kafka topic names for all microservices.
 * Use these constants to ensure consistency across all services.
 */
public final class KafkaTopics {
    
    private KafkaTopics() {
        // Prevent instantiation
    }
    
    // User Service Topics
    public static final String USER_EVENTS = "user-events";
    
    // Order Service Topics
    public static final String ORDER_EVENTS = "order-events";
    
    // Payment Service Topics
    public static final String PAYMENT_EVENTS = "payment-events";
    
    // Delivery Service Topics
    public static final String DELIVERY_EVENTS = "delivery-events";
    
    // Notification Service Topics
    public static final String NOTIFICATION_EVENTS = "notification-events";
    
    // Restaurant Service Topics
    public static final String RESTAURANT_EVENTS = "restaurant-events";
}
