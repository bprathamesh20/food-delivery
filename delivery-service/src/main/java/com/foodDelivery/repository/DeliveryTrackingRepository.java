package com.foodDelivery.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodDelivery.entity.DeliveryTracking;

import java.util.List;

@Repository
public interface DeliveryTrackingRepository extends JpaRepository<DeliveryTracking, Long> {
    
    List<DeliveryTracking> findByDeliveryIdOrderByTimestampDesc(Long deliveryId);
    
    List<DeliveryTracking> findByDeliveryId(Long deliveryId);
}
