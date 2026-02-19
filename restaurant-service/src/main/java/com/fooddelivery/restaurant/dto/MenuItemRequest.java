package com.fooddelivery.restaurant.dto;



import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
 
public class MenuItemRequest {
 
    @NotBlank(message = "Menu item name is required")
    private String name;
 
    @Min(value = 1, message = "Price must be greater than 0")
    private double price;
 
    // getters & setters
    public String getName() {
        return name;
    }
 
    public double getPrice() {
        return price;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public void setPrice(double price) {
        this.price = price;
    }
}
