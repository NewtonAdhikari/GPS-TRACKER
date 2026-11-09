package com.cognitivegps.service;

import com.cognitivegps.model.DynamicZone;
import com.cognitivegps.model.TransitionMatrix;
import com.cognitivegps.repository.TransitionMatrixRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransitionService {

    private final TransitionMatrixRepository matrixRepository;

    public void recordTransition(String userId, String fromZone, String toZone) {
        TransitionMatrix matrix = matrixRepository.findByUserId(userId)
                .orElse(TransitionMatrix.builder()
                        .userId(userId)
                        .transitions(new HashMap<>())
                        .build());

        matrix.getTransitions()
                .computeIfAbsent(fromZone, k -> new HashMap<>())
                .merge(toZone, 1, Integer::sum);

        matrixRepository.save(matrix);
        log.debug("Recorded transition {} -> {} for user {}",
                fromZone, toZone, userId);
    }

    public TransitionMatrix getTransitionMatrix(String userId) {
        return matrixRepository.findByUserId(userId)
                .orElse(TransitionMatrix.builder()
                        .userId(userId)
                        .transitions(new HashMap<>())
                        .build());
    }

    public String predictNextZone(String userId, String currentZone) {
        TransitionMatrix matrix = getTransitionMatrix(userId);

        if (!matrix.getTransitions().containsKey(currentZone)) {
            return null;
        }

        Map<String, Integer> transitions = matrix.getTransitions().get(currentZone);
        return Collections.max(transitions.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public List<String> predictPath(String userId, String currentZone, int steps) {
        List<String> path = new ArrayList<>();
        String nextZone = currentZone;

        for (int i = 0; i < steps; i++) {
            nextZone = predictNextZone(userId, nextZone);
            if (nextZone == null) break;
            path.add(nextZone);
        }

        return path;
    }
}
