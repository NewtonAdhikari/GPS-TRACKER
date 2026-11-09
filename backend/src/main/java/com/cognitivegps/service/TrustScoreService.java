package com.cognitivegps.service;

import com.cognitivegps.model.UserLocation;
import com.cognitivegps.repository.UserLocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrustScoreService {

    private final UserLocationRepository locationRepository;

    public double calculateTrustScore(String userId) {
        List<UserLocation> locations =
                locationRepository.findByUserIdOrderByTimestampDesc(userId);

        if (locations.isEmpty()) {
            return 0.0;
        }

        double dataVolumeScore = Math.min(1.0, locations.size() / 100.0);

        double dataRecencyScore = calculateRecencyScore(locations);

        double consistencyScore = calculateConsistencyScore(locations);

        return (dataVolumeScore * 0.3 + dataRecencyScore * 0.4 + consistencyScore * 0.3);
    }

    private double calculateRecencyScore(List<UserLocation> locations) {
        long hoursSinceLastUpdate = java.time.Duration.between(
                locations.get(0).getTimestamp(),
                java.time.Instant.now()
        ).toHours();

        if (hoursSinceLastUpdate < 1) return 1.0;
        if (hoursSinceLastUpdate < 24) return 0.8;
        if (hoursSinceLastUpdate < 72) return 0.5;
        return 0.2;
    }

    private double calculateConsistencyScore(List<UserLocation> locations) {
        if (locations.size() < 2) return 0.5;

        int consistentZones = 0;
        for (int i = 0; i < Math.min(10, locations.size() - 1); i++) {
            if (isLocationConsistent(locations.get(i), locations.get(i + 1))) {
                consistentZones++;
            }
        }

        return (double) consistentZones / Math.min(10, locations.size() - 1);
    }

    private boolean isLocationConsistent(UserLocation a, UserLocation b) {
        if (a.getSpeed() == null || b.getSpeed() == null) return true;

        return Math.abs(a.getSpeed() - b.getSpeed()) < 20;
    }
}
