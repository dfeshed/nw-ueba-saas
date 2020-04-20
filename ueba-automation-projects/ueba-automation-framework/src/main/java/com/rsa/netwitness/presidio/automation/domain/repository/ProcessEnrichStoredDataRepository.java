package com.rsa.netwitness.presidio.automation.domain.repository;

import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.rsa.netwitness.presidio.automation.domain.process.ProcessEnrichStoredData;

import java.time.Instant;
import java.util.List;


public interface ProcessEnrichStoredDataRepository extends MongoRepository<ProcessEnrichStoredData, String> {

    @Query ("{ 'startInstant': { $gte: ?0 }, $and: [ { 'startInstant': { $lt: ?1 } }, { $and: [ { 'userId': ?2 } ] } ] }")
    List<ProcessEnrichStoredData> findByTimeAndUser(Instant start, Instant end, String username);

    @Query ("{ 'startInstant': { $gte: ?0 }, $and: [ { 'startInstant': { $lt: ?1 } } ] }")
    List<ProcessEnrichStoredData> findByTime(Instant start, Instant end);

    @CountQuery("{ 'startInstant': { $gte: ?0 }, $and: [ { 'startInstant': { $lt: ?1 } } ] }")
    long countByTime(Instant start, Instant end);

    @Query("{ userId: ?0, operationType: ?1 }")
    List<ProcessEnrichStoredData> findByUserAndOperationType(String userId, String operationType);

}
