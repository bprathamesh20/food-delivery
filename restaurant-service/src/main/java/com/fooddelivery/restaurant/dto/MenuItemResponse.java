package com.fooddelivery.restaurant.dto;



public class MenuItemResponse {
 
    private Long id;
    private String name;
    private double price;
    private boolean available;
    private Long restaurantId;
 
    // getters & setters
    public Long getId() {
        return id;
    }
 
    public String getName() {
        return name;
    }
 
    public double getPrice() {
        return price;
    }
 
    public boolean isAvailable() {
        return available;
    }
 
    public Long getRestaurantId() {
        return restaurantId;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public void setPrice(double price) {
        this.price = price;
    }
 
    public void setAvailable(boolean available) {
        this.available = available;
    }
 
    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }
}
