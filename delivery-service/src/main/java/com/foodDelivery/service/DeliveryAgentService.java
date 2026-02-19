package com.foodDelivery.service;

import com.foodDelivery.dto.DeliveryAgentRequest;
import com.foodDelivery.dto.DeliveryAgentResponse;
import com.foodDelivery.entity.DeliveryAgent;
import com.foodDelivery.exception.ResourceNotFoundException;
import com.foodDelivery.repository.DeliveryAgentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeliveryAgentService {

    private final DeliveryAgentRepository deliveryAgentRepository;

    public DeliveryAgentService(DeliveryAgentRepository deliveryAgentRepository) {
        this.deliveryAgentRepository = deliveryAgentRepository;
    }

    @Transactional
    public DeliveryAgentResponse createAgent(DeliveryAgentRequest request) {
        if (deliveryAgentRepository.existsByUserId(request.getUserId())) {
            throw new IllegalArgumentException("Agent with userId " + request.getUserId() + " already exists");
        }

        if (deliveryAgentRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException(
                    "Agent with phone number " + request.getPhoneNumber() + " already exists");
        }

        DeliveryAgent agent = new DeliveryAgent();
        agent.setUserId(request.getUserId());
        agent.setName(request.getName());
        agent.setPhoneNumber(request.getPhoneNumber());
        agent.setEmail(request.getEmail());
        agent.setVehicleType(request.getVehicleType());
        agent.setVehicleNumber(request.getVehicleNumber());
        agent.setCurrentLatitude(request.getCurrentLatitude());
        agent.setCurrentLongitude(request.getCurrentLongitude());
        agent.setStatus(DeliveryAgent.AgentStatus.OFFLINE);

        DeliveryAgent savedAgent = deliveryAgentRepository.save(agent);
        return mapToResponse(savedAgent);
    }

    @Transactional(readOnly = true)
    public DeliveryAgentResponse getAgentById(Long id) {
        DeliveryAgent agent = deliveryAgentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery agent not found with id: " + id));
        return mapToResponse(agent);
    }

    @Transactional(readOnly = true)
    public DeliveryAgent getCurrentAgent(Long agentId) {
        return deliveryAgentRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found with id: " + agentId));
    }

    @Transactional(readOnly = true)
    public List<DeliveryAgentResponse> getAllAgents() {
        return deliveryAgentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DeliveryAgentResponse> getAvailableAgents() {
        return deliveryAgentRepository.findAvailableAgents().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DeliveryAgentResponse updateAgentStatus(Long id, DeliveryAgent.AgentStatus status) {
        DeliveryAgent agent = deliveryAgentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery agent not found with id: " + id));

        agent.setStatus(status);
        agent.setLastActiveAt(LocalDateTime.now());
        DeliveryAgent updatedAgent = deliveryAgentRepository.save(agent);
        return mapToResponse(updatedAgent);
    }

    @Transactional
    public DeliveryAgentResponse updateAgentLocation(Long id, Double latitude, Double longitude) {
        DeliveryAgent agent = deliveryAgentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery agent not found with id: " + id));

        agent.setCurrentLatitude(latitude);
        agent.setCurrentLongitude(longitude);
        agent.setLastActiveAt(LocalDateTime.now());
        DeliveryAgent updatedAgent = deliveryAgentRepository.save(agent);
        return mapToResponse(updatedAgent);
    }

    @Transactional
    public void deleteAgent(Long id) {
        if (!deliveryAgentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Delivery agent not found with id: " + id);
        }
        deliveryAgentRepository.deleteById(id);
    }

    private DeliveryAgentResponse mapToResponse(DeliveryAgent agent) {
        DeliveryAgentResponse r = new DeliveryAgentResponse();
        r.setId(agent.getId());
        r.setUserId(agent.getUserId());
        r.setName(agent.getName());
        r.setPhoneNumber(agent.getPhoneNumber());
        r.setEmail(agent.getEmail());
        r.setVehicleType(agent.getVehicleType());
        r.setVehicleNumber(agent.getVehicleNumber());
        r.setCurrentLatitude(agent.getCurrentLatitude());
        r.setCurrentLongitude(agent.getCurrentLongitude());
        r.setStatus(agent.getStatus());
        r.setRating(agent.getRating());
        r.setTotalDeliveries(agent.getTotalDeliveries());
        r.setCreatedAt(agent.getCreatedAt());
        r.setUpdatedAt(agent.getUpdatedAt());
        return r;
    }
    
    
}
