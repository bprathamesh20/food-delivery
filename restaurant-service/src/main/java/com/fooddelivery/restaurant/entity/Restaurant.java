package com.fooddelivery.restaurant.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
 
@Entity
@Table(name = "restaurant")
public class Restaurant {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false, length = 100)
    private String name;
 
    @Column(nullable = false, length = 255)
    private String address;
 
    @Column(name = "is_active")
    private boolean active = true;
 
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
 
    @OneToMany(
            mappedBy = "restaurant",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonIgnore
    private List<MenuItem> menuItems = new ArrayList<>();
 
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
 
    // -------- Getters & Setters --------
 
    public Long getId() {
        return id;
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public boolean isActive() {
        return active;
    }
 
    public void setActive(boolean active) {
        this.active = active;
    }
 
    public String getAddress() {
        return address;
    }
 
    public void setAddress(String address) {
        this.address = address;
    }
 
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
 
    public List<MenuItem> getMenuItems() {
        return menuItems;
    }
 
    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }
}
