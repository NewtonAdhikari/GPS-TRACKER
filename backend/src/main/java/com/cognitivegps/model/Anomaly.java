package com.cognitivegps.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "anomalies")
public class Anomaly {
    @Id
    private String id;
    private String userId;
    private AnomalyType type;
    private String description;
    private String severity;
    private Instant timestamp;
    private Double[] location;

    public enum AnomalyType {
        SPOOF, DEVIATION, STOP
    }
}
