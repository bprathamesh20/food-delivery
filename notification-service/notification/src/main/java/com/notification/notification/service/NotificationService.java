package com.notification.notification.service;

import com.notification.notification.entity.Notification;
import com.notification.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public Notification createNotification(Long userId, String userType, String title, String message, String type, Long orderId, Long deliveryId) {
        Notification notification = new Notification(userId, userType, title, message, type);
        notification.setOrderId(orderId);
        notification.setDeliveryId(deliveryId);
        
        Notification saved = notificationRepository.save(notification);
        log.info("Created notification: id={}, userId={}, type={}", saved.getId(), userId, type);
        
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(Long userId, String userType) {
        return notificationRepository.findByUserIdAndUserTypeOrderByCreatedAtDesc(userId, userType);
    }

    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(Long userId, String userType) {
        return notificationRepository.findByUserIdAndUserTypeAndIsReadOrderByCreatedAtDesc(userId, userType, false);
    }

    @Transactional(readOnly = true)
    public Long getUnreadCount(Long userId, String userType) {
        return notificationRepository.countByUserIdAndUserTypeAndIsRead(userId, userType, false);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setIsRead(true);
            notificationRepository.save(notification);
            log.info("Marked notification as read: id={}", notificationId);
        });
    }

    @Transactional
    public void markAllAsRead(Long userId, String userType) {
        List<Notification> unreadNotifications = getUnreadNotifications(userId, userType);
        unreadNotifications.forEach(notification -> notification.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);
        log.info("Marked all notifications as read for user: userId={}, userType={}", userId, userType);
    }
}
