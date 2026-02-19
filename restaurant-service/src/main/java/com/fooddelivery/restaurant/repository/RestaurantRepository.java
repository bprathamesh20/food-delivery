package com.fooddelivery.restaurant.repository;


import com.fooddelivery.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
 
    // Find only active restaurants
    List<Restaurant> findByActiveTrue();
}
