package com.cognitivegps.controller;

import com.cognitivegps.dto.SimulationResponse;
import com.cognitivegps.model.Simulation;
import com.cognitivegps.repository.SimulationRepository;
import com.cognitivegps.service.SimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://127.0.0.1:5173"})
public class SimulationController {

    private final SimulationService simulationService;
    private final SimulationRepository simulationRepository;

    @GetMapping("/simulate")
    public ResponseEntity<SimulationResponse> simulate(
            @RequestParam String userId,
            @RequestParam(defaultValue = "2") int hours) {

        List<String> simulatedPath = simulationService.simulateFuturePath(userId, hours);
        double confidence = simulationService.calculateSimulationConfidence(userId);

        Simulation simulation = Simulation.builder()
                .userId(userId)
                .simulatedPath(simulatedPath)
                .confidence(confidence)
                .timestamp(java.time.Instant.now())
                .build();

        simulationRepository.save(simulation);

        return ResponseEntity.ok(SimulationResponse.builder()
                .simulatedPath(simulatedPath)
                .confidence(confidence)
                .possibleRoutes(simulationService.generateAlternativeRoutes(userId, null, 3))
                .build());
    }
}
