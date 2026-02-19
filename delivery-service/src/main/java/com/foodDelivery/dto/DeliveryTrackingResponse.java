package com.foodDelivery.dto;

import com.foodDelivery.entity.Delivery;
import java.time.LocalDateTime;

public class DeliveryTrackingResponse {
    
    private Long id;
    private Long deliveryId;
    private Double latitude;
    private Double longitude;
    private Delivery.DeliveryStatus statusUpdate;
    private String remarks;
    private LocalDateTime timestamp;

    // Constructors
    public DeliveryTrackingResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDeliveryId() { return deliveryId; }
    public void setDeliveryId(Long deliveryId) { this.deliveryId = deliveryId; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Delivery.DeliveryStatus getStatusUpdate() { return statusUpdate; }
    public void setStatusUpdate(Delivery.DeliveryStatus statusUpdate) { this.statusUpdate = statusUpdate; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
