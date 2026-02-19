//package com.foodDelivery.service;
//
//import com.foodDelivery.dto.DeliveryRequest;
//import com.foodDelivery.dto.DeliveryResponse;
//import com.foodDelivery.dto.DeliveryTrackingResponse;
//import com.foodDelivery.entity.Delivery;
//import com.foodDelivery.entity.DeliveryAgent;
//import com.foodDelivery.entity.DeliveryTracking;
//import com.foodDelivery.event.DeliveryEvent;
//import com.foodDelivery.exception.ResourceNotFoundException;
//import com.foodDelivery.kafka.KafkaProducerService;
//import com.foodDelivery.repository.DeliveryAgentRepository;
//import com.foodDelivery.repository.DeliveryRepository;
//import com.foodDelivery.repository.DeliveryTrackingRepository;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class DeliveryService {
//
//    private final DeliveryRepository deliveryRepository;
//    private final DeliveryAgentRepository deliveryAgentRepository;
//    private final DeliveryTrackingRepository trackingRepository;
//    private final KafkaProducerService kafkaProducerService;
//
//    public DeliveryService(DeliveryRepository deliveryRepository,
//                          DeliveryAgentRepository deliveryAgentRepository,
//                          DeliveryTrackingRepository trackingRepository,
//                          KafkaProducerService kafkaProducerService) {
//        this.deliveryRepository = deliveryRepository;
//        this.deliveryAgentRepository = deliveryAgentRepository;
//        this.trackingRepository = trackingRepository;
//        this.kafkaProducerService = kafkaProducerService;
//    }
//
//    @Transactional
//    public DeliveryResponse createDelivery(DeliveryRequest request) {
//        if (deliveryRepository.existsByOrderId(request.getOrderId())) {
//            throw new IllegalArgumentException("Delivery already exists for order: " + request.getOrderId());
//        }
//
//        Delivery delivery = new Delivery();
//        delivery.setOrderId(request.getOrderId());
//        delivery.setRestaurantId(request.getRestaurantId());
//        delivery.setCustomerId(request.getCustomerId());
//        delivery.setPickupAddress(request.getPickupAddress());
//        delivery.setPickupLatitude(request.getPickupLatitude());
//        delivery.setPickupLongitude(request.getPickupLongitude());
//        delivery.setDeliveryAddress(request.getDeliveryAddress());
//        delivery.setDeliveryLatitude(request.getDeliveryLatitude());
//        delivery.setDeliveryLongitude(request.getDeliveryLongitude());
//        delivery.setDeliveryInstructions(request.getDeliveryInstructions());
//        delivery.setDeliveryFee(request.getDeliveryFee());
//        delivery.setStatus(Delivery.DeliveryStatus.PENDING);
//
//        Delivery savedDelivery = deliveryRepository.save(delivery);
//        return mapToResponse(savedDelivery);
//    }
//
//    @Transactional
//    public DeliveryResponse assignDelivery(Long deliveryId, Long agentId) {
//        Delivery delivery = deliveryRepository.findById(deliveryId)
//                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));
//
//        if (delivery.getStatus() != Delivery.DeliveryStatus.PENDING) {
//            throw new IllegalArgumentException("Delivery is not in PENDING status");
//        }
//
//        DeliveryAgent agent = deliveryAgentRepository.findById(agentId)
//                .orElseThrow(() -> new ResourceNotFoundException("Delivery agent not found with id: " + agentId));
//
//        if (agent.getStatus() != DeliveryAgent.AgentStatus.AVAILABLE) {
//            throw new IllegalArgumentException("Agent is not available");
//        }
//
//        delivery.setDeliveryAgent(agent);
//        delivery.setStatus(Delivery.DeliveryStatus.ASSIGNED);
//        delivery.setAssignedAt(LocalDateTime.now());
//        delivery.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(30));
//
//        agent.setStatus(DeliveryAgent.AgentStatus.BUSY);
//        deliveryAgentRepository.save(agent);
//
//        Delivery updatedDelivery = deliveryRepository.save(delivery);
//        addTrackingUpdate(delivery, Delivery.DeliveryStatus.ASSIGNED, "Delivery assigned to agent");
//        
//        // Publish Kafka event
//        DeliveryEvent event = createDeliveryEvent(updatedDelivery, "DELIVERY_ASSIGNED");
//        kafkaProducerService.sendDeliveryEvent(event);
//        kafkaProducerService.sendNotificationEvent(event);
//        
//        return mapToResponse(updatedDelivery);
//    }
//
//    @Transactional
//    public DeliveryResponse updateDeliveryStatus(Long deliveryId, Delivery.DeliveryStatus newStatus, String remarks) {
//        Delivery delivery = deliveryRepository.findById(deliveryId)
//                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));
//
//        delivery.setStatus(newStatus);
//
//        switch (newStatus) {
//            case PICKED_UP:
//                delivery.setPickedUpAt(LocalDateTime.now());
//                break;
//            case DELIVERED:
//                delivery.setDeliveredAt(LocalDateTime.now());
//                if (delivery.getDeliveryAgent() != null) {
//                    DeliveryAgent agent = delivery.getDeliveryAgent();
//                    agent.setStatus(DeliveryAgent.AgentStatus.AVAILABLE);
//                    agent.setTotalDeliveries(agent.getTotalDeliveries() + 1);
//                    deliveryAgentRepository.save(agent);
//                }
//                break;
//            case CANCELLED:
//            case FAILED:
//                if (delivery.getDeliveryAgent() != null) {
//                    DeliveryAgent agent = delivery.getDeliveryAgent();
//                    agent.setStatus(DeliveryAgent.AgentStatus.AVAILABLE);
//                    deliveryAgentRepository.save(agent);
//                }
//                delivery.setCancellationReason(remarks);
//                break;
//        }
//
//        Delivery updatedDelivery = deliveryRepository.save(delivery);
//        addTrackingUpdate(delivery, newStatus, remarks);
//        
//        // Publish Kafka event
//        String eventType = "DELIVERY_" + newStatus.name();
//        DeliveryEvent event = createDeliveryEvent(updatedDelivery, eventType);
//        event.setRemarks(remarks);
//        kafkaProducerService.sendDeliveryEvent(event);
//        kafkaProducerService.sendNotificationEvent(event);
//        
//        return mapToResponse(updatedDelivery);
//    }
//
//    @Transactional(readOnly = true)
//    public DeliveryResponse getDeliveryById(Long id) {
//        Delivery delivery = deliveryRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + id));
//        return mapToResponse(delivery);
//    }
//
//    @Transactional(readOnly = true)
//    public DeliveryResponse getDeliveryByOrderId(Long orderId) {
//        Delivery delivery = deliveryRepository.findByOrderId(orderId)
//                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found for order: " + orderId));
//        return mapToResponse(delivery);
//    }
//
//    @Transactional(readOnly = true)
//    public List<DeliveryResponse> getDeliveriesByAgent(Long agentId) {
//        return deliveryRepository.findByDeliveryAgentId(agentId).stream()
//                .map(this::mapToResponse)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional(readOnly = true)
//    public List<DeliveryResponse> getDeliveriesByCustomer(Long customerId) {
//        return deliveryRepository.findByCustomerId(customerId).stream()
//                .map(this::mapToResponse)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional(readOnly = true)
//    public List<DeliveryTrackingResponse> getDeliveryTracking(Long deliveryId) {
//        if (!deliveryRepository.existsById(deliveryId)) {
//            throw new ResourceNotFoundException("Delivery not found with id: " + deliveryId);
//        }
//        return trackingRepository.findByDeliveryIdOrderByTimestampDesc(deliveryId).stream()
//                .map(this::mapToTrackingResponse)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional
//    public DeliveryResponse updateDeliveryLocation(Long deliveryId, Double latitude, Double longitude, String remarks) {
//        Delivery delivery = deliveryRepository.findById(deliveryId)
//                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));
//
//        DeliveryTracking tracking = new DeliveryTracking();
//        tracking.setDelivery(delivery);
//        tracking.setLatitude(latitude);
//        tracking.setLongitude(longitude);
//        tracking.setStatusUpdate(delivery.getStatus());
//        tracking.setRemarks(remarks);
//        trackingRepository.save(tracking);
//
//        return mapToResponse(delivery);
//    }
//
//    private void addTrackingUpdate(Delivery delivery, Delivery.DeliveryStatus status, String remarks) {
//        DeliveryTracking tracking = new DeliveryTracking();
//        tracking.setDelivery(delivery);
//        tracking.setStatusUpdate(status);
//        tracking.setRemarks(remarks);
//        
//        if (delivery.getDeliveryAgent() != null) {
//            tracking.setLatitude(delivery.getDeliveryAgent().getCurrentLatitude());
//            tracking.setLongitude(delivery.getDeliveryAgent().getCurrentLongitude());
//        }
//        
//        trackingRepository.save(tracking);
//    }
//
//    private DeliveryResponse mapToResponse(Delivery delivery) {
//        DeliveryResponse response = new DeliveryResponse();
//        response.setId(delivery.getId());
//        response.setOrderId(delivery.getOrderId());
//        response.setRestaurantId(delivery.getRestaurantId());
//        response.setCustomerId(delivery.getCustomerId());
//        response.setPickupAddress(delivery.getPickupAddress());
//        response.setPickupLatitude(delivery.getPickupLatitude());
//        response.setPickupLongitude(delivery.getPickupLongitude());
//        response.setDeliveryAddress(delivery.getDeliveryAddress());
//        response.setDeliveryLatitude(delivery.getDeliveryLatitude());
//        response.setDeliveryLongitude(delivery.getDeliveryLongitude());
//        response.setStatus(delivery.getStatus());
//        response.setAssignedAt(delivery.getAssignedAt());
//        response.setPickedUpAt(delivery.getPickedUpAt());
//        response.setDeliveredAt(delivery.getDeliveredAt());
//        response.setEstimatedDeliveryTime(delivery.getEstimatedDeliveryTime());
//        response.setDeliveryInstructions(delivery.getDeliveryInstructions());
//        response.setDeliveryFee(delivery.getDeliveryFee());
//        response.setCancellationReason(delivery.getCancellationReason());
//        response.setCreatedAt(delivery.getCreatedAt());
//        response.setUpdatedAt(delivery.getUpdatedAt());
//
//        if (delivery.getDeliveryAgent() != null) {
//            response.setDeliveryAgentId(delivery.getDeliveryAgent().getId());
//            response.setDeliveryAgentName(delivery.getDeliveryAgent().getName());
//            response.setDeliveryAgentPhone(delivery.getDeliveryAgent().getPhoneNumber());
//        }
//
//        return response;
//    }
//
//    private DeliveryTrackingResponse mapToTrackingResponse(DeliveryTracking tracking) {
//        DeliveryTrackingResponse response = new DeliveryTrackingResponse();
//        response.setId(tracking.getId());
//        response.setDeliveryId(tracking.getDelivery().getId());
//        response.setLatitude(tracking.getLatitude());
//        response.setLongitude(tracking.getLongitude());
//        response.setStatusUpdate(tracking.getStatusUpdate());
//        response.setRemarks(tracking.getRemarks());
//        response.setTimestamp(tracking.getTimestamp());
//        return response;
//    }
//    
//    private DeliveryEvent createDeliveryEvent(Delivery delivery, String eventType) {
//        DeliveryEvent event = new DeliveryEvent(
//            eventType, 
//            delivery.getId(), 
//            delivery.getOrderId(), 
//            delivery.getStatus().name()
//        );
//        
//        if (delivery.getDeliveryAgent() != null) {
//            event.setDeliveryAgentId(delivery.getDeliveryAgent().getId());
//            event.setDeliveryAgentName(delivery.getDeliveryAgent().getName());
//            event.setDeliveryAgentPhone(delivery.getDeliveryAgent().getPhoneNumber());
//            event.setCurrentLatitude(delivery.getDeliveryAgent().getCurrentLatitude());
//            event.setCurrentLongitude(delivery.getDeliveryAgent().getCurrentLongitude());
//        }
//        
//        event.setEstimatedDeliveryTime(delivery.getEstimatedDeliveryTime());
//        return event;
//    }
//
//}

