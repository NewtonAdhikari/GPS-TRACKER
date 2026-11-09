package com.cognitivegps.service;

import com.cognitivegps.dto.PredictionResponse;
import com.cognitivegps.model.DynamicZone;
import com.cognitivegps.model.Prediction;
import com.cognitivegps.model.UserLocation;
import com.cognitivegps.repository.PredictionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PredictionService {

    private final PredictionRepository predictionRepository;
    private final ZoneClusteringService zoneClusteringService;
    private final TransitionService transitionService;

    public PredictionResponse predict(String userId) {
        DynamicZone currentZone = zoneClusteringService.getCurrentZone(userId);

        if (currentZone == null) {
            return PredictionResponse.builder()
                    .currentZone("UNKNOWN")
                    .predictedPath(List.of())
                    .probability(0.0)
                    .confidenceLevel("LOW")
                    .reasons(List.of("Insufficient data for prediction"))
                    .build();
        }

        List<String> predictedPath = transitionService.predictPath(
                userId, currentZone.getZoneName(), 3);

        double probability = calculateTransitionProbability(
                userId, currentZone.getZoneName(), predictedPath.isEmpty() ? null : predictedPath.get(0));

        String confidence = calculateConfidence(predictedPath.size());

        Prediction prediction = Prediction.builder()
                .userId(userId)
                .currentZone(currentZone.getZoneName())
                .predictedPath(predictedPath)
                .probability(probability)
                .confidenceLevel(confidence)
                .reasons(generateReasons(currentZone, predictedPath))
                .timestamp(Instant.now())
                .build();

        predictionRepository.save(prediction);

        return PredictionResponse.builder()
                .currentZone(currentZone.getZoneName())
                .predictedPath(predictedPath)
                .probability(probability)
                .confidenceLevel(confidence)
                .reasons(generateReasons(currentZone, predictedPath))
                .build();
    }

    private double calculateTransitionProbability(String userId, String fromZone, String toZone) {
        if (toZone == null) return 0.0;

        var matrix = transitionService.getTransitionMatrix(userId);
        if (!matrix.getTransitions().containsKey(fromZone)) return 0.0;

        int totalTransitions = matrix.getTransitions().get(fromZone).values().stream()
                .mapToInt(Integer::intValue).sum();

        int targetTransition = matrix.getTransitions().get(fromZone).getOrDefault(toZone, 0);

        return (double) targetTransition / totalTransitions;
    }

    private String calculateConfidence(int pathLength) {
        if (pathLength >= 3) return "HIGH";
        if (pathLength >= 1) return "MEDIUM";
        return "LOW";
    }

    private List<String> generateReasons(DynamicZone currentZone, List<String> predictedPath) {
        List<String> reasons = new ArrayList<>();
        reasons.add("Pattern-based prediction from movement history");

        LocalTime now = LocalTime.now();
        if (now.getHour() >= 8 && now.getHour() <= 10) {
            reasons.add("Morning rush hour - typical commute pattern");
        } else if (now.getHour() >= 17 && now.getHour() <= 19) {
            reasons.add("Evening rush hour - typical return pattern");
        }

        if (currentZone.getVisitCount() > 10) {
            reasons.add("High visit frequency to current zone (" +
                    currentZone.getVisitCount() + " visits)");
        }

        if (!predictedPath.isEmpty()) {
            reasons.add("Strong transition pattern to " + predictedPath.get(0));
        }

        return reasons;
    }
}
