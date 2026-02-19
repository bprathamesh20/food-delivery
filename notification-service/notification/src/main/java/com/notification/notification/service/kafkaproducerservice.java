package com.notification.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class kafkaproducerservice {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendPaymentStatus(String message) {
        kafkaTemplate.send("payment-status-topic", message);
        System.out.println("Sent message: " + message);
    }
}