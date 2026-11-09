package com.cognitivegps.service;

import com.cognitivegps.model.Alert;
import com.cognitivegps.model.UserLocation;
import com.cognitivegps.repository.AlertRepository;
import com.cognitivegps.repository.UserLocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmergencyDetectionService {

    private final AlertRepository alertRepository;
    private final UserLocationRepository locationRepository;
    private final AnomalyDetectionService anomalyDetectionService;

    private static final long NO_MOVEMENT_THRESHOLD_MINUTES = 60;

    public void checkEmergencyConditions(String userId) {
        List<UserLocation> locations =
                locationRepository.findByUserIdOrderByTimestampDesc(userId);

        if (locations.isEmpty()) return;

        UserLocation latest = locations.get(0);
        long minutesSinceUpdate = ChronoUnit.MINUTES.between(
                latest.getTimestamp(), Instant.now());

        if (minutesSinceUpdate > NO_MOVEMENT_THRESHOLD_MINUTES) {
            createAlert(userId, Alert.AlertType.NO_MOVEMENT,
                    String.format("No movement detected for %d minutes", minutesSinceUpdate),
                    "HIGH");
        }

        anomalyDetectionService.detectAnomalies(userId);
    }

    public void createAlert(String userId, Alert.AlertType type,
                           String message, String severity) {
        Alert alert = Alert.builder()
                .userId(userId)
                .type(type)
                .message(message)
                .severity(severity)
                .timestamp(Instant.now())
                .build();

        alertRepository.save(alert);
        log.warn("Alert created for user {}: {} - {}",
                userId, type, message);
    }

    public List<Alert> getAlerts(String userId) {
        return alertRepository.findByUserIdOrderByTimestampDesc(userId);
    }
}
