package com.deliveryflow.notificationservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificationConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "notification.order.created")
    public void handleOrderCreated(Message message) {
        try {
            String body = new String(message.getBody());
            System.out.println("NOTIFICATION - ORDER CREATED: " + body);
            Map<String, Object> event = objectMapper.readValue(body, Map.class);
            System.out.println("=== SMS SENT === New order #" + event.get("orderId") + " received! Pickup: " + event.get("pickupAddress"));
        } catch (Exception e) {
            System.err.println("Notification error: " + e.getMessage());
        }
    }

    @RabbitListener(queues = "notification.order.assigned")
    public void handleOrderAssigned(Message message) {
        try {
            String body = new String(message.getBody());
            System.out.println("NOTIFICATION - ORDER ASSIGNED: " + body);
            Map<String, Object> event = objectMapper.readValue(body, Map.class);
            System.out.println("=== SMS SENT === Order #" + event.get("orderId") + " driver assigned!");
        } catch (Exception e) {
            System.err.println("Notification error: " + e.getMessage());
        }
    }
}
