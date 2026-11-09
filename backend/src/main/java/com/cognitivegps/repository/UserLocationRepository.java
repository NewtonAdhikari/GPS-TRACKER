package com.cognitivegps.repository;

import com.cognitivegps.model.UserLocation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface UserLocationRepository extends MongoRepository<UserLocation, String> {
    List<UserLocation> findByUserIdOrderByTimestampDesc(String userId);
    List<UserLocation> findByUserIdAndTimestampBetweenOrderByTimestampAsc(
        String userId, Instant start, Instant end);
}
