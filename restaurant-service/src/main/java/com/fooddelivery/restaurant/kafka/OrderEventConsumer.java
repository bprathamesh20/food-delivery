package com.fooddelivery.restaurant.kafka;

import com.fooddelivery.events.KafkaTopics;
import com.fooddelivery.events.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventConsumer.class);

    @KafkaListener(topics = KafkaTopics.ORDER_EVENTS, groupId = "restaurant-group")
    public void consumeOrderEvent(OrderEvent event) {
        logger.info("Received order event: type={}, orderId={}, restaurantId={}", 
                event.getEventType(), event.getOrderId(), event.getRestaurantId());
        
        try {
            switch (event.getEventType()) {
                case "ORDER_CREATED":
                    handleNewOrder(event);
                    break;
                case "ORDER_CANCELLED":
                    handleOrderCancelled(event);
                    break;
                default:
                    logger.info("Order update: orderId={}, status={}", 
                            event.getOrderId(), event.getStatus());
            }
        } catch (Exception e) {
            logger.error("Error processing order event for orderId={}", event.getOrderId(), e);
        }
    }

    private void handleNewOrder(OrderEvent event) {
        logger.info("New order received for restaurant {}: orderId={}, amount={}",
                event.getRestaurantId(), event.getOrderId(), event.getTotalAmount());
        // TODO: Notify restaurant dashboard, update order queue
    }

    private void handleOrderCancelled(OrderEvent event) {
        logger.info("Order cancelled for restaurant {}: orderId={}",
                event.getRestaurantId(), event.getOrderId());
        // TODO: Update restaurant dashboard, remove from queue
    }
}
