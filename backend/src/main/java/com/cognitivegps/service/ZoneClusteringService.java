package com.cognitivegps.service;

import com.cognitivegps.model.DynamicZone;
import com.cognitivegps.model.UserLocation;
import com.cognitivegps.repository.DynamicZoneRepository;
import com.cognitivegps.repository.UserLocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZoneClusteringService {

    private final DynamicZoneRepository zoneRepository;
    private final UserLocationRepository locationRepository;

    private static final double ZONE_RADIUS_KM = 0.5;
    private static final double KM_PER_DEGREE = 111.0;

    public DynamicZone findOrCreateZone(String userId, double lat, double lng) {
        List<DynamicZone> existingZones = zoneRepository.findByUserId(userId);

        for (DynamicZone zone : existingZones) {
            double distance = calculateDistance(
                    lat, lng,
                    zone.getCentroid()[1], zone.getCentroid()[0]
            );
            if (distance <= ZONE_RADIUS_KM) {
                zone.setVisitCount(zone.getVisitCount() + 1);
                zone.setLastVisited(Instant.now());
                zoneRepository.save(zone);
                return zone;
            }
        }

        String zoneName = generateZoneName(userId, existingZones.size());
        DynamicZone newZone = DynamicZone.builder()
                .userId(userId)
                .zoneId(UUID.randomUUID().toString())
                .zoneName(zoneName)
                .centroid(new Double[]{lng, lat})
                .radius(ZONE_RADIUS_KM)
                .visitCount(1)
                .lastVisited(Instant.now())
                .zoneType("UNKNOWN")
                .build();

        return zoneRepository.save(newZone);
    }

    @Scheduled(fixedRate = 300000)
    public void refineZones() {
        log.debug("Running zone refinement...");
    }

    public DynamicZone getCurrentZone(String userId) {
        UserLocation latest = locationRepository.findByUserIdOrderByTimestampDesc(userId)
                .stream()
                .findFirst()
                .orElse(null);

        if (latest == null) return null;

        return findOrCreateZone(userId,
                latest.getLocation().getY(),
                latest.getLocation().getX());
    }

    public List<DynamicZone> getUserZones(String userId) {
        return zoneRepository.findByUserId(userId);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return KM_PER_DEGREE * c;
    }

    private String generateZoneName(String userId, int zoneIndex) {
        return userId + "_zone_" + zoneIndex;
    }
}
