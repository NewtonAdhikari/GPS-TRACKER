package com.cognitivegps.controller;

import com.cognitivegps.dto.PredictionResponse;
import com.cognitivegps.dto.ExplanationResponse;
import com.cognitivegps.service.PredictionService;
import com.cognitivegps.service.ExplainableAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://127.0.0.1:5173"})
public class PredictionController {

    private final PredictionService predictionService;
    private final ExplainableAIService explainableAIService;

    @GetMapping("/predict")
    public ResponseEntity<PredictionResponse> predict(
            @RequestParam String userId) {
        return ResponseEntity.ok(predictionService.predict(userId));
    }

    @GetMapping("/explain")
    public ResponseEntity<ExplanationResponse> explain(
            @RequestParam String userId) {
        return ResponseEntity.ok(explainableAIService.explain(userId));
    }
}
