package com.cognitivegps.repository;

import com.cognitivegps.model.Simulation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SimulationRepository extends MongoRepository<Simulation, String> {
    Optional<Simulation> findFirstByUserIdOrderByTimestampDesc(String userId);
}
