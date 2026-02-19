package com.fooddelivery.restaurant.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
 
@Entity
@Table(name = "menu_item")
public class MenuItem {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false, length = 100)
    private String name;
 
    @Column(nullable = false)
    private double price;
 
    @Column(nullable = false)
    private boolean available = true;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonIgnore
    private Restaurant restaurant;
 
    // -------- Getters & Setters --------
 
    public Long getId() {
        return id;
    }
 
    public String getName() {
        return name;
    }
 
    public boolean isAvailable() {
        return available;
    }
 
    public double getPrice() {
        return price;
    }
 
    public Restaurant getRestaurant() {
        return restaurant;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public void setAvailable(boolean available) {
        this.available = available;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public void setPrice(double price) {
        this.price = price;
    }
 
    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}
