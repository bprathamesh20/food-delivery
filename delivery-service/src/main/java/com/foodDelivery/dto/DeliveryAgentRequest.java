package com.foodDelivery.dto;

import com.foodDelivery.entity.DeliveryAgent;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DeliveryAgentRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Vehicle type is required")
    private DeliveryAgent.VehicleType vehicleType;

    private String vehicleNumber;
    private Double currentLatitude;
    private Double currentLongitude;

    public DeliveryAgentRequest() {}

    public DeliveryAgentRequest(Long userId, String name, String phoneNumber, String email,
                                DeliveryAgent.VehicleType vehicleType, String vehicleNumber,
                                Double currentLatitude, Double currentLongitude) {
        this.userId = userId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.vehicleType = vehicleType;
        this.vehicleNumber = vehicleNumber;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
    }

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
}
