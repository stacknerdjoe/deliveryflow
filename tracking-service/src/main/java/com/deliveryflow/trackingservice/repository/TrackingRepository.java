package com.deliveryflow.trackingservice.repository;

import com.deliveryflow.trackingservice.model.TrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackingRepository extends JpaRepository<TrackingEvent, Long> {
    List<TrackingEvent> findByOrderIdOrderByTimestampAsc(Long orderId);
}
