package com.deliveryflow.orderservice.controller;

import com.deliveryflow.orderservice.dto.OrderDto;
import com.deliveryflow.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Order service is running");
    }

    @PostMapping
    public ResponseEntity<OrderDto.Response> createOrder(
        @RequestHeader("X-User-Id") String customerId,
        @Valid @RequestBody OrderDto.CreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(orderService.createOrder(customerId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto.Response> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderDto.Response>> getMyOrders(
        @RequestHeader("X-User-Id") String customerId
    ) {
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<OrderDto.Response>> getOrdersByDriver(
        @PathVariable String driverId
    ) {
        return ResponseEntity.ok(orderService.getOrdersByDriver(driverId));
    }

    @PutMapping("/{orderId}/assign-driver")
    public ResponseEntity<OrderDto.Response> assignDriver(
        @PathVariable Long orderId,
        @Valid @RequestBody OrderDto.AssignDriverRequest request
    ) {
        return ResponseEntity.ok(orderService.assignDriver(orderId, request.getDriverId()));
    }
}
