package com.deliveryflow.driverservice.controller;

import com.deliveryflow.driverservice.dto.DriverDto;
import com.deliveryflow.driverservice.model.DriverStatus;
import com.deliveryflow.driverservice.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Driver service is running");
    }

    @PostMapping("/register")
    public ResponseEntity<DriverDto.Response> registerDriver(
        @RequestHeader("X-User-Id") String driverId,
        @Valid @RequestBody DriverDto.RegisterRequest request
    ) {
        return ResponseEntity.ok(driverService.registerDriver(driverId, request));
    }

    @GetMapping("/me")
    public ResponseEntity<DriverDto.Response> getMyProfile(
        @RequestHeader("X-User-Id") String driverId
    ) {
        return ResponseEntity.ok(driverService.getDriver(driverId));
    }

    @GetMapping("/available")
    public ResponseEntity<List<DriverDto.Response>> getAvailableDrivers() {
        return ResponseEntity.ok(driverService.getAvailableDrivers());
    }

    @PutMapping("/location")
    public ResponseEntity<DriverDto.Response> updateLocation(
        @RequestHeader("X-User-Id") String driverId,
        @Valid @RequestBody DriverDto.LocationUpdate request
    ) {
        return ResponseEntity.ok(driverService.updateLocation(driverId, request.getLocation()));
    }

    @PutMapping("/status/{status}")
    public ResponseEntity<DriverDto.Response> updateStatus(
        @RequestHeader("X-User-Id") String driverId,
        @PathVariable DriverStatus status
    ) {
        return ResponseEntity.ok(driverService.updateStatus(driverId, status));
    }
}
