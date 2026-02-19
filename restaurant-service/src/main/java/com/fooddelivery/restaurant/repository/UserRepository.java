package com.fooddelivery.restaurant.repository;



import java.util.Optional;
 
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.fooddelivery.restaurant.entity.User;
 
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}