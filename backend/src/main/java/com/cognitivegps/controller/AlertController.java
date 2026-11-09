package com.cognitivegps.controller;

import com.cognitivegps.model.Anomaly;
import com.cognitivegps.model.Alert;
import com.cognitivegps.service.AnomalyDetectionService;
import com.cognitivegps.service.EmergencyDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://127.0.0.1:5173"})
public class AlertController {

    private final AnomalyDetectionService anomalyDetectionService;
    private final EmergencyDetectionService emergencyDetectionService;

    @GetMapping("/anomalies")
    public ResponseEntity<List<Anomaly>> getAnomalies(
            @RequestParam String userId) {
        return ResponseEntity.ok(anomalyDetectionService.getAnomalies(userId));
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<Alert>> getAlerts(
            @RequestParam String userId) {
        return ResponseEntity.ok(emergencyDetectionService.getAlerts(userId));
    }
}
