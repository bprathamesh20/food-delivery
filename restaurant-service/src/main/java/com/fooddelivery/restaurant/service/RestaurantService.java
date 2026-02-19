package com.fooddelivery.restaurant.service;




import com.fooddelivery.restaurant.entity.Restaurant;
 
import java.util.List;
 
public interface RestaurantService {
 
    Restaurant createRestaurant(Restaurant restaurant);
 
    List<Restaurant> getAllRestaurants();
 
    Restaurant getRestaurantById(Long id);
 
    void disableRestaurant(Long id);


    // NEW — delete restaurant
     void deleteRestaurant(Long id);

}