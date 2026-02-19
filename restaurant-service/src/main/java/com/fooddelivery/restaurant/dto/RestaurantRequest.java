package com.fooddelivery.restaurant.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
public class RestaurantRequest {
 
    @NotBlank(message = "Restaurant name is required")
    @Size(min = 2, max = 100)
    private String name;
 
    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 255)
    private String address;
 
    // getters & setters
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public String getAddress() {
        return address;
    }
 
    public void setAddress(String address) {
        this.address = address;
    }
}