package com.cognitivegps.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponse {
    private String id;
    private String userId;
    private Double longitude;
    private Double latitude;
    private Instant timestamp;
    private Double speed;
}
