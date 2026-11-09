package com.cognitivegps.service;

import com.cognitivegps.dto.ExplanationResponse;
import com.cognitivegps.dto.PredictionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExplainableAIService {

    private final PredictionService predictionService;
    private final TrustScoreService trustScoreService;

    public ExplanationResponse explain(String userId) {
        PredictionResponse prediction = predictionService.predict(userId);

        String primaryReason = generatePrimaryReason(prediction);
        List<String> secondaryFactors = generateSecondaryFactors(prediction);
        String confidenceLevel = prediction.getConfidenceLevel();

        return ExplanationResponse.builder()
                .prediction(formatPrediction(prediction))
                .primaryReason(primaryReason)
                .secondaryFactors(secondaryFactors)
                .confidenceLevel(confidenceLevel)
                .build();
    }

    private String generatePrimaryReason(PredictionResponse prediction) {
        if (prediction.getPredictedPath().isEmpty()) {
            return "Insufficient historical data to establish strong patterns";
        }

        double probability = prediction.getProbability();
        if (probability >= 0.7) {
            return String.format("Strong transition pattern detected (%.0f%% confidence) to %s",
                    probability * 100, prediction.getPredictedPath().get(0));
        } else if (probability >= 0.4) {
            return String.format("Moderate pattern match (%.0f%% confidence) suggesting movement to %s",
                    probability * 100, prediction.getPredictedPath().get(0));
        }

        return "Weak pattern match - this is an unusual route based on your history";
    }

    private List<String> generateSecondaryFactors(PredictionResponse prediction) {
        List<String> factors = new ArrayList<>();

        factors.addAll(prediction.getReasons());

        if (prediction.getConfidenceLevel().equals("LOW")) {
            factors.add("Low confidence - more data needed for accurate prediction");
        }

        factors.add(String.format("Predicted path length: %d steps",
                prediction.getPredictedPath().size()));

        return factors;
    }

    private String formatPrediction(PredictionResponse prediction) {
        if (prediction.getPredictedPath().isEmpty()) {
            return "Unable to predict next destination";
        }

        return "You are likely heading to: " +
                String.join(" -> ", prediction.getPredictedPath());
    }
}
