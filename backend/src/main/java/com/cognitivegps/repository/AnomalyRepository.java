package com.cognitivegps.repository;

import com.cognitivegps.model.Anomaly;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnomalyRepository extends MongoRepository<Anomaly, String> {
    List<Anomaly> findByUserIdOrderByTimestampDesc(String userId);
}
