package com.rsa.netwitness.presidio.automation.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.rsa.netwitness.presidio.automation.domain.activedirectory.OutputActiveDirectoryEnrichedStoredData;

import java.time.Instant;
import java.util.List;


public interface OutputActiveDirectoryStoredDataRepository extends MongoRepository<OutputActiveDirectoryEnrichedStoredData, String> {

    @Query ("{ 'eventDate': { $gte: ?0 }, $and: [ { 'eventDate': { $lte : ?1 } }, { $and: [ { 'userId': ?2 } ] } ] }")
    List<OutputActiveDirectoryEnrichedStoredData> findByTimeAndUser(Instant start, Instant end, String username);

    @Query ("{ 'eventDate': { $gte: ?0 }, $and: [ { 'eventDate': { $lt: ?1 } } ] }")
    List<OutputActiveDirectoryEnrichedStoredData> findByTime(Instant start, Instant end);

    @Query (value = "{}", count=true )
    Long countAll();
}
