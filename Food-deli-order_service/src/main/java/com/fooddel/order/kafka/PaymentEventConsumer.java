package com.fooddel.order.kafka;

import com.fooddelivery.events.KafkaTopics;
import com.fooddelivery.events.PaymentEvent;
import com.fooddel.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = KafkaTopics.PAYMENT_EVENTS, groupId = "order-group")
    public void consumePaymentEvent(PaymentEvent paymentEvent) {
        log.info("Received payment event for order: {}, status: {}", 
                paymentEvent.getOrderId(), paymentEvent.getStatus());
        
        try {
            orderService.handlePaymentEvent(paymentEvent);
            log.info("Payment event processed successfully for order: {}", 
                    paymentEvent.getOrderId());
        } catch (Exception e) {
            log.error("Error processing payment event for order: {}", 
                    paymentEvent.getOrderId(), e);
        }
    }
}
