package com.fooddelivery.auth.event;

import java.time.LocalDateTime;

public class UserEvent {
    
    private String eventType; // USER_REGISTERED, USER_PROFILE_UPDATED, USER_LOGIN
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private LocalDateTime timestamp;

    public UserEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public UserEvent(String eventType, Long userId, String fullName, String email, String phone, String role) {
        this.eventType = eventType;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "UserEvent{" +
                "eventType='" + eventType + '\'' +
                ", userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
