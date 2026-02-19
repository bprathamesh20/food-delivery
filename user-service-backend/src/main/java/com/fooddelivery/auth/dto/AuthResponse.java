package com.fooddelivery.auth.dto;

import java.util.Objects;

public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String fullName;
    private String email;
    private String phone;

    // No-args constructor
    public AuthResponse() {
    }

    // All-args constructor
    public AuthResponse(String token, String type, Long id, String fullName, String email, String phone) {
        this.token = token;
        this.type = type;
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    // Custom constructor (same as your original)
    public AuthResponse(String token, Long id, String fullName, String email, String phone) {
        this.token = token;
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        // 'type' remains default "Bearer"
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // equals and hashCode (helpful if you compare/store instances)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthResponse)) return false;
        AuthResponse that = (AuthResponse) o;
        return Objects.equals(token, that.token) &&
               Objects.equals(type, that.type) &&
               Objects.equals(id, that.id) &&
               Objects.equals(fullName, that.fullName) &&
               Objects.equals(email, that.email) &&
               Objects.equals(phone, that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, type, id, fullName, email, phone);
    }

    // toString (useful for logging/debugging)
    @Override
    public String toString() {
        return "AuthResponse{" +
                "token='" + token + '\'' +
                ", type='" + type + '\'' +
                ", id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
