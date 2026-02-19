package com.fooddel.order.service;

import com.fooddel.order.dto.CreateOrderRequest;
import com.fooddel.order.dto.OrderResponse;
import com.fooddel.order.entity.Order;
import com.fooddelivery.events.PaymentEvent;
import com.fooddel.order.enums.OrderStatus;
import com.fooddel.order.enums.PaymentStatus;
import com.fooddel.order.exception.InvalidOrderStatusException;
import com.fooddel.order.exception.OrderNotFoundException;
import com.fooddel.order.kafka.OrderEventProducer;
import com.fooddel.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventProducer orderEventProducer;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;
    private CreateOrderRequest createOrderRequest;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomerId(100L);
        testOrder.setRestaurantId(200L);
        testOrder.setOrderStatus(OrderStatus.PENDING);
        testOrder.setPaymentStatus(PaymentStatus.PENDING);
        testOrder.setTotalAmount(BigDecimal.valueOf(25.50));
        testOrder.setDeliveryAddress("123 Main St");
        testOrder.setOrderItems(new ArrayList<>());

        CreateOrderRequest.OrderItemRequest itemRequest = 
                new CreateOrderRequest.OrderItemRequest(101L, 2);
        createOrderRequest = new CreateOrderRequest(
                100L, 200L, "123 Main St", null, List.of(itemRequest));
    }

    @Test
    void testCreateOrder_Success() {
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        OrderResponse response = orderService.createOrder(createOrderRequest);

        assertNotNull(response);
        assertEquals(100L, response.getCustomerId());
        assertEquals(200L, response.getRestaurantId());
        assertEquals(OrderStatus.PENDING, response.getOrderStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderEventProducer, times(1)).publishOrderEvent(any());
    }

    @Test
    void testGetOrderById_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        OrderResponse response = orderService.getOrderById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(100L, response.getCustomerId());
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(999L));
    }

    @Test
    void testUpdateOrderStatus_ValidTransition() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        OrderResponse response = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);

        assertEquals(OrderStatus.CONFIRMED, response.getOrderStatus());
        verify(orderEventProducer, times(1)).publishOrderEvent(any());
    }

    @Test
    void testUpdateOrderStatus_InvalidTransition() {
        testOrder.setOrderStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        assertThrows(InvalidOrderStatusException.class, 
                () -> orderService.updateOrderStatus(1L, OrderStatus.PENDING));
    }

    @Test
    void testCancelOrder_AllowedStatus() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        assertDoesNotThrow(() -> orderService.cancelOrder(1L));
        verify(orderEventProducer, times(1)).publishOrderEvent(any());
    }

    @Test
    void testCancelOrder_DisallowedStatus() {
        testOrder.setOrderStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        assertThrows(InvalidOrderStatusException.class, () -> orderService.cancelOrder(1L));
    }

    @Test
    void testHandlePaymentEvent_Success() {
        PaymentEvent paymentEvent = PaymentEvent.builder()
                .orderId(1L)
                .status("SUCCESS")
                .amount(BigDecimal.valueOf(25.50))
                .transactionId("TXN123")
                .build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        orderService.handlePaymentEvent(paymentEvent);

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderEventProducer, times(1)).publishOrderEvent(any());
    }

    @Test
    void testHandlePaymentEvent_Failure() {
        PaymentEvent paymentEvent = PaymentEvent.builder()
                .orderId(1L)
                .status("FAILED")
                .amount(BigDecimal.valueOf(25.50))
                .transactionId("TXN124")
                .build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        orderService.handlePaymentEvent(paymentEvent);

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderEventProducer, never()).publishOrderEvent(any());
    }
}
