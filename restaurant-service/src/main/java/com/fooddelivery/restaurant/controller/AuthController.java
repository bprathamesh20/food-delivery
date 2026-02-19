//package com.fooddelivery.restaurant.controller;
//
//
//
//import com.fooddelivery.restaurant.dto.*;
//import com.fooddelivery.restaurant.security.JwtUtil;
// 
//import org.springframework.security.authentication.*;
//import org.springframework.web.bind.annotation.*;
// 
//@RestController
//@RequestMapping("/auth")
//
//public class AuthController {
// 
//    private final AuthenticationManager authManager;
//    private final JwtUtil jwtUtil;
// 
//    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil) {
//        this.authManager = authManager;
//        this.jwtUtil = jwtUtil;
//    }
// 
//    @PostMapping("/login")
//    public AuthResponse login(@RequestBody AuthRequest request) {
// 
//        authManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.username, request.password));
// 
//        String token = jwtUtil.generateToken(request.username);
//        return new AuthResponse(token);
//    }
//}
package com.fooddelivery.restaurant.controller;
 
import com.fooddelivery.restaurant.dto.*;
import com.fooddelivery.restaurant.entity.User;
import com.fooddelivery.restaurant.repository.UserRepository;
import com.fooddelivery.restaurant.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
 
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
 
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
 
    // ✅ REGISTER
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
 
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return "User already exists";
        }
 
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER");
 
        userRepository.save(user);
 
        return "User registered successfully";
    }
 
    // ✅ LOGIN
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
 
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
 
        String token = jwtUtil.generateToken(request.getUsername());
        return new AuthResponse(token);
    }
}
 