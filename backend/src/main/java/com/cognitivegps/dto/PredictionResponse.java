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
public class PredictionResponse {
    private String currentZone;
    private List<String> predictedPath;
    private Double probability;
    private String confidenceLevel;
    private List<String> reasons;
}
