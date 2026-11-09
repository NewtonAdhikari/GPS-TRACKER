package com.cognitivegps.service;

import com.cognitivegps.dto.LocationRequest;
import com.cognitivegps.dto.LocationResponse;
import com.cognitivegps.model.UserLocation;
import com.cognitivegps.repository.UserLocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final UserLocationRepository locationRepository;

    public LocationResponse storeLocation(LocationRequest request) {
        UserLocation location = UserLocation.builder()
                .userId(request.getUserId())
                .location(new org.springframework.data.mongodb.core.geo.GeoJsonPoint(
                        request.getLongitude(), request.getLatitude()))
                .timestamp(Instant.now())
                .speed(request.getSpeed())
                .altitude(request.getAltitude())
                .accuracy(request.getAccuracy())
                .build();

        UserLocation saved = locationRepository.save(location);
        log.info("Stored location for user {}: [{}, {}]",
                request.getUserId(), request.getLatitude(), request.getLongitude());

        return toResponse(saved);
    }

    public List<LocationResponse> getLocationHistory(String userId) {
        return locationRepository.findByUserIdOrderByTimestampDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<LocationResponse> getLocationHistory(String userId, Instant start, Instant end) {
        return locationRepository.findByUserIdAndTimestampBetweenOrderByTimestampAsc(userId, start, end)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public UserLocation getLatestLocation(String userId) {
        return locationRepository.findByUserIdOrderByTimestampDesc(userId)
                .stream()
                .findFirst()
                .orElse(null);
    }

    private LocationResponse toResponse(UserLocation location) {
        return LocationResponse.builder()
                .id(location.getId())
                .userId(location.getUserId())
                .longitude(location.getLocation().getX())
                .latitude(location.getLocation().getY())
                .timestamp(location.getTimestamp())
                .speed(location.getSpeed())
                .build();
    }
}
