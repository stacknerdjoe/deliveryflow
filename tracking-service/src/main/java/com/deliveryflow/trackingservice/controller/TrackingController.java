package com.deliveryflow.trackingservice.controller;

import com.deliveryflow.trackingservice.model.TrackingEvent;
import com.deliveryflow.trackingservice.repository.TrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingRepository trackingRepository;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Tracking service is running");
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<List<TrackingEvent>> getTrackingHistory(@PathVariable Long orderId) {
        List<TrackingEvent> events = trackingRepository.findByOrderIdOrderByTimestampAsc(orderId);
        return ResponseEntity.ok(events);
    }
}
