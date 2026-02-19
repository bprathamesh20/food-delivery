package com.fooddel.order.dto;

import com.fooddel.order.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {

    private Long orderId;
    private Long customerId;
    private Long restaurantId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String deliveryAddress;
    private String eventType; // ORDER_CREATED, ORDER_CONFIRMED, ORDER_CANCELLED, STATUS_CHANGED
}
