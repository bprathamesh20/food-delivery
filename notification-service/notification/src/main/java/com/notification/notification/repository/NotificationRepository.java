package com.notification.notification.repository;

import com.notification.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByUserIdAndUserTypeOrderByCreatedAtDesc(Long userId, String userType);
    
    List<Notification> findByUserIdAndUserTypeAndIsReadOrderByCreatedAtDesc(Long userId, String userType, Boolean isRead);
    
    Long countByUserIdAndUserTypeAndIsRead(Long userId, String userType, Boolean isRead);
}
