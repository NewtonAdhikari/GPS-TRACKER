package com.cognitivegps.service;

import com.cognitivegps.dto.ChatRequest;
import com.cognitivegps.dto.ChatResponse;
import com.cognitivegps.dto.PredictionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final PredictionService predictionService;
    private final ExplainableAIService explainableAIService;
    private final IntentDetectionService intentDetectionService;
    private final ZoneClusteringService zoneClusteringService;

    private static final Pattern WHERE_PATTERN =
            Pattern.compile("(?i).*(where|going|destination|next).*");
    private static final Pattern WHY_PATTERN =
            Pattern.compile("(?i).*(why|reason|because|explain).*");
    private static final Pattern ROUTINE_PATTERN =
            Pattern.compile("(?i).*(routine|habit|pattern|schedule).*");
    private static final Pattern INTENT_PATTERN =
            Pattern.compile("(?i).*(intent|why am I|what.*doing).*");
    private static final Pattern ZONE_PATTERN =
            Pattern.compile("(?i).*(zone|visited|place|location).*");

    public ChatResponse processMessage(ChatRequest request) {
        String message = request.getMessage().toLowerCase();
        String userId = request.getUserId();

        if (WHERE_PATTERN.matcher(message).matches()) {
            return handleWhereQuery(userId);
        }

        if (WHY_PATTERN.matcher(message).matches()) {
            return handleWhyQuery(userId);
        }

        if (ROUTINE_PATTERN.matcher(message).matches()) {
            return handleRoutineQuery(userId);
        }

        if (INTENT_PATTERN.matcher(message).matches()) {
            return handleIntentQuery(userId);
        }

        if (ZONE_PATTERN.matcher(message).matches()) {
            return handleZoneQuery(userId);
        }

        return new ChatResponse(
                "I can answer questions about your predictions, routines, intent, and zones. " +
                        "Try asking 'Where am I going?' or 'Why this prediction?'",
                "HELP"
        );
    }

    private ChatResponse handleWhereQuery(String userId) {
        PredictionResponse prediction = predictionService.predict(userId);

        String response;
        if (prediction.getPredictedPath().isEmpty()) {
            response = "I don't have enough data to predict your next destination yet. " +
                    "Keep moving and I'll learn your patterns!";
        } else {
            response = String.format("Based on your movement patterns, you're likely heading to: %s",
                    String.join(" -> ", prediction.getPredictedPath()));
        }

        return new ChatResponse(response, "PREDICTION");
    }

    private ChatResponse handleWhyQuery(String userId) {
        var explanation = explainableAIService.explain(userId);

        String response = String.format("%s\n\nPrimary Reason: %s\n\nConfidence: %s",
                explanation.getPrediction(),
                explanation.getPrimaryReason(),
                explanation.getConfidenceLevel());

        return new ChatResponse(response, "EXPLANATION");
    }

    private ChatResponse handleRoutineQuery(String userId) {
        var zones = zoneClusteringService.getUserZones(userId);

        if (zones.isEmpty()) {
            return new ChatResponse(
                    "I haven't learned your routine yet. Keep using the app to help me understand your patterns!",
                    "ROUTINE"
            );
        }

        StringBuilder response = new StringBuilder("Your frequent zones:\n");
        zones.stream()
                .sorted((a, b) -> b.getVisitCount() - a.getVisitCount())
                .limit(5)
                .forEach(zone -> response.append(String.format("- %s (visited %d times)\n",
                        zone.getZoneName(), zone.getVisitCount())));

        return new ChatResponse(response.toString(), "ROUTINE");
    }

    private ChatResponse handleIntentQuery(String userId) {
        String intent = intentDetectionService.detectIntent(userId);
        String response = String.format("Your current detected intent: %s", intent);
        return new ChatResponse(response, "INTENT");
    }

    private ChatResponse handleZoneQuery(String userId) {
        var zones = zoneClusteringService.getUserZones(userId);
        var currentZone = zoneClusteringService.getCurrentZone(userId);

        StringBuilder response = new StringBuilder();
        if (currentZone != null) {
            response.append(String.format("You are currently in: %s\n\n",
                    currentZone.getZoneName()));
        }

        if (!zones.isEmpty()) {
            response.append("Your zones:\n");
            zones.forEach(zone -> response.append(String.format("- %s\n", zone.getZoneName())));
        }

        return new ChatResponse(
                response.length() > 0 ? response.toString() : "No zone data available",
                "ZONES"
        );
    }
}
