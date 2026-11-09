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
@Document(collection = "dynamic_zones")
public class DynamicZone {
    @Id
    private String id;
    private String userId;
    private String zoneId;
    private String zoneName;
    private Double[] centroid;
    private Double radius;
    private Integer visitCount;
    private Instant lastVisited;
    private String zoneType;
}
