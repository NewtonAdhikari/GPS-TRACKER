package com.cognitivegps.repository;

import com.cognitivegps.model.BehaviorProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BehaviorProfileRepository extends MongoRepository<BehaviorProfile, String> {
    Optional<BehaviorProfile> findByUserId(String userId);
}
