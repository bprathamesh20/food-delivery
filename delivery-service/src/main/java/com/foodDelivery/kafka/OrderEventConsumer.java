package com.foodDelivery.kafka;

import com.fooddelivery.events.KafkaTopics;
import com.fooddelivery.events.OrderEvent;
import com.foodDelivery.dto.DeliveryRequest;
import com.foodDelivery.dto.DeliveryResponse;
import com.foodDelivery.entity.Delivery;
import com.foodDelivery.service.DeliveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final DeliveryService deliveryService;

    public OrderEventConsumer(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @KafkaListener(topics = KafkaTopics.ORDER_EVENTS, groupId = "${spring.kafka.consumer.group-id}")
    public void consumeOrderEvent(OrderEvent orderEvent) {
        logger.info("Received order event: {}", orderEvent);

        try {
            switch (orderEvent.getEventType()) {
                case "ORDER_CONFIRMED":
                    handleOrderCreated(orderEvent);
                    break;
                case "ORDER_READY_FOR_PICKUP":
                    handleOrderReadyForPickup(orderEvent);
                    break;
                case "ORDER_CANCELLED":
                    handleOrderCancelled(orderEvent);
                    break;
                default:
                    logger.warn("Unknown event type: {}", orderEvent.getEventType());
            }
        } catch (Exception e) {
            logger.error("Error processing order event: {}", e.getMessage(), e);
        }
    }

    private void handleOrderCreated(OrderEvent orderEvent) {
        logger.info("Handling ORDER_CREATED/ORDER_CONFIRMED event for orderId: {}", orderEvent.getOrderId());
        
        DeliveryRequest deliveryRequest = new DeliveryRequest();
        deliveryRequest.setOrderId(orderEvent.getOrderId());
        deliveryRequest.setRestaurantId(orderEvent.getRestaurantId());
        deliveryRequest.setCustomerId(orderEvent.getCustomerId());
        
        // Pickup address - use from event or default to restaurant address
        String pickupAddress = orderEvent.getPickupAddress();
        if (pickupAddress == null || pickupAddress.trim().isEmpty()) {
            // Default pickup address to "Restaurant #X" if not provided
            pickupAddress = "Restaurant Aroma";
            logger.warn("Pickup address not provided in event, using default: {}", pickupAddress);
        }
        deliveryRequest.setPickupAddress(pickupAddress);
        deliveryRequest.setPickupLatitude(orderEvent.getPickupLatitude() != null ? orderEvent.getPickupLatitude() : 18.5204); // Default Pune coordinates
        deliveryRequest.setPickupLongitude(orderEvent.getPickupLongitude() != null ? orderEvent.getPickupLongitude() : 73.8567);
        
        // Delivery address - must be provided
        String deliveryAddress = orderEvent.getDeliveryAddress();
        if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
            logger.error("Delivery address is required but not provided for order: {}", orderEvent.getOrderId());
            throw new IllegalArgumentException("Delivery address is required");
        }
        deliveryRequest.setDeliveryAddress(deliveryAddress);
        deliveryRequest.setDeliveryLatitude(orderEvent.getDeliveryLatitude() != null ? orderEvent.getDeliveryLatitude() : 18.5204);
        deliveryRequest.setDeliveryLongitude(orderEvent.getDeliveryLongitude() != null ? orderEvent.getDeliveryLongitude() : 73.8567);
        
        deliveryRequest.setDeliveryInstructions(orderEvent.getDeliveryInstructions());
        
        // Convert BigDecimal to Double for delivery fee
        BigDecimal fee = orderEvent.getDeliveryFee();
        deliveryRequest.setDeliveryFee(fee != null ? fee.doubleValue() : 50.0); // Default delivery fee

        DeliveryResponse delivery = deliveryService.createDelivery(deliveryRequest);
        logger.info("Delivery created successfully: deliveryId={}", delivery.getId());
    }

    private void handleOrderReadyForPickup(OrderEvent orderEvent) {
        logger.info("Handling ORDER_READY_FOR_PICKUP event for orderId: {}", orderEvent.getOrderId());
        // Logic to notify delivery agent
        // Auto-assign delivery agent if configured
    }

    private void handleOrderCancelled(OrderEvent orderEvent) {
        logger.info("Handling ORDER_CANCELLED event for orderId: {}", orderEvent.getOrderId());
        
        try {
            DeliveryResponse delivery = deliveryService.getDeliveryByOrderId(orderEvent.getOrderId());
            deliveryService.updateDeliveryStatus(
                delivery.getId(), 
                Delivery.DeliveryStatus.CANCELLED, 
                "Order cancelled by customer/restaurant"
            );
            logger.info("Delivery cancelled successfully: deliveryId={}", delivery.getId());
        } catch (Exception e) {
            logger.error("Error cancelling delivery: {}", e.getMessage());
        }
    }
}
