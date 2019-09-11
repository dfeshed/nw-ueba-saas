package com.rsa.netwitness.presidio.automation.domain.repository;

import com.rsa.netwitness.presidio.automation.domain.output.SmartJa3Hourly;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

public interface SmartJa3HourlyRepository extends MongoRepository<SmartJa3Hourly, String>  {


    @Query("{ 'smartScore': { $gte: 50.0 } }")
    List<SmartJa3HourlyRepository> findAllBiggerThan50();

    @Query("{ 'smartScore': { $gte: ?0 } }")
    List<SmartJa3HourlyRepository> findAllBiggerThanRequestedScore(Double score);

    @Query("{ 'contextId': ?0 }")
    List<SmartJa3HourlyRepository> findByContextId(String contextId);

    @Query("{ 'smartScore': { $gte: 50.0 }, $and: [ { 'contextId': ?0 } ] }")
    List<SmartJa3HourlyRepository> findByContextIdAndGreaterThan50(String contextId);

    @Query("{ 'smartScore': { $gte: ?0 }, $and: [ { 'contextId': ?1 } ] }")
    List<SmartJa3HourlyRepository> findByContextIdAndRequestedScore(Double score, String contextId);

    Instant findFirstByOrderByCreatedDateAsc();

    @Query("{ 'dateTime': { $gte: ?0 }, $and: [ { 'dateTime': { $lt: ?1 } } ] }")
    List<SmartJa3HourlyRepository> findByStartInstant(Instant start, Instant end);

}
