package com.fooddel.order.kafka;

import com.fooddelivery.events.KafkaTopics;
import com.fooddelivery.events.OrderEvent;
import com.fooddel.order.entity.Order;
import com.fooddel.order.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void publishOrderEvent(OrderEvent event) {
        log.info("Publishing order event: {} for order: {}", event.getEventType(), event.getOrderId());
        kafkaTemplate.send(KafkaTopics.ORDER_EVENTS, event.getOrderId().toString(), event);
        log.info("Order event published successfully");
    }

    public void publishOrderStatusUpdated(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        OrderEvent event = OrderEvent.builder()
                .eventType("ORDER_STATUS_UPDATED")
                .orderId(order.getId())
                .customerId(order.getCustomerId())
                .restaurantId(order.getRestaurantId())
                .status(newStatus.name())
                .totalAmount(order.getTotalAmount())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryInstructions(order.getSpecialInstructions())
                .build();

        log.info("Publishing order status update: orderId={}, oldStatus={}, newStatus={}", 
                order.getId(), oldStatus, newStatus);
        kafkaTemplate.send(KafkaTopics.ORDER_EVENTS, order.getId().toString(), event);
        log.info("Order status update event published successfully");
    }
}
