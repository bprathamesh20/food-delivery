package com.fooddelivery.restaurant.controller;

import com.fooddelivery.restaurant.entity.MenuItem;
import com.fooddelivery.restaurant.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
@RestController
@RequestMapping("/api/menus")
public class MenuController {
 
    @Autowired
    private MenuService menuService;
 
    /**
     * Add a menu item to a restaurant
     */
    @PostMapping("/{restaurantId}")
    public MenuItem addMenuItem(
            @PathVariable Long restaurantId,
            @RequestBody MenuItem menuItem) {
        return menuService.addMenuItem(restaurantId, menuItem);
    }
 
    /**
     * Get menu items by restaurant ID
     */
    @GetMapping("/{restaurantId}")
    public List<MenuItem> getMenuByRestaurant(
            @PathVariable Long restaurantId) {
        return menuService.getMenuByRestaurant(restaurantId);
    }
    
    /**
     * Get single menu item by ID
     */
    @GetMapping("/item/{menuItemId}")
    public MenuItem getMenuItemById(@PathVariable Long menuItemId) {
        return menuService.getMenuItemById(menuItemId);
    }
 
    /**
     * Disable menu item (mark unavailable)
     */
    @PutMapping("/item/{menuItemId}/disable")
    public String disableMenuItem(@PathVariable Long menuItemId) {
        menuService.disableMenuItem(menuItemId);
        return "Menu item disabled successfully";
    }
    
    //new line added 
    /**
     * Delete menu item permanently
     */
    @DeleteMapping("/item/{menuItemId}")
    public String deleteMenuItem(@PathVariable Long menuItemId) {
        menuService.deleteMenuItem(menuItemId);
        return "Menu item deleted successfully";
    }
}
