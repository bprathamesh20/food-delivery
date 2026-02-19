package com.fooddelivery.restaurant.service.impl;



import com.fooddelivery.restaurant.entity.MenuItem;
import com.fooddelivery.restaurant.entity.Restaurant;
import com.fooddelivery.restaurant.repository.MenuItemRepository;
import com.fooddelivery.restaurant.repository.RestaurantRepository;
import com.fooddelivery.restaurant.service.MenuService;
import org.springframework.stereotype.Service;
 
import java.util.List;
 
@Service
public  class MenuServiceImpl implements MenuService {
 
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
 
    public MenuServiceImpl(MenuItemRepository menuItemRepository,
                           RestaurantRepository restaurantRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }
 
    @Override
    public MenuItem addMenuItem(Long restaurantId, MenuItem menuItem) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() ->
                        new RuntimeException("Restaurant not found with id: " + restaurantId));
 
        menuItem.setRestaurant(restaurant);
        return menuItemRepository.save(menuItem);
    }
 
    @Override
    public List<MenuItem> getMenuByRestaurant(Long restaurantId) {
        return menuItemRepository.findByRestaurantIdAndAvailableTrue(restaurantId);
    }
    
    @Override
    public MenuItem getMenuItemById(Long menuItemId) {
        return menuItemRepository.findById(menuItemId)
                .orElseThrow(() ->
                        new RuntimeException("Menu item not found with id: " + menuItemId));
    }
 
    @Override
    public void disableMenuItem(Long menuItemId) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() ->
                        new RuntimeException("Menu item not found with id: " + menuItemId));
 
        menuItem.setAvailable(false);
        menuItemRepository.save(menuItem);
    } 
    
    //new line added
    
    @Override
    public void deleteMenuItem(Long menuItemId) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() ->
                        new RuntimeException("Menu item not found with id: " + menuItemId));

        menuItemRepository.delete(menuItem);
    }
}
