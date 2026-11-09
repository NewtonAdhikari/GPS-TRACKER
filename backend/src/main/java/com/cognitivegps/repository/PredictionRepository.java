package com.cognitivegps.repository;

import com.cognitivegps.model.Prediction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PredictionRepository extends MongoRepository<Prediction, String> {
    Optional<Prediction> findFirstByUserIdOrderByTimestampDesc(String userId);
}
