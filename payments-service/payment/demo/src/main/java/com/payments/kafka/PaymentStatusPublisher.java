package com.payments.kafka;

import com.fooddelivery.events.KafkaTopics;
import com.fooddelivery.events.PaymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentStatusPublisher {

    private static final Logger log = LoggerFactory.getLogger(PaymentStatusPublisher.class);

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public PaymentStatusPublisher(KafkaTemplate<String, PaymentEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(PaymentEvent event) {
        kafkaTemplate.send(KafkaTopics.PAYMENT_EVENTS, event.getOrderId().toString(), event);
        log.info("Published payment event: orderId={}, status={}, topic={}",
                event.getOrderId(), event.getStatus(), KafkaTopics.PAYMENT_EVENTS);
    }
}
