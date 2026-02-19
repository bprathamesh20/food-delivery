package com.fooddel.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddel.order.dto.CreateOrderRequest;
import com.fooddel.order.dto.OrderResponse;
import com.fooddel.order.dto.UpdateOrderStatusRequest;
import com.fooddel.order.enums.OrderStatus;
import com.fooddel.order.enums.PaymentStatus;
import com.fooddel.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private OrderResponse orderResponse;
    private CreateOrderRequest createOrderRequest;

    @BeforeEach
    void setUp() {
        orderResponse = new OrderResponse();
        orderResponse.setId(1L);
        orderResponse.setCustomerId(100L);
        orderResponse.setRestaurantId(200L);
        orderResponse.setOrderStatus(OrderStatus.PENDING);
        orderResponse.setPaymentStatus(PaymentStatus.PENDING);
        orderResponse.setTotalAmount(BigDecimal.valueOf(25.50));
        orderResponse.setDeliveryAddress("123 Main St");
        orderResponse.setCreatedAt(LocalDateTime.now());
        orderResponse.setUpdatedAt(LocalDateTime.now());
        orderResponse.setItems(new ArrayList<>());

        CreateOrderRequest.OrderItemRequest itemRequest = 
                new CreateOrderRequest.OrderItemRequest(101L, 2);
        createOrderRequest = new CreateOrderRequest(
                100L, 200L, "123 Main St", null, List.of(itemRequest));
    }

    @Test
    void testCreateOrder_Success() throws Exception {
        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(orderResponse);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerId").value(100))
                .andExpect(jsonPath("$.orderStatus").value("PENDING"));
    }

    @Test
    void testGetOrderById_Success() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(orderResponse);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerId").value(100));
    }

    @Test
    void testGetOrdersByCustomer_Success() throws Exception {
        when(orderService.getOrdersByCustomer(100L)).thenReturn(List.of(orderResponse));

        mockMvc.perform(get("/api/orders/customer/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].customerId").value(100));
    }

    @Test
    void testUpdateOrderStatus_Success() throws Exception {
        orderResponse.setOrderStatus(OrderStatus.CONFIRMED);
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(OrderStatus.CONFIRMED);
        
        when(orderService.updateOrderStatus(eq(1L), any(OrderStatus.class))).thenReturn(orderResponse);

        mockMvc.perform(put("/api/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("CONFIRMED"));
    }

    @Test
    void testCancelOrder_Success() throws Exception {
        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isNoContent());
    }
}
