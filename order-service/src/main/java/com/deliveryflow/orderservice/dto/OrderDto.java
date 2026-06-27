package com.deliveryflow.orderservice.dto;

import com.deliveryflow.orderservice.model.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class OrderDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "Pickup address is required")
        private String pickupAddress;

        @NotBlank(message = "Delivery address is required")
        private String deliveryAddress;

        @NotBlank(message = "Recipient name is required")
        private String recipientName;

        @NotBlank(message = "Recipient phone is required")
        private String recipientPhone;

        private String packageDescription;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String customerId;
        private String pickupAddress;
        private String deliveryAddress;
        private String recipientName;
        private String recipientPhone;
        private String packageDescription;
        private OrderStatus status;
        private String assignedDriverId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignDriverRequest {
        @NotBlank(message = "Driver ID is required")
        private String driverId;
    }
}
