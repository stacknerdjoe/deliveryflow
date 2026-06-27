package com.deliveryflow.driverservice.repository;

import com.deliveryflow.driverservice.model.Driver;
import com.deliveryflow.driverservice.model.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, String> {
    List<Driver> findByStatus(DriverStatus status);
}
