package com.rsa.netwitness.presidio.automation.domain.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.rsa.netwitness.presidio.automation.domain.authentication.OutputAuthenticationEnrichStoredData;

import java.time.Instant;
import java.util.List;


public interface OutputAuthenticationStoredDataRepository extends MongoRepository<OutputAuthenticationEnrichStoredData, String> {

    @Query("{ 'eventDate': { $gte: ?0 }, $and: [ { 'eventDate': { $lte : ?1 } }, { $and: [ { 'userId': ?2 } ] } ] }")
    List<OutputAuthenticationEnrichStoredData> findByTimeAndUser(Instant start, Instant end, String username);

    @Query("{ 'eventDate': { $gte: ?0 }, $and: [ { 'eventDate': { $lt: ?1 } } ] }")
    List<OutputAuthenticationEnrichStoredData> findByTime(Instant start, Instant end);

    @Query("{ 'eventDate': { $gte: ?0 }, $and: [ { 'eventDate': { $lt: ?1 } } ] }")
    List<OutputAuthenticationEnrichStoredData> findByTime(Instant start, Instant end, Sort sort);

    @Query (value = "{}", count=true )
    Long countAll();
}
