package com.fooddelivery.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    public String token;
 
//    public AuthResponse(String token) {
//        this.token = token;
//    }
}