package com.notification.notification.kafkalistener;

import org.springframework.stereotype.Component;

@Component
public class kafkahandler {
    public void handleMessage(String message) {
        System.out.println("Handling message: " + message);
    }
}