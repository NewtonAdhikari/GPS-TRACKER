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
@Document(collection = "simulations")
public class Simulation {
    @Id
    private String id;
    private String userId;
    private List<String> simulatedPath;
    private Double confidence;
    private Instant timestamp;
}
