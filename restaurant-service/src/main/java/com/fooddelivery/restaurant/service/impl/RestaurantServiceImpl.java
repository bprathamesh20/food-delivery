//package com.fooddelivery.restaurant.service.impl;
//
//
//import com.fooddelivery.restaurant.entity.Restaurant;
//import com.fooddelivery.restaurant.repository.RestaurantRepository;
//import com.fooddelivery.restaurant.service.RestaurantService;
//import org.springframework.stereotype.Service;
// 
//import java.util.List;
//
////@Service
//public class RestaurantServiceImpl implements RestaurantService {
//// 
//private final RestaurantRepository restaurantRepository;
//
//   public RestaurantServiceImpl(RestaurantRepository restaurantRepository) {
//        this.restaurantRepository = restaurantRepository;
//   }
//
//  @Override
//   public Restaurant createRestaurant(Restaurant restaurant) {
//       return restaurantRepository.save(restaurant);
//    }
// 
//    @Override
//    public List<Restaurant> getAllRestaurants() {
//        return restaurantRepository.findAll();
//   }
//
//    @Override
//  public Restaurant getRestaurantById(Long id) {
//      return restaurantRepository.findById(id)
//               .orElseThrow(() ->
//                       new RuntimeException("Restaurant not found with id: " + id));
//  }
//
//   @Override
//   public void disableRestaurant(Long id) {
//       Restaurant restaurant = getRestaurantById(id);
//       restaurant.setActive(false);
//      restaurantRepository.save(restaurant);
//   }
//   
//
//    //new line
//    @Override
//       public void deleteRestaurant(Long id) {
//        Restaurant restaurant = getRestaurantById(id); // ensures it exists
//          restaurantRepository.delete(restaurant);
//  }
//
//}
//




package com.fooddelivery.restaurant.service.impl;
 
import com.fooddelivery.restaurant.entity.Restaurant;
import com.fooddelivery.restaurant.repository.RestaurantRepository;
import com.fooddelivery.restaurant.service.RestaurantService;

import org.springframework.stereotype.Service;
 
import java.util.List;
 
@Service
public class RestaurantServiceImpl implements RestaurantService {
 
    private final RestaurantRepository restaurantRepository;
 
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }
 
    // ✅ CREATE RESTAURANT
    @Override
    public Restaurant createRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }
 
    // ✅ GET ALL RESTAURANTS
    @Override
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }
 
    // ✅ GET RESTAURANT BY ID
    @Override
    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Restaurant not found with id " + id));
    }
 
    // ✅ DISABLE RESTAURANT
    @Override
    public void disableRestaurant(Long id) {
        Restaurant restaurant = getRestaurantById(id);
        restaurant.setActive(false);
        restaurantRepository.save(restaurant);
    }
 
    // ✅ DELETE RESTAURANT
    @Override
    public void deleteRestaurant(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Restaurant not found with id " + id));
 
        restaurantRepository.delete(restaurant);
    }
}