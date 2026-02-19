package com.fooddelivery.events;

/**
 * Event published when user-related actions occur.
 * Event types: USER_REGISTERED, USER_PROFILE_UPDATED, USER_LOGIN, USER_DELETED
 */
public class UserEvent extends BaseEvent {
    
    private static final long serialVersionUID = 1L;
    
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    
    public UserEvent() {
        super();
    }
    
    public UserEvent(String eventType, Long userId, String fullName, String email, String phone, String role) {
        super(eventType);
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
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
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    @Override
    public String toString() {
        return "UserEvent{" +
                "eventType='" + getEventType() + '\'' +
                ", userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", role='" + role + '\'' +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}
