package com.deliveryflow.driverservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "drivers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Driver {

    @Id
    private String id; // Same ID as User in order-service

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String vehicleType; // BIKE, CAR, VAN, TRUCK

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverStatus status;

    private String currentLocation;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = DriverStatus.AVAILABLE;
    }
}
