package com.rsa.netwitness.presidio.automation.domain.repository;

import com.rsa.netwitness.presidio.automation.domain.registry.RegistryEnrichStoredData;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;


public interface RegistryEnrichStoredDataRepository extends MongoRepository<RegistryEnrichStoredData, String> {

    @Query ("{ 'startInstant': { $gte: ?0 }, $and: [ { 'startInstant': { $lte: ?1 } }, { $and: [ { 'userId': ?2 } ] } ] }")
    List<RegistryEnrichStoredData> findByTimeAndUser(Instant start, Instant end, String username);

    @Query ("{ 'startInstant': { $gte: ?0 }, $and: [ { 'startInstant': { $lt: ?1 } } ] }")
    List<RegistryEnrichStoredData> findByTime(Instant start, Instant end);

    @CountQuery("{ 'startInstant': { $gte: ?0 }, $and: [ { 'startInstant': { $lt: ?1 } } ] }")
    long countByTime(Instant start, Instant end);

    @Query("{ userId: ?0, operationType: ?1 }")
    List<RegistryEnrichStoredData> findByUserAndOperationType(String userId, String operationType);

}
