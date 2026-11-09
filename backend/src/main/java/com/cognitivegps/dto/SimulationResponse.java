package com.cognitivegps.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationResponse {
    private List<String> simulatedPath;
    private Double confidence;
    private List<String> possibleRoutes;
}
