package com.fooddel.order.service;

import com.fooddel.order.client.MenuItemDTO;
import com.fooddel.order.client.RestaurantDTO;
import com.fooddel.order.client.RestaurantServiceClient;
import com.fooddel.order.client.UserDTO;
import com.fooddel.order.client.UserServiceClient;
import com.fooddel.order.dto.CreateOrderRequest;
import com.fooddel.order.dto.OrderResponse;
import com.fooddel.order.entity.Order;
import com.fooddelivery.events.OrderEvent;
import com.fooddelivery.events.PaymentEvent;
import com.fooddel.order.entity.OrderItem;
import com.fooddel.order.enums.OrderStatus;
import com.fooddel.order.enums.PaymentStatus;
import com.fooddel.order.exception.InvalidOrderStatusException;
import com.fooddel.order.exception.OrderNotFoundException;
import com.fooddel.order.kafka.OrderEventProducer;
import com.fooddel.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;
    private final RestaurantServiceClient restaurantServiceClient;
    private final UserServiceClient userServiceClient;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order for customer: {}, restaurant: {}", 
                request.getCustomerId(), request.getRestaurantId());

        // Validate customer exists
        log.debug("Validating customer: {}", request.getCustomerId());
        UserDTO customer = userServiceClient.getUser(request.getCustomerId());
        log.debug("Customer validated: {}", customer.getUsername());

        // Validate restaurant exists and is active
        log.debug("Validating restaurant: {}", request.getRestaurantId());
        RestaurantDTO restaurant = restaurantServiceClient.getRestaurant(request.getRestaurantId());
        if (!restaurant.isActive()) {
            throw new IllegalStateException("Restaurant is not currently accepting orders");
        }
        log.debug("Restaurant validated: {}", restaurant.getName());

        // Create order entity
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setRestaurantId(request.getRestaurantId());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setSpecialInstructions(request.getSpecialInstructions());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        // Create order items with validation
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            // Validate menu item exists and get actual price
            log.debug("Validating menu item: {}", itemRequest.getMenuItemId());
            MenuItemDTO menuItem = restaurantServiceClient.getMenuItem(itemRequest.getMenuItemId());
            
            if (!menuItem.isAvailable()) {
                throw new IllegalStateException("Menu item " + menuItem.getName() + " is not available");
            }
            
            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItemId(itemRequest.getMenuItemId());
            orderItem.setMenuItemName(menuItem.getName());
            orderItem.setPricePerUnit(menuItem.getPrice());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.calculateSubtotal();
            
            totalAmount = totalAmount.add(orderItem.getSubtotal());
            order.addOrderItem(orderItem);
        }

        order.setTotalAmount(totalAmount);

        // Save order
        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {}", savedOrder.getId());

        // Publish order created event
        OrderEvent event = OrderEvent.builder()
                .eventType("ORDER_CREATED")
                .orderId(savedOrder.getId())
                .customerId(savedOrder.getCustomerId())
                .restaurantId(savedOrder.getRestaurantId())
                .status(savedOrder.getOrderStatus().name())
                .totalAmount(savedOrder.getTotalAmount())
                .deliveryAddress(savedOrder.getDeliveryAddress())
                .build();
        orderEventProducer.publishOrderEvent(event);

        return mapToOrderResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        log.info("Fetching order with ID: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return mapToOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomer(Long customerId) {
        log.info("Fetching orders for customer: {}", customerId);
        List<Order> orders = orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByRestaurant(Long restaurantId) {
        log.info("Fetching orders for restaurant: {}", restaurantId);
        List<Order> orders = orderRepository.findByRestaurantId(restaurantId);
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        log.info("Fetching all orders");
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        log.info("Updating order {} status to {}", orderId, newStatus);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // Validate status transition
        validateStatusTransition(order.getOrderStatus(), newStatus);

        order.setOrderStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        // Publish status change event
        OrderEvent event = OrderEvent.builder()
                .eventType("STATUS_CHANGED")
                .orderId(updatedOrder.getId())
                .customerId(updatedOrder.getCustomerId())
                .restaurantId(updatedOrder.getRestaurantId())
                .status(updatedOrder.getOrderStatus().name())
                .totalAmount(updatedOrder.getTotalAmount())
                .deliveryAddress(updatedOrder.getDeliveryAddress())
                .build();
        orderEventProducer.publishOrderEvent(event);

        log.info("Order {} status updated successfully", orderId);
        return mapToOrderResponse(updatedOrder);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        log.info("Cancelling order: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // Only allow cancellation for certain statuses
        if (order.getOrderStatus() == OrderStatus.DELIVERED || 
            order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusException(
                    "Cannot cancel order with status: " + order.getOrderStatus());
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // Publish cancellation event
        OrderEvent event = OrderEvent.builder()
                .eventType("ORDER_CANCELLED")
                .orderId(order.getId())
                .customerId(order.getCustomerId())
                .restaurantId(order.getRestaurantId())
                .status(order.getOrderStatus().name())
                .totalAmount(order.getTotalAmount())
                .deliveryAddress(order.getDeliveryAddress())
                .build();
        orderEventProducer.publishOrderEvent(event);

        log.info("Order {} cancelled successfully", orderId);
    }

    @Transactional
    public void handlePaymentEvent(PaymentEvent paymentEvent) {
        log.info("Handling payment event for order: {}", paymentEvent.getOrderId());
        
        Order order = orderRepository.findById(paymentEvent.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(paymentEvent.getOrderId()));

        if ("SUCCESS".equalsIgnoreCase(paymentEvent.getStatus())) {
            order.setPaymentStatus(PaymentStatus.COMPLETED);
            order.setOrderStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);

            // Publish order confirmed event (triggers delivery assignment)
            OrderEvent orderEvent = OrderEvent.builder()
                    .eventType("ORDER_CONFIRMED")
                    .orderId(order.getId())
                    .customerId(order.getCustomerId())
                    .restaurantId(order.getRestaurantId())
                    .status(order.getOrderStatus().name())
                    .totalAmount(order.getTotalAmount())
                    .deliveryAddress(order.getDeliveryAddress())
                    .build();
            orderEventProducer.publishOrderEvent(orderEvent);

            log.info("Payment successful for order: {}, status updated to CONFIRMED", 
                    paymentEvent.getOrderId());
        } else {
            order.setPaymentStatus(PaymentStatus.FAILED);
            orderRepository.save(order);
            log.warn("Payment failed for order: {}", paymentEvent.getOrderId());
        }
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Define valid transitions
        boolean isValid = switch (currentStatus) {
            case PENDING -> newStatus == OrderStatus.CONFIRMED || newStatus == OrderStatus.CANCELLED;
            case CONFIRMED -> newStatus == OrderStatus.PREPARING || newStatus == OrderStatus.CANCELLED;
            case PREPARING -> newStatus == OrderStatus.READY || newStatus == OrderStatus.CANCELLED;
            case READY -> newStatus == OrderStatus.PICKED_UP;
            case PICKED_UP -> newStatus == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false; // Terminal states
        };

        if (!isValid) {
            throw new InvalidOrderStatusException(
                    String.format("Invalid status transition from %s to %s", currentStatus, newStatus));
        }
    }

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerId(order.getCustomerId());
        response.setRestaurantId(order.getRestaurantId());
        response.setOrderStatus(order.getOrderStatus());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setDeliveryAddress(order.getDeliveryAddress());
        response.setSpecialInstructions(order.getSpecialInstructions());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());

        List<OrderResponse.OrderItemResponse> items = order.getOrderItems().stream()
                .map(item -> new OrderResponse.OrderItemResponse(
                        item.getId(),
                        item.getMenuItemId(),
                        item.getMenuItemName(),
                        item.getQuantity(),
                        item.getPricePerUnit(),
                        item.getSubtotal()
                ))
                .collect(Collectors.toList());
        response.setItems(items);

        return response;
    }
}