package com.foodDelivery.service;

import com.foodDelivery.dto.DeliveryRequest;
import com.foodDelivery.dto.DeliveryResponse;
import com.foodDelivery.dto.DeliveryTrackingResponse;
import com.foodDelivery.entity.Delivery;
import com.foodDelivery.entity.DeliveryAgent;
import com.foodDelivery.entity.DeliveryTracking;
import com.fooddelivery.events.DeliveryEvent;
import com.foodDelivery.exception.ResourceNotFoundException;
import com.foodDelivery.kafka.KafkaProducerService;
import com.foodDelivery.repository.DeliveryAgentRepository;
import com.foodDelivery.repository.DeliveryRepository;
import com.foodDelivery.repository.DeliveryTrackingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeliveryService {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryService.class);

    private final DeliveryRepository deliveryRepository;
    private final DeliveryAgentRepository deliveryAgentRepository;
    private final DeliveryTrackingRepository trackingRepository;
    private final KafkaProducerService kafkaProducerService;
    private final DeliveryAssignmentService assignmentService;

    public DeliveryService(DeliveryRepository deliveryRepository,
                          DeliveryAgentRepository deliveryAgentRepository,
                          DeliveryTrackingRepository trackingRepository,
                          KafkaProducerService kafkaProducerService,
                          DeliveryAssignmentService assignmentService) {
        this.deliveryRepository = deliveryRepository;
        this.deliveryAgentRepository = deliveryAgentRepository;
        this.trackingRepository = trackingRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.assignmentService = assignmentService;
    }

    @Transactional
    public DeliveryResponse createDelivery(DeliveryRequest request) {
        if (deliveryRepository.existsByOrderId(request.getOrderId())) {
            throw new IllegalArgumentException("Delivery already exists for order: " + request.getOrderId());
        }

        Delivery delivery = new Delivery();
        delivery.setOrderId(request.getOrderId());
        delivery.setRestaurantId(request.getRestaurantId());
        delivery.setCustomerId(request.getCustomerId());
        delivery.setPickupAddress(request.getPickupAddress());
        delivery.setPickupLatitude(request.getPickupLatitude());
        delivery.setPickupLongitude(request.getPickupLongitude());
        delivery.setDeliveryAddress(request.getDeliveryAddress());
        delivery.setDeliveryLatitude(request.getDeliveryLatitude());
        delivery.setDeliveryLongitude(request.getDeliveryLongitude());
        delivery.setDeliveryInstructions(request.getDeliveryInstructions());
        delivery.setDeliveryFee(request.getDeliveryFee());
        delivery.setStatus(Delivery.DeliveryStatus.PENDING);

        Delivery savedDelivery = deliveryRepository.save(delivery);
        
        // ✅ AUTO-ASSIGN to nearest available agent
        try {
            logger.info("Attempting to auto-assign delivery {} to available agent", savedDelivery.getId());
            Optional<DeliveryAgent> agentOpt = assignmentService.findBestAvailableAgent(savedDelivery);
            
            if (agentOpt.isPresent()) {
                DeliveryAgent agent = agentOpt.get();
                
                savedDelivery.setDeliveryAgent(agent);
                savedDelivery.setStatus(Delivery.DeliveryStatus.ASSIGNED);
                savedDelivery.setAssignedAt(LocalDateTime.now());
                savedDelivery.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(30));
                
                // Update agent status to BUSY
                agent.setStatus(DeliveryAgent.AgentStatus.BUSY);
                deliveryAgentRepository.save(agent);
                
                savedDelivery = deliveryRepository.save(savedDelivery);
                
                // Add tracking update
                addTrackingUpdate(savedDelivery, Delivery.DeliveryStatus.ASSIGNED, "Delivery assigned to agent");
                
                // Publish Kafka event
                DeliveryEvent event = createDeliveryEvent(savedDelivery, "DELIVERY_ASSIGNED");
                kafkaProducerService.sendDeliveryEvent(event);
                kafkaProducerService.sendNotificationEvent(event);
                
                logger.info("✅ Auto-assigned delivery {} to agent {}", savedDelivery.getId(), agent.getName());
            } else {
                logger.warn("⚠️ No available agents for delivery {}", savedDelivery.getId());
            }
        } catch (Exception e) {
            logger.error("❌ Auto-assignment failed for delivery {}: {}", savedDelivery.getId(), e.getMessage(), e);
        }
        
        return mapToResponse(savedDelivery);
    }

    @Transactional
    public DeliveryResponse assignDelivery(Long deliveryId, Long agentId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));

        if (delivery.getStatus() != Delivery.DeliveryStatus.PENDING) {
            throw new IllegalArgumentException("Delivery is not in PENDING status");
        }

        DeliveryAgent agent = deliveryAgentRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery agent not found with id: " + agentId));

        if (agent.getStatus() != DeliveryAgent.AgentStatus.AVAILABLE) {
            throw new IllegalArgumentException("Agent is not available");
        }

        delivery.setDeliveryAgent(agent);
        delivery.setStatus(Delivery.DeliveryStatus.ASSIGNED);
        delivery.setAssignedAt(LocalDateTime.now());
        delivery.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(30));

        agent.setStatus(DeliveryAgent.AgentStatus.BUSY);
        deliveryAgentRepository.save(agent);

        Delivery updatedDelivery = deliveryRepository.save(delivery);
        addTrackingUpdate(delivery, Delivery.DeliveryStatus.ASSIGNED, "Delivery assigned to agent");
        
        // Publish Kafka event
        DeliveryEvent event = createDeliveryEvent(updatedDelivery, "DELIVERY_ASSIGNED");
        kafkaProducerService.sendDeliveryEvent(event);
        kafkaProducerService.sendNotificationEvent(event);
        
        return mapToResponse(updatedDelivery);
    }

    @Transactional
    public DeliveryResponse updateDeliveryStatus(Long deliveryId, Delivery.DeliveryStatus newStatus, String remarks) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));

        delivery.setStatus(newStatus);

        switch (newStatus) {
            case PICKED_UP:
                delivery.setPickedUpAt(LocalDateTime.now());
                break;
            case DELIVERED:
                delivery.setDeliveredAt(LocalDateTime.now());
                if (delivery.getDeliveryAgent() != null) {
                    DeliveryAgent agent = delivery.getDeliveryAgent();
                    agent.setStatus(DeliveryAgent.AgentStatus.AVAILABLE);
                    agent.setTotalDeliveries(agent.getTotalDeliveries() + 1);
                    deliveryAgentRepository.save(agent);
                }
                break;
            case CANCELLED:
            case FAILED:
                if (delivery.getDeliveryAgent() != null) {
                    DeliveryAgent agent = delivery.getDeliveryAgent();
                    agent.setStatus(DeliveryAgent.AgentStatus.AVAILABLE);
                    deliveryAgentRepository.save(agent);
                }
                delivery.setCancellationReason(remarks);
                break;
        }

        Delivery updatedDelivery = deliveryRepository.save(delivery);
        addTrackingUpdate(delivery, newStatus, remarks);
        
        // Publish Kafka event
        String eventType = "DELIVERY_" + newStatus.name();
        DeliveryEvent event = createDeliveryEvent(updatedDelivery, eventType);
        event.setNotes(remarks);
        kafkaProducerService.sendDeliveryEvent(event);
        kafkaProducerService.sendNotificationEvent(event);
        
        return mapToResponse(updatedDelivery);
    }

    @Transactional(readOnly = true)
    public DeliveryResponse getDeliveryById(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + id));
        return mapToResponse(delivery);
    }
    
    @Transactional(readOnly = true)
    public Delivery getDeliveryEntityById(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public DeliveryResponse getDeliveryByOrderId(Long orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found for order: " + orderId));
        return mapToResponse(delivery);
    }
    
    @Transactional(readOnly = true)
    public Optional<Delivery> findByOrderId(Long orderId) {
        return deliveryRepository.findByOrderId(orderId);
    }

    @Transactional(readOnly = true)
    public List<DeliveryResponse> getDeliveriesByAgent(Long agentId) {
        return deliveryRepository.findByDeliveryAgentId(agentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DeliveryResponse> getDeliveriesByCustomer(Long customerId) {
        return deliveryRepository.findByCustomerId(customerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DeliveryTrackingResponse> getDeliveryTracking(Long deliveryId) {
        if (!deliveryRepository.existsById(deliveryId)) {
            throw new ResourceNotFoundException("Delivery not found with id: " + deliveryId);
        }
        return trackingRepository.findByDeliveryIdOrderByTimestampDesc(deliveryId).stream()
                .map(this::mapToTrackingResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DeliveryTrackingResponse updateDeliveryLocation(Long deliveryId, Double latitude, Double longitude, String remarks) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));

        DeliveryTracking tracking = new DeliveryTracking();
        tracking.setDelivery(delivery);
        tracking.setLatitude(latitude);
        tracking.setLongitude(longitude);
        tracking.setStatusUpdate(delivery.getStatus());
        tracking.setRemarks(remarks);
        DeliveryTracking savedTracking = trackingRepository.save(tracking);

        return mapToTrackingResponse(savedTracking);
    }
    
    @Transactional
    public void cancelDeliveryByOrderId(Long orderId, String reason) {
        Optional<Delivery> deliveryOpt = deliveryRepository.findByOrderId(orderId);
        if (deliveryOpt.isPresent()) {
            Delivery delivery = deliveryOpt.get();
            delivery.setStatus(Delivery.DeliveryStatus.CANCELLED);
            delivery.setCancellationReason(reason);
            
            // If agent was assigned, make them available again
            if (delivery.getDeliveryAgent() != null) {
                DeliveryAgent agent = delivery.getDeliveryAgent();
                agent.setStatus(DeliveryAgent.AgentStatus.AVAILABLE);
                deliveryAgentRepository.save(agent);
            }
            
            deliveryRepository.save(delivery);
            
            // Add tracking update
            addTrackingUpdate(delivery, Delivery.DeliveryStatus.CANCELLED, reason);
            
            // Publish Kafka event
            DeliveryEvent event = createDeliveryEvent(delivery, "DELIVERY_CANCELLED");
            event.setNotes(reason);
            kafkaProducerService.sendDeliveryEvent(event);
            kafkaProducerService.sendNotificationEvent(event);
        }
    }

    private void addTrackingUpdate(Delivery delivery, Delivery.DeliveryStatus status, String remarks) {
        DeliveryTracking tracking = new DeliveryTracking();
        tracking.setDelivery(delivery);
        tracking.setStatusUpdate(status);
        tracking.setRemarks(remarks);
        
        // Set coordinates - use agent's location if available, otherwise use pickup location
        Double latitude = null;
        Double longitude = null;
        
        if (delivery.getDeliveryAgent() != null) {
            latitude = delivery.getDeliveryAgent().getCurrentLatitude();
            longitude = delivery.getDeliveryAgent().getCurrentLongitude();
        }
        
        // If agent coordinates are null, use pickup location as fallback
        if (latitude == null || longitude == null) {
            latitude = delivery.getPickupLatitude();
            longitude = delivery.getPickupLongitude();
        }
        
        // If still null, use default coordinates (Pune, India)
        if (latitude == null || longitude == null) {
            latitude = 18.5204;
            longitude = 73.8567;
            logger.warn("Using default coordinates for tracking update - delivery: {}", delivery.getId());
        }
        
        tracking.setLatitude(latitude);
        tracking.setLongitude(longitude);
        
        trackingRepository.save(tracking);
    }

    private DeliveryResponse mapToResponse(Delivery delivery) {
        DeliveryResponse response = new DeliveryResponse();
        response.setId(delivery.getId());
        response.setOrderId(delivery.getOrderId());
        response.setRestaurantId(delivery.getRestaurantId());
        response.setCustomerId(delivery.getCustomerId());
        response.setPickupAddress(delivery.getPickupAddress());
        response.setPickupLatitude(delivery.getPickupLatitude());
        response.setPickupLongitude(delivery.getPickupLongitude());
        response.setDeliveryAddress(delivery.getDeliveryAddress());
        response.setDeliveryLatitude(delivery.getDeliveryLatitude());
        response.setDeliveryLongitude(delivery.getDeliveryLongitude());
        response.setStatus(delivery.getStatus());
        response.setAssignedAt(delivery.getAssignedAt());
        response.setPickedUpAt(delivery.getPickedUpAt());
        response.setDeliveredAt(delivery.getDeliveredAt());
        response.setEstimatedDeliveryTime(delivery.getEstimatedDeliveryTime());
        response.setDeliveryInstructions(delivery.getDeliveryInstructions());
        response.setDeliveryFee(delivery.getDeliveryFee());
        response.setCancellationReason(delivery.getCancellationReason());
        response.setCreatedAt(delivery.getCreatedAt());
        response.setUpdatedAt(delivery.getUpdatedAt());

        if (delivery.getDeliveryAgent() != null) {
            response.setDeliveryAgentId(delivery.getDeliveryAgent().getId());
            response.setDeliveryAgentName(delivery.getDeliveryAgent().getName());
            response.setDeliveryAgentPhone(delivery.getDeliveryAgent().getPhoneNumber());
        }

        return response;
    }

    private DeliveryTrackingResponse mapToTrackingResponse(DeliveryTracking tracking) {
        DeliveryTrackingResponse response = new DeliveryTrackingResponse();
        response.setId(tracking.getId());
        response.setDeliveryId(tracking.getDelivery().getId());
        response.setLatitude(tracking.getLatitude());
        response.setLongitude(tracking.getLongitude());
        response.setStatusUpdate(tracking.getStatusUpdate());
        response.setRemarks(tracking.getRemarks());
        response.setTimestamp(tracking.getTimestamp());
        return response;
    }
    
    private DeliveryEvent createDeliveryEvent(Delivery delivery, String eventType) {
        DeliveryEvent event = new DeliveryEvent(
            eventType, 
            delivery.getId(), 
            delivery.getOrderId(), 
            delivery.getStatus().name()
        );
        
        if (delivery.getDeliveryAgent() != null) {
            event.setDeliveryAgentId(delivery.getDeliveryAgent().getId());
            event.setDeliveryAgentName(delivery.getDeliveryAgent().getName());
            event.setDeliveryAgentPhone(delivery.getDeliveryAgent().getPhoneNumber());
            event.setCurrentLatitude(delivery.getDeliveryAgent().getCurrentLatitude());
            event.setCurrentLongitude(delivery.getDeliveryAgent().getCurrentLongitude());
        }
        
        // Convert LocalDateTime to String for Kafka serialization
        if (delivery.getEstimatedDeliveryTime() != null) {
            event.setEstimatedDeliveryTime(delivery.getEstimatedDeliveryTime().toString());
        }
        return event;
    }
}

