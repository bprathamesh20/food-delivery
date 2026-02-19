package com.foodDelivery.dto;

import java.time.LocalDateTime;

import com.foodDelivery.entity.DeliveryAgent;

public class DeliveryAgentResponse {

    private Long id;
    private Long userId;
    private String name;
    private String phoneNumber;
    private String email;
    private DeliveryAgent.VehicleType vehicleType;
    private String vehicleNumber;
    private Double currentLatitude;
    private Double currentLongitude;
    private DeliveryAgent.AgentStatus status;
    private Double rating;
    private Integer totalDeliveries;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DeliveryAgentResponse() {}

    public DeliveryAgentResponse(Long id, Long userId, String name, String phoneNumber, String email,
                                 DeliveryAgent.VehicleType vehicleType, String vehicleNumber,
                                 Double currentLatitude, Double currentLongitude,
                                 DeliveryAgent.AgentStatus status, Double rating,
                                 Integer totalDeliveries, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.vehicleType = vehicleType;
        this.vehicleNumber = vehicleNumber;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
        this.status = status;
        this.rating = rating;
        this.totalDeliveries = totalDeliveries;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ---------- GETTERS & SETTERS ----------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public DeliveryAgent.VehicleType getVehicleType() { return vehicleType; }
    public void setVehicleType(DeliveryAgent.VehicleType vehicleType) { this.vehicleType = vehicleType; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public Double getCurrentLatitude() { return currentLatitude; }
    public void setCurrentLatitude(Double currentLatitude) { this.currentLatitude = currentLatitude; }

    public Double getCurrentLongitude() { return currentLongitude; }
    public void setCurrentLongitude(Double currentLongitude) { this.currentLongitude = currentLongitude; }

    public DeliveryAgent.AgentStatus getStatus() { return status; }
    public void setStatus(DeliveryAgent.AgentStatus status) { this.status = status; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getTotalDeliveries() { return totalDeliveries; }
    public void setTotalDeliveries(Integer totalDeliveries) { this.totalDeliveries = totalDeliveries; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
