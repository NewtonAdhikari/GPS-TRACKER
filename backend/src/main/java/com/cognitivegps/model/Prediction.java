package com.cognitivegps.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "predictions")
public class Prediction {
    @Id
    private String id;
    private String userId;
    private String currentZone;
    private List<String> predictedPath;
    private Double probability;
    private String confidenceLevel;
    private List<String> reasons;
    private Instant timestamp;
}
