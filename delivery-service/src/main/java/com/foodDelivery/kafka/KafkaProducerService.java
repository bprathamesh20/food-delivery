package com.foodDelivery.kafka;

import com.foodDelivery.config.KafkaTopicConfig;
import com.fooddelivery.events.DeliveryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

	private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

	private final KafkaTemplate<String, Object> kafkaTemplate;

	public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendDeliveryEvent(DeliveryEvent event) {
		logger.info("Publishing delivery event: {}", event);

		CompletableFuture<SendResult<String, Object>> future = kafkaTemplate
				.send(KafkaTopicConfig.DELIVERY_EVENTS_TOPIC, event.getOrderId().toString(), event);

		future.whenComplete((result, ex) -> {
			if (ex == null) {
				logger.info("Delivery event sent successfully: orderId={}, offset={}", event.getOrderId(),
						result.getRecordMetadata().offset());
			} else {
				logger.error("Failed to send delivery event: orderId={}, error={}", event.getOrderId(),
						ex.getMessage());
			}
		});
	}

	public void sendNotificationEvent(DeliveryEvent event) {
		logger.info("Publishing notification event: {}", event);

		CompletableFuture<SendResult<String, Object>> future = kafkaTemplate
				.send(KafkaTopicConfig.NOTIFICATION_EVENTS_TOPIC, event.getOrderId().toString(), event);

		future.whenComplete((result, ex) -> {
			if (ex == null) {
				logger.info("Notification event sent successfully: orderId={}", event.getOrderId());
			} else {
				logger.error("Failed to send notification event: orderId={}, error={}", event.getOrderId(),
						ex.getMessage());
			}
		});
	}
}
