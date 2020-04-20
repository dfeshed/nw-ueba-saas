package com.rsa.netwitness.presidio.automation.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.rsa.netwitness.presidio.automation.domain.registry.OutputRegistryEnrichedStoredData;

import java.time.Instant;
import java.util.List;


public interface OutputRegistryStoredDataRepository extends MongoRepository<OutputRegistryEnrichedStoredData, String> {

    @Query ("{ 'eventDate': { $gte: ?0 }, $and: [ { 'eventDate': { $lte : ?1 } }, { $and: [ { 'userId': ?2 } ] } ] }")
    List<OutputRegistryEnrichedStoredData> findByTimeAndUser(Instant start, Instant end, String username);

    @Query ("{ 'eventDate': { $gte: ?0 }, $and: [ { 'eventDate': { $lt: ?1 } } ] }")
    List<OutputRegistryEnrichedStoredData> findByTime(Instant start, Instant end);

    @Query (value = "{}", count=true )
    Long countAll();
}
