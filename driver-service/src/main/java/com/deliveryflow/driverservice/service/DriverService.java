package com.deliveryflow.driverservice.service;

import com.deliveryflow.driverservice.dto.DriverDto;
import com.deliveryflow.driverservice.model.Driver;
import com.deliveryflow.driverservice.model.DriverStatus;
import com.deliveryflow.driverservice.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverService {

    private final DriverRepository driverRepository;

    public DriverDto.Response registerDriver(String driverId, DriverDto.RegisterRequest request) {
        Driver driver = Driver.builder()
            .id(driverId)
            .name(request.getName())
            .phone(request.getPhone())
            .vehicleType(request.getVehicleType())
            .status(DriverStatus.AVAILABLE)
            .build();

        Driver saved = driverRepository.save(driver);
        log.info("Driver registered with id: {}", saved.getId());
        return mapToResponse(saved);
    }

    public DriverDto.Response getDriver(String driverId) {
        Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new RuntimeException("Driver not found: " + driverId));
        return mapToResponse(driver);
    }

    public List<DriverDto.Response> getAvailableDrivers() {
        return driverRepository.findByStatus(DriverStatus.AVAILABLE)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public DriverDto.Response updateLocation(String driverId, String location) {
        Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new RuntimeException("Driver not found: " + driverId));
        driver.setCurrentLocation(location);
        return mapToResponse(driverRepository.save(driver));
    }

    public DriverDto.Response updateStatus(String driverId, DriverStatus status) {
        Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new RuntimeException("Driver not found: " + driverId));
        driver.setStatus(status);
        return mapToResponse(driverRepository.save(driver));
    }

    private DriverDto.Response mapToResponse(Driver driver) {
        return DriverDto.Response.builder()
            .id(driver.getId())
            .name(driver.getName())
            .phone(driver.getPhone())
            .vehicleType(driver.getVehicleType())
            .status(driver.getStatus())
            .currentLocation(driver.getCurrentLocation())
            .build();
    }
}
