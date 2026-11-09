package com.cognitivegps.repository;

import com.cognitivegps.model.DynamicZone;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DynamicZoneRepository extends MongoRepository<DynamicZone, String> {
    List<DynamicZone> findByUserId(String userId);
    Optional<DynamicZone> findByUserIdAndZoneId(String userId, String zoneId);
}
