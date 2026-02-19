package com.fooddelivery.events;

import java.math.BigDecimal;

/**
 * Event published when payment-related actions occur.
 * Event types: PAYMENT_INITIATED, PAYMENT_SUCCESS, PAYMENT_FAILED, PAYMENT_REFUNDED
 */
public class PaymentEvent extends BaseEvent {
    
    private static final long serialVersionUID = 1L;
    
    private Long orderId;
    private Long paymentId;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String status;
    private BigDecimal amount;
    private String currency;
    private String reason;
    private String transactionId;
    
    public PaymentEvent() {
        super();
    }
    
    public PaymentEvent(String eventType, Long orderId, String status, BigDecimal amount) {
        super(eventType);
        this.orderId = orderId;
        this.status = status;
        this.amount = amount;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final PaymentEvent event = new PaymentEvent();
        
        public Builder eventType(String eventType) {
            event.setEventType(eventType);
            return this;
        }
        
        public Builder orderId(Long orderId) {
            event.orderId = orderId;
            return this;
        }
        
        public Builder paymentId(Long paymentId) {
            event.paymentId = paymentId;
            return this;
        }
        
        public Builder razorpayOrderId(String razorpayOrderId) {
            event.razorpayOrderId = razorpayOrderId;
            return this;
        }
        
        public Builder razorpayPaymentId(String razorpayPaymentId) {
            event.razorpayPaymentId = razorpayPaymentId;
            return this;
        }
        
        public Builder status(String status) {
            event.status = status;
            return this;
        }
        
        public Builder amount(BigDecimal amount) {
            event.amount = amount;
            return this;
        }
        
        public Builder currency(String currency) {
            event.currency = currency;
            return this;
        }
        
        public Builder reason(String reason) {
            event.reason = reason;
            return this;
        }
        
        public Builder transactionId(String transactionId) {
            event.transactionId = transactionId;
            return this;
        }
        
        public PaymentEvent build() {
            return event;
        }
    }
    
    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public Long getPaymentId() {
        return paymentId;
    }
    
    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
    
    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }
    
    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }
    
    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }
    
    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    @Override
    public String toString() {
        return "PaymentEvent{" +
                "eventType='" + getEventType() + '\'' +
                ", orderId=" + orderId +
                ", paymentId=" + paymentId +
                ", status='" + status + '\'' +
                ", amount=" + amount +
                ", transactionId='" + transactionId + '\'' +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}
