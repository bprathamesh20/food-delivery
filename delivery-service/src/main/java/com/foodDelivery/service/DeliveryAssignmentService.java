package com.foodDelivery.service;

import com.foodDelivery.entity.Delivery;
import com.foodDelivery.entity.DeliveryAgent;
import com.foodDelivery.repository.DeliveryAgentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class DeliveryAssignmentService {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryAssignmentService.class);

    private final DeliveryAgentRepository agentRepository;

    public DeliveryAssignmentService(DeliveryAgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    /**
     * Find best available agent based on proximity
     */
    public Optional<DeliveryAgent> findBestAvailableAgent(Delivery delivery) {
        // Get all available agents
        List<DeliveryAgent> availableAgents = agentRepository.findByStatus(DeliveryAgent.AgentStatus.AVAILABLE);

        logger.info("Found {} available agents for delivery assignment", availableAgents.size());

        if (availableAgents.isEmpty()) {
            logger.warn("No available agents found for delivery assignment");
            return Optional.empty();
        }

        // Find nearest agent using Haversine formula
        DeliveryAgent nearestAgent = availableAgents.stream()
                .min(Comparator.comparingDouble(agent -> 
                    calculateDistance(
                        delivery.getPickupLatitude(), 
                        delivery.getPickupLongitude(),
                        agent.getCurrentLatitude(), 
                        agent.getCurrentLongitude()
                    )
                ))
                .orElse(null);

        if (nearestAgent != null) {
            double distance = calculateDistance(
                delivery.getPickupLatitude(), 
                delivery.getPickupLongitude(),
                nearestAgent.getCurrentLatitude(), 
                nearestAgent.getCurrentLongitude()
            );
            logger.info("Found best agent: {} (distance: {} km)", nearestAgent.getName(), String.format("%.2f", distance));
        }

        return Optional.ofNullable(nearestAgent);
    }

    /**
     * Calculate distance between two coordinates using Haversine formula
     * Returns distance in kilometers
     */
    private double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            logger.warn("Missing coordinates for distance calculation");
            return Double.MAX_VALUE;
        }

        final int R = 6371; // Radius of the earth in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return distance;
    }
}
