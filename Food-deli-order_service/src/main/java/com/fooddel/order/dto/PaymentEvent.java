package com.fooddel.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {

    private Long orderId;
    private String status; // SUCCESS or FAILED
    private BigDecimal amount;
    private String transactionId;
}
