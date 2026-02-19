package com.notification.notification.kafkaconsumer;

import com.fooddelivery.events.KafkaTopics;
import com.fooddelivery.events.DeliveryEvent;
import com.notification.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DeliveryEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(DeliveryEventConsumer.class);
    private final NotificationService notificationService;

    public DeliveryEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = KafkaTopics.DELIVERY_EVENTS, groupId = "notification-group", containerFactory = "deliveryEventKafkaListenerContainerFactory")
    public void consumeDeliveryEvent(DeliveryEvent event) {
        // Handle null events from deserialization failures
        if (event == null) {
            log.warn("Received null delivery event - skipping (likely deserialization error)");
            return;
        }
        
        log.info("Received delivery event: type={}, deliveryId={}, orderId={}",
                event.getEventType(), event.getDeliveryId(), event.getOrderId());
        
        // Note: We need customerId from the event - it should be added to DeliveryEvent
        // For now, we'll log and skip customer notifications
        
        switch (event.getEventType()) {
            case "DELIVERY_ASSIGNED":
                notifyDeliveryAssigned(event);
                break;
            case "DELIVERY_PICKED_UP":
                notifyDeliveryPickedUp(event);
                break;
            case "DELIVERY_IN_TRANSIT":
                notifyDeliveryInTransit(event);
                break;
            case "DELIVERY_DELIVERED":
            case "DELIVERY_COMPLETED":
                notifyDeliveryCompleted(event);
                break;
            default:
                log.info("Delivery status update: deliveryId={}, status={}",
                        event.getDeliveryId(), event.getStatus());
        }
    }

    private void notifyDeliveryAssigned(DeliveryEvent event) {
        log.info("Notifying: Delivery agent {} assigned for order {}",
                event.getDeliveryAgentName(), event.getOrderId());
        
        // Notify delivery agent
        if (event.getDeliveryAgentId() != null) {
            notificationService.createNotification(
                event.getDeliveryAgentId(),
                "DELIVERY_AGENT",
                "New Delivery Assigned",
                "You have been assigned delivery for order #" + event.getOrderId(),
                "DELIVERY",
                event.getOrderId(),
                event.getDeliveryId()
            );
        }
    }

    private void notifyDeliveryPickedUp(DeliveryEvent event) {
        log.info("Notifying: Order {} has been picked up", event.getOrderId());
    }

    private void notifyDeliveryInTransit(DeliveryEvent event) {
        log.info("Notifying: Order {} is on the way", event.getOrderId());
    }

    private void notifyDeliveryCompleted(DeliveryEvent event) {
        log.info("Notifying: Order {} has been delivered", event.getOrderId());
    }
}