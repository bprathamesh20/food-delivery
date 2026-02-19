package com.notification.notification.kafkaconsumer;

import com.fooddelivery.events.KafkaTopics;
import com.fooddelivery.events.OrderEvent;
import com.notification.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);
    private final NotificationService notificationService;

    public OrderEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = KafkaTopics.ORDER_EVENTS, groupId = "notification-group", containerFactory = "orderEventKafkaListenerContainerFactory")
    public void consumeOrderEvent(OrderEvent event) {
        log.info("Received order event: type={}, orderId={}, customerId={}",
                event.getEventType(), event.getOrderId(), event.getCustomerId());
        
        switch (event.getEventType()) {
            case "ORDER_CREATED":
                notifyOrderCreated(event);
                break;
            case "ORDER_CONFIRMED":
                notifyOrderConfirmed(event);
                break;
            case "ORDER_READY_FOR_PICKUP":
                notifyOrderReady(event);
                break;
            case "ORDER_CANCELLED":
                notifyOrderCancelled(event);
                break;
            default:
                log.info("Order status update: orderId={}, status={}", 
                        event.getOrderId(), event.getStatus());
        }
    }

    private void notifyOrderCreated(OrderEvent event) {
        log.info("Sending order confirmation notification for orderId: {}", event.getOrderId());
        notificationService.createNotification(
            event.getCustomerId(),
            "CUSTOMER",
            "Order Placed Successfully",
            "Your order #" + event.getOrderId() + " has been placed successfully and is being prepared.",
            "ORDER",
            event.getOrderId(),
            null
        );
    }

    private void notifyOrderConfirmed(OrderEvent event) {
        log.info("Notifying customer that order {} is confirmed", event.getOrderId());
        notificationService.createNotification(
            event.getCustomerId(),
            "CUSTOMER",
            "Order Confirmed",
            "Your order #" + event.getOrderId() + " has been confirmed and will be delivered soon.",
            "ORDER",
            event.getOrderId(),
            null
        );
    }

    private void notifyOrderReady(OrderEvent event) {
        log.info("Notifying customer that order {} is ready for pickup", event.getOrderId());
        notificationService.createNotification(
            event.getCustomerId(),
            "CUSTOMER",
            "Order Ready",
            "Your order #" + event.getOrderId() + " is ready and will be picked up by delivery agent soon.",
            "ORDER",
            event.getOrderId(),
            null
        );
    }

    private void notifyOrderCancelled(OrderEvent event) {
        log.info("Notifying customer that order {} has been cancelled", event.getOrderId());
        notificationService.createNotification(
            event.getCustomerId(),
            "CUSTOMER",
            "Order Cancelled",
            "Your order #" + event.getOrderId() + " has been cancelled.",
            "ORDER",
            event.getOrderId(),
            null
        );
    }
}