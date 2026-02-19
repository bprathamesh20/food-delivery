//package com.foodDelivery.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        http
//            .csrf(csrf -> csrf.disable())
//            .authorizeHttpRequests(auth -> auth
//                .anyRequest().permitAll()
//            )
//            .httpBasic(basic -> basic.disable())     // 🔴 disable basic auth
//            .formLogin(form -> form.disable());     // 🔴 disable login page
//
//        return http.build();
//    }
//}

package com.foodDelivery.config;

import com.foodDelivery.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.disable()) // CORS handled by CorsFilter bean
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - authentication not required
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/delivery-service/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/test/**").permitAll()
                .requestMatchers("/error").permitAll()
                
                // Agent endpoints - require authentication (role check in JWT filter)
                .requestMatchers("/api/v1/agents/me/**").authenticated()
                .requestMatchers("/api/v1/deliveries/agent/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/v1/deliveries/*/status").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/deliveries/*/location").authenticated()
                
                // Delivery endpoints - allow authenticated access
                .requestMatchers(HttpMethod.GET, "/api/v1/deliveries/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/deliveries").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/deliveries/*/assign").permitAll()
                
                // Admin endpoints
                .requestMatchers("/api/v1/agents").permitAll()
                .requestMatchers("/api/v1/agents/available").permitAll()
                
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

