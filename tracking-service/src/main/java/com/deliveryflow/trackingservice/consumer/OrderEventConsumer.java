package com.deliveryflow.trackingservice.consumer;

import com.deliveryflow.trackingservice.model.TrackingEvent;
import com.deliveryflow.trackingservice.repository.TrackingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrderEventConsumer {

    @Autowired
    private TrackingRepository trackingRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "tracking.order.created")
    public void handleOrderCreated(Message message) {
        try {
            String body = new String(message.getBody());
            System.out.println("ORDER CREATED EVENT RECEIVED: " + body);
            Map<String, Object> event = objectMapper.readValue(body, Map.class);

            TrackingEvent trackingEvent = new TrackingEvent();
            trackingEvent.setOrderId(Long.valueOf(event.get("orderId").toString()));
            trackingEvent.setStatus("PENDING");
            trackingEvent.setNotes("Order created, awaiting driver assignment");

            trackingRepository.save(trackingEvent);
            System.out.println("Tracking record saved for order: " + event.get("orderId"));
        } catch (Exception e) {
            System.err.println("Error processing order.created: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "tracking.order.assigned")
    public void handleOrderAssigned(Message message) {
        try {
            String body = new String(message.getBody());
            System.out.println("ORDER ASSIGNED EVENT RECEIVED: " + body);
            Map<String, Object> event = objectMapper.readValue(body, Map.class);

            TrackingEvent trackingEvent = new TrackingEvent();
            trackingEvent.setOrderId(Long.valueOf(event.get("orderId").toString()));
            trackingEvent.setStatus("ASSIGNED");
            trackingEvent.setDriverId(event.get("assignedDriverId") != null ? event.get("assignedDriverId").toString() : null);
            trackingEvent.setNotes("Driver assigned to order");

            trackingRepository.save(trackingEvent);
            System.out.println("Tracking record saved for assigned order: " + event.get("orderId"));
        } catch (Exception e) {
            System.err.println("Error processing order.assigned: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
