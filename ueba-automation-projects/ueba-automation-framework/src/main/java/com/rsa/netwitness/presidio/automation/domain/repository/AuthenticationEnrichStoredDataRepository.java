package com.rsa.netwitness.presidio.automation.domain.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.rsa.netwitness.presidio.automation.domain.authentication.EnrichedAuthenticationStoredData;

import java.time.Instant;
import java.util.List;


public interface AuthenticationEnrichStoredDataRepository extends MongoRepository<EnrichedAuthenticationStoredData, String> {

    @Query ("{ 'startInstant': { $gte: ?0 }, $and: [ { 'startInstant': { $lte: ?1 } }, { $and: [ { 'userId': ?2 } ] } ] }")
    List<EnrichedAuthenticationStoredData> findByTimeAndUser(Instant start, Instant end, String username);

    @Query ("{ 'startInstant': { $gte: ?0 }, $and: [ { 'startInstant': { $lt: ?1 } } ] }")
    List<EnrichedAuthenticationStoredData> findByTime(Instant start, Instant end);

    @Query ("{ 'startInstant': { $gte: ?0 }, $and: [ { 'startInstant': { $lt: ?1 } } ] }")
    List<EnrichedAuthenticationStoredData> findByTime(Instant start, Instant end, Sort sort);

    @Query("{ userId: ?0, operationType: ?1 }")
    List<EnrichedAuthenticationStoredData> findByUserAndOperationType(String userId, String operationType);

}
