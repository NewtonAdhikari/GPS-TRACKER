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
@Document(collection = "alerts")
public class Alert {
    @Id
    private String id;
    private String userId;
    private AlertType type;
    private String message;
    private String severity;
    private Instant timestamp;

    public enum AlertType {
        NO_MOVEMENT, UNUSUAL_PATTERN, EMERGENCY
    }
}
