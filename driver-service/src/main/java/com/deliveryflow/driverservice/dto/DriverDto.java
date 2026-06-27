package com.deliveryflow.driverservice.dto;

import com.deliveryflow.driverservice.model.DriverStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DriverDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        @NotBlank
        private String name;

        @NotBlank
        private String phone;

        @NotBlank
        private String vehicleType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String id;
        private String name;
        private String phone;
        private String vehicleType;
        private DriverStatus status;
        private String currentLocation;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationUpdate {
        @NotBlank
        private String location;
    }
}
