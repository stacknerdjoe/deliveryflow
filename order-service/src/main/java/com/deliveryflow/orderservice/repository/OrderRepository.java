package com.deliveryflow.orderservice.repository;

import com.deliveryflow.orderservice.model.Order;
import com.deliveryflow.orderservice.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(String customerId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByAssignedDriverId(String driverId);
}
