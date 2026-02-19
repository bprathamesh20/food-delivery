package com.fooddelivery.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Base class for all events in the food delivery system.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@type",
    defaultImpl = OrderEvent.class
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = OrderEvent.class, name = "ORDER"),
    @JsonSubTypes.Type(value = UserEvent.class, name = "USER"),
    @JsonSubTypes.Type(value = PaymentEvent.class, name = "PAYMENT"),
    @JsonSubTypes.Type(value = DeliveryEvent.class, name = "DELIVERY")
})
public abstract class BaseEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String eventType;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    protected BaseEvent() {
        this.timestamp = LocalDateTime.now();
    }
    
    protected BaseEvent(String eventType) {
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
