package com.fooddelivery.auth.service;

import com.fooddelivery.auth.event.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import org.springframework.kafka.support.SendResult;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    private static final String USER_EVENTS_TOPIC = "user-events";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessageToTopic(String message) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(USER_EVENTS_TOPIC, message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Sent message=[{}] with offset=[{}]", message, result.getRecordMetadata().offset());
            } else {
                logger.error("Unable to send message=[{}] due to : {}", message, ex.getMessage());
            }
        });
    }

    public void publishUserEvent(UserEvent event) {
        try {
            String eventJson = convertToJson(event);
            CompletableFuture<SendResult<String, String>> future = 
                kafkaTemplate.send(USER_EVENTS_TOPIC, event.getUserId().toString(), eventJson);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("User event published successfully: eventType={}, userId={}", 
                        event.getEventType(), event.getUserId());
                } else {
                    logger.error("Failed to publish user event: eventType={}, userId={}", 
                        event.getEventType(), event.getUserId(), ex);
                }
            });
        } catch (Exception e) {
            logger.error("Error publishing user event", e);
        }
    }

    private String convertToJson(UserEvent event) {
        return String.format("{\"@type\":\"USER\",\"eventType\":\"%s\",\"userId\":%d,\"fullName\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\",\"role\":\"%s\",\"timestamp\":\"%s\"}",
            event.getEventType(), event.getUserId(), event.getFullName(), 
            event.getEmail(), event.getPhone(), event.getRole(), event.getTimestamp());
    }
}
