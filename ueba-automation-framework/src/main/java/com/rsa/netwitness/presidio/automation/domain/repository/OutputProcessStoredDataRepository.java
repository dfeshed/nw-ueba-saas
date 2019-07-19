package com.rsa.netwitness.presidio.automation.domain.repository;

import com.rsa.netwitness.presidio.automation.domain.process.OutputProcessEnrichedStoredData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;


public interface OutputProcessStoredDataRepository extends MongoRepository<OutputProcessEnrichedStoredData, String> {

    @Query ("{ 'eventDate': { $gte: ?0 }, $and: [ { 'eventDate': { $lte : ?1 } }, { $and: [ { 'userId': ?2 } ] } ] }")
    List<OutputProcessEnrichedStoredData> findByTimeAndUser(Instant start, Instant end, String username);

    @Query ("{ 'eventDate': { $gte: ?0 }, $and: [ { 'eventDate': { $lt: ?1 } } ] }")
    List<OutputProcessEnrichedStoredData> findByTime(Instant start, Instant end);

    @Query (value = "{}", count=true )
    Long countAll();
}
