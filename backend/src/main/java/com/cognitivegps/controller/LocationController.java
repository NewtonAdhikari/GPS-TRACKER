package com.cognitivegps.controller;

import com.cognitivegps.dto.LocationRequest;
import com.cognitivegps.dto.LocationResponse;
import com.cognitivegps.service.LocationService;
import com.cognitivegps.service.ZoneClusteringService;
import com.cognitivegps.service.TransitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://127.0.0.1:5173"})
public class LocationController {

    private final LocationService locationService;
    private final ZoneClusteringService zoneClusteringService;
    private final TransitionService transitionService;

    @PostMapping("/location")
    public ResponseEntity<LocationResponse> storeLocation(
            @Valid @RequestBody LocationRequest request) {
        LocationResponse response = locationService.storeLocation(request);

        String currentZone = zoneClusteringService
                .findOrCreateZone(request.getUserId(),
                        request.getLatitude(), request.getLongitude())
                .getZoneName();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<LocationResponse>> getHistory(
            @RequestParam String userId,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime) {
        List<LocationResponse> history;
        if (startTime != null && endTime != null) {
            history = locationService.getLocationHistory(userId,
                    java.time.Instant.ofEpochMilli(startTime),
                    java.time.Instant.ofEpochMilli(endTime));
        } else {
            history = locationService.getLocationHistory(userId);
        }
        return ResponseEntity.ok(history);
    }
}
