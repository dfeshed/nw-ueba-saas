package com.rsa.netwitness.presidio.automation.domain.repository;

import com.rsa.netwitness.presidio.automation.domain.activedirectory.ActiveDirectoryEnrichStoredData;
import com.rsa.netwitness.presidio.automation.domain.file.FileEnrichStoredData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;


public interface FileEnrichStoredDataRepository extends MongoRepository<FileEnrichStoredData, String> {

    @Query ("{ 'startInstant': { $gte: ?0 }, $and: [ { 'startInstant': { $lte : ?1 } }, { $and: [ { 'userId': ?2 } ] } ] }")
    List<FileEnrichStoredData> findByTimeAndUser(Instant start, Instant end, String username);

    @Query ("{ 'startInstant': { $gte: ?0 }, $and: [ { 'startInstant': { $lt: ?1 } } ] }")
    List<FileEnrichStoredData> findByTime(Instant start, Instant end);

    @Query("{ userId: ?0, operationType: ?1 }")
    List<FileEnrichStoredData> findByUserAndOperationType(String userId, String operationType);

    @Query("{ operationType: ?0 }")
    List<ActiveDirectoryEnrichStoredData> findByOperationType(String operationType);

    @Query("{ dataSource: ?0 }")
    List<ActiveDirectoryEnrichStoredData> findByDataSource(String dataSource);
}
