package com.foodDelivery.dto;

public class AuthResponse {
    private String token;
    private String email;
    private String name;
    private Long agentId;
    private String role;

    public AuthResponse() {}

    public AuthResponse(String token, String email, String name, Long agentId, String role) {
        this.token = token;
        this.email = email;
        this.name = name;
        this.agentId = agentId;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
