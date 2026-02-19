package com.foodDelivery.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodDelivery.entity.Delivery;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    
    Optional<Delivery> findByOrderId(Long orderId);
    
    List<Delivery> findByDeliveryAgentId(Long agentId);
    
    List<Delivery> findByCustomerId(Long customerId);
    
    List<Delivery> findByRestaurantId(Long restaurantId);
    
    List<Delivery> findByStatus(Delivery.DeliveryStatus status);
    
    List<Delivery> findByDeliveryAgentIdAndStatus(Long agentId, Delivery.DeliveryStatus status);
    
    boolean existsByOrderId(Long orderId);
}
