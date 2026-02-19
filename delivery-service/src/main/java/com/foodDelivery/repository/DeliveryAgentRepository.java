package com.foodDelivery.repository;

import com.foodDelivery.entity.DeliveryAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryAgentRepository extends JpaRepository<DeliveryAgent, Long> {
    
    Optional<DeliveryAgent> findByEmail(String email);
    
    Optional<DeliveryAgent> findByPhoneNumber(String phoneNumber);
    
    boolean existsByUserId(Long userId);
    
    boolean existsByPhoneNumber(String phoneNumber);
    
    @Query("SELECT da FROM DeliveryAgent da WHERE da.status = 'AVAILABLE'")
    List<DeliveryAgent> findAvailableAgents();
    
    List<DeliveryAgent> findByStatus(DeliveryAgent.AgentStatus status);
}
