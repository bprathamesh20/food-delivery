package com.notification.notification.kafkaconsumer;

import com.fooddelivery.events.KafkaTopics;
import com.fooddelivery.events.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(UserEventConsumer.class);

    @KafkaListener(topics = KafkaTopics.USER_EVENTS, groupId = "notification-group", containerFactory = "userEventKafkaListenerContainerFactory")
    public void consumeUserEvent(UserEvent event) {
        log.info("Received user event: type={}, userId={}, email={}", 
                event.getEventType(), event.getUserId(), event.getEmail());
        
        switch (event.getEventType()) {
            case "USER_REGISTERED":
                sendWelcomeEmail(event);
                break;
            case "USER_PROFILE_UPDATED":
                log.info("Profile updated for user: {}", event.getUserId());
                break;
            default:
                log.warn("Unknown user event type: {}", event.getEventType());
        }
    }

    private void sendWelcomeEmail(UserEvent event) {
        // TODO: Implement actual email sending logic
        log.info("Sending welcome email to: {} ({})", event.getFullName(), event.getEmail());
    }
}
