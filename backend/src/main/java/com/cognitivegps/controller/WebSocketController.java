package com.cognitivegps.controller;

import com.cognitivegps.config.WebSocketConfig;
import com.cognitivegps.dto.LocationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://127.0.0.1:5173"})
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/location")
    @SendTo("/topic/location")
    public Map<String, Object> handleLocationUpdate(LocationRequest request) {
        return Map.of(
                "userId", request.getUserId(),
                "latitude", request.getLatitude(),
                "longitude", request.getLongitude(),
                "timestamp", System.currentTimeMillis()
        );
    }

    public void broadcastLocation(String userId, double lat, double lng) {
        messagingTemplate.convertAndSend("/topic/location/" + userId,
                Map.of("latitude", lat, "longitude", lng,
                        "timestamp", System.currentTimeMillis()));
    }

    public void broadcastPrediction(String userId, Object prediction) {
        messagingTemplate.convertAndSend("/topic/prediction/" + userId, prediction);
    }

    public void broadcastAlert(String userId, Object alert) {
        messagingTemplate.convertAndSend("/topic/alerts/" + userId, alert);
    }
}
