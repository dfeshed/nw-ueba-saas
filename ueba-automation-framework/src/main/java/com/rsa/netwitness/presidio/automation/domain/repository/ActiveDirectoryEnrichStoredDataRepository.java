package com.rsa.netwitness.presidio.automation.domain.repository;

import com.rsa.netwitness.presidio.automation.domain.activedirectory.ActiveDirectoryEnrichStoredData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;


public interface ActiveDirectoryEnrichStoredDataRepository extends MongoRepository<ActiveDirectoryEnrichStoredData, String> {

    @Query ("{ 'startInstant': { $gte: ?0 }, $and: [ { 'startInstant': { $lt: ?1 } }, { $and: [ { 'userId': ?2 } ] } ] }")
    List<ActiveDirectoryEnrichStoredData> findByTimeAndUser(Instant start, Instant end, String username);

    @Query ("{ 'startInstant': { $gte: ?0 }, $and: [ { 'startInstant': { $lt: ?1 } } ] }")
    List<ActiveDirectoryEnrichStoredData> findByTime(Instant start, Instant end);

    @Query("{ userId: ?0, operationType: ?1 }")
    List<ActiveDirectoryEnrichStoredData> findByUserAndOperationType(String userId, String operationType);

}
