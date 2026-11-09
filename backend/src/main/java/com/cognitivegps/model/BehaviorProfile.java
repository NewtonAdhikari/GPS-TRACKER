package com.cognitivegps.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "behavior_profiles")
public class BehaviorProfile {
    @Id
    private String id;
    private String userId;
    private Map<String, String> timePatterns;
    private Map<String, String> intentPatterns;
    private Map<String, Integer> frequentZones;
}
