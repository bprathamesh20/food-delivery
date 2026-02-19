package com.fooddel.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "RESTAURANT-SERVICE")
public interface RestaurantServiceClient {

    @GetMapping("/api/restaurants/{id}")
    RestaurantDTO getRestaurant(@PathVariable("id") Long id);
    
    @GetMapping("/api/menus/item/{id}")
    MenuItemDTO getMenuItem(@PathVariable("id") Long id);
}
