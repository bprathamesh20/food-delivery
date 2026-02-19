package com.fooddelivery.auth.service;

import com.fooddelivery.auth.dto.AuthResponse;
import com.fooddelivery.auth.dto.LoginRequest;
import com.fooddelivery.auth.dto.SignupRequest;
import com.fooddelivery.auth.entity.User;
import com.fooddelivery.auth.event.UserEvent;
import com.fooddelivery.auth.exception.CustomException;
import com.fooddelivery.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        // Validate password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new CustomException("Passwords do not match");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("Email already registered");
        }

        // Check if phone already exists
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new CustomException("Phone number already registered");
        }

        // Create new user instance
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Save user to database
        User savedUser = userRepository.save(user);

        // Publish user registered event to Kafka
        UserEvent userEvent = new UserEvent(
            "USER_REGISTERED",
            savedUser.getId(),
            savedUser.getFullName(),
            savedUser.getEmail(),
            savedUser.getPhone(),
            "CUSTOMER"
        );
        kafkaProducerService.publishUserEvent(userEvent);

        // Send legacy Kafka message (for backward compatibility)
        kafkaProducerService.sendMessageToTopic("User " + savedUser.getFullName() + " registered successfully");

        // Generate JWT token for the registered user
        String token = jwtService.generateToken(savedUser.getEmail());

        // Return AuthResponse with user info and token
        return new AuthResponse(
            token,
            savedUser.getId(),
            savedUser.getFullName(),
            savedUser.getEmail(),
            savedUser.getPhone()
        );
    }

    public AuthResponse login(LoginRequest request) {
        // Find user by email or phone
        User user = userRepository.findByEmail(request.getEmailOrPhone())
                .or(() -> userRepository.findByPhone(request.getEmailOrPhone()))
                .orElseThrow(() -> new CustomException("Invalid credentials"));

        // Verify password matches
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException("Invalid credentials");
        }

        // Generate JWT token
        String token = jwtService.generateToken(user.getEmail());

        // Return response with token and user info
        return new AuthResponse(
            token,
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getPhone()
        );
    }
}