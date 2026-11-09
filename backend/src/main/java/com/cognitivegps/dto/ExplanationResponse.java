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
public class ExplanationResponse {
    private String prediction;
    private String primaryReason;
    private List<String> secondaryFactors;
    private String confidenceLevel;
}
