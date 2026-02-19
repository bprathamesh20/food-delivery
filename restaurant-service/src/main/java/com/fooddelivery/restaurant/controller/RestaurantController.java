package com.fooddelivery.restaurant.controller;



import com.fooddelivery.restaurant.entity.Restaurant;
import com.fooddelivery.restaurant.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {
 
    @Autowired
    private RestaurantService restaurantService;
 
    /**
     * Register a new restaurant (alias for createRestaurant)
     */
    @PostMapping("/register")
    public Restaurant registerRestaurant(@RequestBody Restaurant restaurant) {
        return restaurantService.createRestaurant(restaurant);
    }
 
    /**
     * Create a new restaurant
     */
    @PostMapping
    public Restaurant createRestaurant(@RequestBody Restaurant restaurant) {
        return restaurantService.createRestaurant(restaurant);
    }
 
    /**
     * Get all restaurants
     */
    @GetMapping
    public List<Restaurant> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }
 
    /**
     * Get restaurant by ID
     */
    @GetMapping("/{id}")
    public Restaurant getRestaurantById(@PathVariable Long id) {
        return restaurantService.getRestaurantById(id);
    }
 
    /**
     * Disable a restaurant (soft disable)
     */
    @PutMapping("/{id}/disable")
    public String disableRestaurant(@PathVariable Long id) {
        restaurantService.disableRestaurant(id);
        return "Restaurant disabled successfully";
    }
    
    //new line added
    
    /**
     * Delete a restaurant permanently
     */
    @DeleteMapping("/{id}")
    public String deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
        return "Restaurant deleted successfully";
    }
}
