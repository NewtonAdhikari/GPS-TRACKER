package com.cognitivegps.repository;

import com.cognitivegps.model.TransitionMatrix;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransitionMatrixRepository extends MongoRepository<TransitionMatrix, String> {
    Optional<TransitionMatrix> findByUserId(String userId);
}
