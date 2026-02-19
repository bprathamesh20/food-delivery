package com.foodDelivery.controller;

import com.foodDelivery.dto.DeliveryAgentResponse;
import com.foodDelivery.dto.UpdateLocationRequest;
import com.foodDelivery.dto.UpdateStatusRequest;
import com.foodDelivery.entity.DeliveryAgent;
import com.foodDelivery.service.DeliveryAgentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agents")
@CrossOrigin(origins = "*")
public class DeliveryAgentController {

    private final DeliveryAgentService agentService;

    public DeliveryAgentController(DeliveryAgentService agentService) {
        this.agentService = agentService;
    }

    // Get current agent's profile
    @GetMapping("/me")
    public ResponseEntity<DeliveryAgent> getMyProfile(HttpServletRequest request) {
        Long agentId = (Long) request.getAttribute("userId");
        DeliveryAgent agent = agentService.getCurrentAgent(agentId);
        // Don't send password to frontend
        agent.setPassword(null);
        return ResponseEntity.ok(agent);
    }

    // Update agent status
    @PutMapping("/me/status")
    public ResponseEntity<DeliveryAgentResponse> updateMyStatus(
            HttpServletRequest request,
            @RequestBody UpdateStatusRequest statusRequest) {
        Long agentId = (Long) request.getAttribute("userId");
        DeliveryAgentResponse response = agentService.updateAgentStatus(agentId, statusRequest.getStatus());
        return ResponseEntity.ok(response);
    }

    // Update agent location
    @PutMapping("/me/location")
    public ResponseEntity<DeliveryAgentResponse> updateMyLocation(
            HttpServletRequest request,
            @RequestBody UpdateLocationRequest locationRequest) {
        Long agentId = (Long) request.getAttribute("userId");
        DeliveryAgentResponse response = agentService.updateAgentLocation(
                agentId,
                locationRequest.getLatitude(),
                locationRequest.getLongitude()
        );
        return ResponseEntity.ok(response);
    }

    // Get all agents (for admin)
    @GetMapping
    public ResponseEntity<List<DeliveryAgentResponse>> getAllAgents() {
        List<DeliveryAgentResponse> agents = agentService.getAllAgents();
        return ResponseEntity.ok(agents);
    }

    // Get available agents
    @GetMapping("/available")
    public ResponseEntity<List<DeliveryAgentResponse>> getAvailableAgents() {
        List<DeliveryAgentResponse> agents = agentService.getAvailableAgents();
        return ResponseEntity.ok(agents);
    }

    // Get agent by ID (for admin)
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryAgentResponse> getAgentById(@PathVariable Long id) {
        DeliveryAgentResponse agent = agentService.getAgentById(id);
        return ResponseEntity.ok(agent);
    }

    // Update agent status by ID (for admin)
    @PutMapping("/{id}/status")
    public ResponseEntity<DeliveryAgentResponse> updateAgentStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest statusRequest) {
        DeliveryAgentResponse response = agentService.updateAgentStatus(id, statusRequest.getStatus());
        return ResponseEntity.ok(response);
    }
}
