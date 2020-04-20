package com.rsa.netwitness.presidio.automation.domain.repository;

import com.rsa.netwitness.presidio.automation.domain.dlpfile.DlpFileEnrichStoredData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;


public interface DlpFileEnrichStoredDataRepository extends MongoRepository<DlpFileEnrichStoredData, String> {

    @Query ("{ 'startInstant': { $gte: ?0 }, $and: [ { 'startInstant': { $lt: ?1 } }, { $and: [ { 'normalizedUsername': ?2 } ] } ] }")
    List<DlpFileEnrichStoredData> findByTimeAndUser(Instant start, Instant end, String username);
}
