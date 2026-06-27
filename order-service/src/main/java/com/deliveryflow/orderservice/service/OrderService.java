package com.deliveryflow.orderservice.service;

import com.deliveryflow.orderservice.dto.OrderDto;
import com.deliveryflow.orderservice.event.OrderEvent;
import com.deliveryflow.orderservice.model.Order;
import com.deliveryflow.orderservice.model.OrderStatus;
import com.deliveryflow.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-keys.order-created}")
    private String orderCreatedRoutingKey;

    @Value("${rabbitmq.routing-keys.order-assigned}")
    private String orderAssignedRoutingKey;

    public OrderDto.Response createOrder(String customerId, OrderDto.CreateRequest request) {
        Order order = Order.builder()
            .customerId(customerId)
            .pickupAddress(request.getPickupAddress())
            .deliveryAddress(request.getDeliveryAddress())
            .recipientName(request.getRecipientName())
            .recipientPhone(request.getRecipientPhone())
            .packageDescription(request.getPackageDescription())
            .status(OrderStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .build();

        Order saved = orderRepository.save(order);
        log.info("Order created with id: {}", saved.getId());

        // Publish event to RabbitMQ — tracking and notification services will consume this
        OrderEvent event = OrderEvent.builder()
            .orderId(saved.getId())
            .customerId(saved.getCustomerId())
            .pickupAddress(saved.getPickupAddress())
            .deliveryAddress(saved.getDeliveryAddress())
            .recipientName(saved.getRecipientName())
            .recipientPhone(saved.getRecipientPhone())
            .status(saved.getStatus().name())
            .timestamp(LocalDateTime.now())
            .build();

        rabbitTemplate.convertAndSend(exchange, orderCreatedRoutingKey, event);
        log.info("Published order.created event for order id: {}", saved.getId());

        return mapToResponse(saved);
    }

    public OrderDto.Response getOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return mapToResponse(order);
    }

    public List<OrderDto.Response> getOrdersByCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public List<OrderDto.Response> getOrdersByDriver(String driverId) {
        return orderRepository.findByAssignedDriverId(driverId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public OrderDto.Response assignDriver(Long orderId, String driverId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        order.setAssignedDriverId(driverId);
        order.setStatus(OrderStatus.ASSIGNED);
        Order saved = orderRepository.save(order);

        // Publish order assigned event
        OrderEvent event = OrderEvent.builder()
            .orderId(saved.getId())
            .customerId(saved.getCustomerId())
            .assignedDriverId(driverId)
            .status(saved.getStatus().name())
            .timestamp(LocalDateTime.now())
            .build();

        rabbitTemplate.convertAndSend(exchange, orderAssignedRoutingKey, event);
        log.info("Published order.assigned event for order id: {}", saved.getId());

        return mapToResponse(saved);
    }

    public OrderDto.Response updateStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        order.setStatus(status);
        return mapToResponse(orderRepository.save(order));
    }

    private OrderDto.Response mapToResponse(Order order) {
        return OrderDto.Response.builder()
            .id(order.getId())
            .customerId(order.getCustomerId())
            .pickupAddress(order.getPickupAddress())
            .deliveryAddress(order.getDeliveryAddress())
            .recipientName(order.getRecipientName())
            .recipientPhone(order.getRecipientPhone())
            .packageDescription(order.getPackageDescription())
            .status(order.getStatus())
            .assignedDriverId(order.getAssignedDriverId())
            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt())
            .build();
    }
}
