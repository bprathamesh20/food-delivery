package com.notification.notification.kafkaconsumer;

import com.fooddelivery.events.KafkaTopics;
import com.fooddelivery.events.PaymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class kafkaconsumer {

    private static final Logger log = LoggerFactory.getLogger(kafkaconsumer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    public kafkaconsumer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_EVENTS, groupId = "notification-group", containerFactory = "paymentEventKafkaListenerContainerFactory")
    public void consumePaymentEvent(PaymentEvent event) {
        log.info("Received payment event: orderId={}, status={}", 
                event.getOrderId(), event.getStatus());

        String notificationMessage = null;
        String status = event.getStatus();
        
        if ("PAYMENT_SUCCESS".equals(status) || "SUCCESS".equals(status)) {
            notificationMessage = "Payment successful! Order " + event.getOrderId() + " has been placed.";
        } else if ("PAYMENT_FAILED".equals(status) || "FAILED".equals(status)) {
            notificationMessage = "Payment failed for order " + event.getOrderId() + ". Please try again.";
        } else if ("PAYMENT_REFUNDED".equals(status)) {
            notificationMessage = "Refund processed for order " + event.getOrderId();
        }

        if (notificationMessage != null) {
            kafkaTemplate.send(KafkaTopics.NOTIFICATION_EVENTS, notificationMessage);
            log.info("Produced notification: {}", notificationMessage);
        }
    }
}
