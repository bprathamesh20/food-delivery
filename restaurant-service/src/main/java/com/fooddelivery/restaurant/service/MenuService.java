package com.fooddelivery.restaurant.service;


import com.fooddelivery.restaurant.entity.MenuItem;
 
import java.util.List;
 
public interface MenuService {
 
    MenuItem addMenuItem(Long restaurantId, MenuItem menuItem);
 
    List<MenuItem> getMenuByRestaurant(Long restaurantId);
    
    MenuItem getMenuItemById(Long menuItemId);
 
    void disableMenuItem(Long menuItemId);

	void deleteMenuItem(Long menuItemId);

	
}