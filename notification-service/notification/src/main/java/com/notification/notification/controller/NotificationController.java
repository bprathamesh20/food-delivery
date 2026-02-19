package com.notification.notification.controller;

import com.notification.notification.entity.Notification;
import com.notification.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getUserNotifications(
            @RequestParam Long userId,
            @RequestParam String userType) {
        List<Notification> notifications = notificationService.getUserNotifications(userId, userType);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(
            @RequestParam Long userId,
            @RequestParam String userType) {
        List<Notification> notifications = notificationService.getUnreadNotifications(userId, userType);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @RequestParam Long userId,
            @RequestParam String userType) {
        Long count = notificationService.getUnreadCount(userId, userType);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @RequestParam Long userId,
            @RequestParam String userType) {
        notificationService.markAllAsRead(userId, userType);
        return ResponseEntity.ok().build();
    }
}
