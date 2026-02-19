package com.fooddelivery.restaurant.repository;



import com.fooddelivery.restaurant.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
 
    // Get menu items by restaurant
    List<MenuItem> findByRestaurantId(Long restaurantId);
 
    // Get only available menu items of a restaurant
    List<MenuItem> findByRestaurantIdAndAvailableTrue(Long restaurantId);
}
