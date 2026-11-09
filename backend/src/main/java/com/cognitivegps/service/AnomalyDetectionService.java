package com.cognitivegps.service;

import com.cognitivegps.model.UserLocation;
import com.cognitivegps.repository.UserLocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnomalyDetectionService {

    private final UserLocationRepository locationRepository;
    private final com.cognitivegps.repository.AnomalyRepository anomalyRepository;

    private static final double MAX_SPEED_KMH = 200.0;
    private static final double MIN_MOVEMENT_THRESHOLD_M = 10.0;

    public void detectAnomalies(String userId) {
        List<UserLocation> recentLocations =
                locationRepository.findByUserIdOrderByTimestampDesc(userId);

        if (recentLocations.size() < 2) return;

        UserLocation latest = recentLocations.get(0);
        UserLocation previous = recentLocations.get(1);

        detectSpeedAnomaly(userId, latest, previous);
        detectStopAnomaly(userId, recentLocations);
    }

    private void detectSpeedAnomaly(String userId, UserLocation current, UserLocation previous) {
        if (current.getSpeed() != null && current.getSpeed() > MAX_SPEED_KMH) {
            log.warn("Speed anomaly detected for user {}: {} km/h",
                    userId, current.getSpeed());

            var anomaly = com.cognitivegps.model.Anomaly.builder()
                    .userId(userId)
                    .type(com.cognitivegps.model.Anomaly.AnomalyType.SPOOF)
                    .description(String.format("Impossible speed detected: %.1f km/h",
                            current.getSpeed()))
                    .severity("HIGH")
                    .timestamp(Instant.now())
                    .location(new Double[]{
                            current.getLocation().getX(),
                            current.getLocation().getY()
                    })
                    .build();

            anomalyRepository.save(anomaly);
        }
    }

    private void detectStopAnomaly(String userId, List<UserLocation> locations) {
        if (locations.size() < 3) return;

        Instant now = Instant.now();
        Instant lastMovement = locations.get(0).getTimestamp();

        long minutesSinceMovement = ChronoUnit.MINUTES.between(lastMovement, now);

        if (minutesSinceMovement > 30) {
            var anomaly = com.cognitivegps.model.Anomaly.builder()
                    .userId(userId)
                    .type(com.cognitivegps.model.Anomaly.AnomalyType.STOP)
                    .description(String.format("No movement detected for %d minutes",
                            minutesSinceMovement))
                    .severity("MEDIUM")
                    .timestamp(lastMovement)
                    .location(new Double[]{
                            locations.get(0).getLocation().getX(),
                            locations.get(0).getLocation().getY()
                    })
                    .build();

            anomalyRepository.save(anomaly);
        }
    }

    public List<com.cognitivegps.model.Anomaly> getAnomalies(String userId) {
        return anomalyRepository.findByUserIdOrderByTimestampDesc(userId);
    }
}
