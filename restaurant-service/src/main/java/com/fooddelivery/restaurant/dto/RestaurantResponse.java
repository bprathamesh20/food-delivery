package com.fooddelivery.restaurant.dto;



import java.time.LocalDateTime;
 
public class RestaurantResponse {
 
    private Long id;
    private String name;
    private String address;
    private boolean active;
    private LocalDateTime createdAt;
 
    // getters & setters
    public Long getId() {
        return id;
    }
 
    public String getName() {
        return name;
    }
 
    public String getAddress() {
        return address;
    }
 
    public boolean isActive() {
        return active;
    }
 
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public void setAddress(String address) {
        this.address = address;
    }
 
    public void setActive(boolean active) {
        this.active = active;
    }
 
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
