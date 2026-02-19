package com.fooddel.order.kafka;

import com.fooddelivery.events.DeliveryEvent;
import com.fooddelivery.events.KafkaTopics;
import com.fooddel.order.entity.Order;
import com.fooddel.order.enums.OrderStatus;
import com.fooddel.order.repository.OrderRepository;
import com.fooddel.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryEventConsumer {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    @KafkaListener(
        topics = KafkaTopics.DELIVERY_EVENTS, 
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "deliveryEventKafkaListenerContainerFactory"
    )
    @Transactional
    public void consumeDeliveryEvent(
            @Payload DeliveryEvent deliveryEvent,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        log.info("Received delivery event: orderId={}, eventType={}, status={}, topic={}, partition={}, offset={}", 
                deliveryEvent.getOrderId(), deliveryEvent.getEventType(), deliveryEvent.getStatus(), 
                topic, partition, offset);

        try {
            Optional<Order> orderOpt = orderRepository.findById(deliveryEvent.getOrderId());
            if (orderOpt.isEmpty()) {
                log.warn("Order not found for delivery event: orderId={}", deliveryEvent.getOrderId());
                acknowledgment.acknowledge();
                return;
            }

            Order order = orderOpt.get();
            OrderStatus newStatus = mapDeliveryStatusToOrderStatus(deliveryEvent.getEventType(), deliveryEvent.getStatus());
            
            if (newStatus != null && shouldUpdateOrderStatus(order.getOrderStatus(), newStatus)) {
                OrderStatus oldStatus = order.getOrderStatus();
                order.setOrderStatus(newStatus);
                orderRepository.save(order);
                
                log.info("Updated order status: orderId={}, oldStatus={}, newStatus={}", 
                        order.getId(), oldStatus, newStatus);

                // Publish order status update event
                orderEventProducer.publishOrderStatusUpdated(order, oldStatus, newStatus);
            }

            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Error processing delivery event: orderId={}, error={}", 
                    deliveryEvent.getOrderId(), e.getMessage(), e);
            // Don't acknowledge on error - message will be retried
        }
    }

    private OrderStatus mapDeliveryStatusToOrderStatus(String eventType, String deliveryStatus) {
        switch (eventType) {
            case "DELIVERY_ASSIGNED":
                return OrderStatus.READY; // Order is ready and assigned to agent
            case "DELIVERY_PICKED_UP":
                return OrderStatus.PICKED_UP;
            case "DELIVERY_DELIVERED":
            case "DELIVERY_COMPLETED":
                return OrderStatus.DELIVERED;
            case "DELIVERY_CANCELLED":
            case "DELIVERY_FAILED":
                return OrderStatus.CANCELLED;
            default:
                log.debug("No order status mapping for delivery event: {}", eventType);
                return null;
        }
    }

    private boolean shouldUpdateOrderStatus(OrderStatus currentStatus, OrderStatus newStatus) {
        // Define valid status transitions to prevent backwards updates
        switch (currentStatus) {
            case PENDING:
                return newStatus == OrderStatus.CONFIRMED || newStatus == OrderStatus.CANCELLED;
            case CONFIRMED:
                return newStatus == OrderStatus.PREPARING || newStatus == OrderStatus.READY || 
                       newStatus == OrderStatus.PICKED_UP || newStatus == OrderStatus.CANCELLED;
            case PREPARING:
                return newStatus == OrderStatus.READY || newStatus == OrderStatus.PICKED_UP || 
                       newStatus == OrderStatus.CANCELLED;
            case READY:
                return newStatus == OrderStatus.PICKED_UP || newStatus == OrderStatus.DELIVERED || 
                       newStatus == OrderStatus.CANCELLED;
            case PICKED_UP:
                return newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.CANCELLED;
            case DELIVERED:
            case CANCELLED:
                return false; // Terminal states
            default:
                return false;
        }
    }
}