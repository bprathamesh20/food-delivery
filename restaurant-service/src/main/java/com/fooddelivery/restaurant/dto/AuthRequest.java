package com.fooddelivery.restaurant.dto;

import lombok.Data;

@Data
public class AuthRequest {
    public String username;
    public String password;
}