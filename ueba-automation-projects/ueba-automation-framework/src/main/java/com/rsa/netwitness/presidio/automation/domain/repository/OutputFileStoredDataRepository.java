package com.rsa.netwitness.presidio.automation.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.rsa.netwitness.presidio.automation.domain.file.OutputFileEnrichStoredData;

import java.time.Instant;
import java.util.List;


public interface OutputFileStoredDataRepository extends MongoRepository<OutputFileEnrichStoredData, String> {

    @Query ("{ 'eventDate': { $gte: ?0 }, $and: [ { 'eventDate': { $lte : ?1 } }, { $and: [ { 'userId': ?2 } ] } ] }")
    List<OutputFileEnrichStoredData> findByTimeAndUser(Instant start, Instant end, String username);

    @Query ("{ 'eventDate': { $gte: ?0 }, $and: [ { 'eventDate': { $lt: ?1 } } ] }")
    List<OutputFileEnrichStoredData> findByTime(Instant start, Instant end);

    @Query (value = "{}", count=true )
    Long countAll();
}
