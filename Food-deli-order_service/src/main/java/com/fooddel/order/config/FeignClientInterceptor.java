package com.fooddel.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // Get the JWT token from the current request context
            String token = (String) authentication.getCredentials();
            if (token != null && !token.isEmpty()) {
                template.header("Authorization", "Bearer " + token);
            }
        }
    }
}
