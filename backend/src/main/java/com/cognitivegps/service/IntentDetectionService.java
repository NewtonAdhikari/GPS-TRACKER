package com.cognitivegps.service;

import com.cognitivegps.model.DynamicZone;
import com.cognitivegps.repository.BehaviorProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntentDetectionService {

    private final BehaviorProfileRepository profileRepository;
    private final ZoneClusteringService zoneClusteringService;

    public String detectIntent(String userId) {
        DynamicZone currentZone = zoneClusteringService.getCurrentZone(userId);
        LocalDateTime now = LocalDateTime.now();

        String timeOfDay = getTimeOfDay(now);
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        boolean isWeekend = dayOfWeek == DayOfWeek.SATURDAY ||
                dayOfWeek == DayOfWeek.SUNDAY;

        return inferIntent(currentZone, timeOfDay, isWeekend);
    }

    private String getTimeOfDay(LocalDateTime time) {
        int hour = time.getHour();
        if (hour >= 5 && hour < 12) return "MORNING";
        if (hour >= 12 && hour < 17) return "AFTERNOON";
        if (hour >= 17 && hour < 21) return "EVENING";
        return "NIGHT";
    }

    private String inferIntent(DynamicZone zone, String timeOfDay, boolean isWeekend) {
        String zoneType = zone != null ? zone.getZoneType() : "UNKNOWN";

        if (isWeekend) {
            if (timeOfDay.equals("MORNING") || timeOfDay.equals("AFTERNOON")) {
                return "Leisure/Recreation";
            }
            return "Weekend Routine";
        }

        return switch (timeOfDay) {
            case "MORNING" -> {
                if (zoneType.equals("HOME")) yield "Going to Work";
                yield "Morning Activity";
            }
            case "AFTERNOON" -> zoneType.equals("WORK") ? "At Work" : "Afternoon Activity";
            case "EVENING" -> {
                if (zoneType.equals("WORK")) yield "Returning Home";
                yield "Evening Activity";
            }
            default -> "Night Activity";
        };
    }

    public void updateBehaviorProfile(String userId, String detectedIntent) {
        var profileOpt = profileRepository.findByUserId(userId);
        // Behavior profile update logic would go here
        log.debug("Updated behavior profile for user {}: intent = {}",
                userId, detectedIntent);
    }
}
