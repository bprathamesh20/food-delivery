package com.foodDelivery.controller;

import com.foodDelivery.dto.DeliveryRequest;
import com.foodDelivery.dto.DeliveryResponse;
import com.foodDelivery.dto.DeliveryTrackingResponse;
import com.foodDelivery.entity.Delivery;
import com.foodDelivery.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping
    public ResponseEntity<DeliveryResponse> createDelivery(@Valid @RequestBody DeliveryRequest request) {
        DeliveryResponse response = deliveryService.createDelivery(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{deliveryId}/assign")
    public ResponseEntity<DeliveryResponse> assignDelivery(
            @PathVariable Long deliveryId,
            @RequestBody Map<String, Long> requestBody) {
        Long agentId = requestBody.get("agentId");
        DeliveryResponse response = deliveryService.assignDelivery(deliveryId, agentId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{deliveryId}/status")
    public ResponseEntity<DeliveryResponse> updateDeliveryStatus(
            @PathVariable Long deliveryId,
            @RequestBody Map<String, String> requestBody) {
        String statusStr = requestBody.get("status");
        String remarks = requestBody.get("remarks");
        Delivery.DeliveryStatus status = Delivery.DeliveryStatus.valueOf(statusStr);
        DeliveryResponse response = deliveryService.updateDeliveryStatus(deliveryId, status, remarks);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponse> getDeliveryById(@PathVariable Long id) {
        DeliveryResponse response = deliveryService.getDeliveryById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryResponse> getDeliveryByOrderId(@PathVariable Long orderId) {
        DeliveryResponse response = deliveryService.getDeliveryByOrderId(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<DeliveryResponse>> getDeliveriesByAgent(@PathVariable Long agentId) {
        List<DeliveryResponse> deliveries = deliveryService.getDeliveriesByAgent(agentId);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<DeliveryResponse>> getDeliveriesByCustomer(@PathVariable Long customerId) {
        List<DeliveryResponse> deliveries = deliveryService.getDeliveriesByCustomer(customerId);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/{deliveryId}/tracking")
    public ResponseEntity<List<DeliveryTrackingResponse>> getDeliveryTracking(@PathVariable Long deliveryId) {
        List<DeliveryTrackingResponse> tracking = deliveryService.getDeliveryTracking(deliveryId);
        return ResponseEntity.ok(tracking);
    }

    @PostMapping("/{deliveryId}/location")
    public ResponseEntity<DeliveryTrackingResponse> updateDeliveryLocation(
            @PathVariable Long deliveryId,
            @RequestBody Map<String, Object> locationData) {
        Double latitude = ((Number) locationData.get("latitude")).doubleValue();
        Double longitude = ((Number) locationData.get("longitude")).doubleValue();
        String remarks = (String) locationData.get("remarks");
        DeliveryTrackingResponse response = deliveryService.updateDeliveryLocation(deliveryId, latitude, longitude, remarks);
        return ResponseEntity.ok(response);
    }
}
