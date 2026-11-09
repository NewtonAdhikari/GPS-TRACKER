package com.cognitivegps.service;

import com.cognitivegps.model.DynamicZone;
import com.cognitivegps.model.UserLocation;
import com.cognitivegps.repository.UserLocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimulationService {

    private final ZoneClusteringService zoneClusteringService;
    private final TransitionService transitionService;
    private final UserLocationRepository locationRepository;

    public List<String> simulateFuturePath(String userId, int hoursAhead) {
        DynamicZone currentZone = zoneClusteringService.getCurrentZone(userId);

        if (currentZone == null) {
            return List.of();
        }

        int steps = hoursAhead * 2;
        return transitionService.predictPath(userId, currentZone.getZoneName(), steps);
    }

    public double calculateSimulationConfidence(String userId) {
        List<UserLocation> recentLocations =
                locationRepository.findByUserIdOrderByTimestampDesc(userId);

        if (recentLocations.size() < 10) {
            return 0.3;
        }

        double dataFreshness = Math.min(1.0, recentLocations.size() / 50.0);

        return dataFreshness * 0.8;
    }

    public List<String> generateAlternativeRoutes(String userId, String destination, int count) {
        return List.of("Route via Highway", "Route via City Center", "Route via Suburbs")
                .subList(0, Math.min(count, 3));
    }
}
