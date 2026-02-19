package com.foodDelivery.service;

import com.foodDelivery.dto.AuthRequest;
import com.foodDelivery.dto.AuthResponse;
import com.foodDelivery.dto.RegisterAgentRequest;
import com.foodDelivery.entity.DeliveryAgent;
import com.foodDelivery.repository.DeliveryAgentRepository;
import com.foodDelivery.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthService {

    private final DeliveryAgentRepository agentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(DeliveryAgentRepository agentRepository,
                      PasswordEncoder passwordEncoder,
                      JwtUtil jwtUtil) {
        this.agentRepository = agentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterAgentRequest request) {
        // Check if email already exists
        if (agentRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Check if phone number already exists
        if (agentRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new RuntimeException("Phone number already registered");
        }

        // Create new agent
        DeliveryAgent agent = new DeliveryAgent();
        agent.setName(request.getName());
        agent.setEmail(request.getEmail());
        agent.setPassword(passwordEncoder.encode(request.getPassword()));
        agent.setPhoneNumber(request.getPhoneNumber());
        agent.setVehicleType(request.getVehicleType());
        agent.setVehicleNumber(request.getVehicleNumber());
        agent.setAddress(request.getAddress());
        agent.setCity(request.getCity());
        agent.setState(request.getState());
        agent.setLicenseNumber(request.getLicenseNumber());
        
        // Set coordinates - use provided or default to Pune, India
        agent.setCurrentLatitude(request.getCurrentLatitude() != null ? request.getCurrentLatitude() : 18.5204);
        agent.setCurrentLongitude(request.getCurrentLongitude() != null ? request.getCurrentLongitude() : 73.8567);
        
        agent.setStatus(DeliveryAgent.AgentStatus.AVAILABLE); // Set to AVAILABLE so they can receive deliveries
        agent.setIsActive(true);
        agent.setIsVerified(false);

        agent = agentRepository.save(agent);

        // Generate JWT token
        String token = jwtUtil.generateToken(
                agent.getEmail(),
                agent.getId(),
                agent.getName(),
                List.of("DELIVERY_AGENT")
        );

        return new AuthResponse(token, agent.getEmail(), agent.getName(), agent.getId(), "DELIVERY_AGENT");
    }

    public AuthResponse login(AuthRequest request) {
        DeliveryAgent agent = agentRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!agent.getIsActive()) {
            throw new RuntimeException("Account is deactivated. Please contact admin.");
        }

        if (!passwordEncoder.matches(request.getPassword(), agent.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Update last active time
        agent.setLastActiveAt(LocalDateTime.now());
        agentRepository.save(agent);

        // Generate JWT token
        String token = jwtUtil.generateToken(
                agent.getEmail(),
                agent.getId(),
                agent.getName(),
                List.of("DELIVERY_AGENT")
        );

        return new AuthResponse(token, agent.getEmail(), agent.getName(), agent.getId(), "DELIVERY_AGENT");
    }
}
