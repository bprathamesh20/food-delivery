package com.foodDelivery.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.events.OrderEvent;

@RestController
@RequestMapping("/api/v1/test")
public class TestKafkaController {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TestKafkaController(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/order-event")
    public ResponseEntity<String> sendOrderEvent(@RequestBody OrderEvent orderEvent) {
        kafkaTemplate.send("order-events", orderEvent.getOrderId().toString(), orderEvent);
        return ResponseEntity.ok("Order event sent");
    }
}
