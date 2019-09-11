package com.rsa.netwitness.presidio.automation.domain.repository;

import com.rsa.netwitness.presidio.automation.domain.output.SmartUserIdStoredRecored;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

public interface SmartUserIdHourlyRepository extends MongoRepository<SmartUserIdStoredRecored, String>  {

    @Query ("{ 'startInstant': { $gte: ?0 }, $and: [ { 'startInstant': { $lt: ?1 } } ] }")
    List<SmartUserIdStoredRecored> findByTime(Instant start, Instant end);

    @Query("{ 'smartScore': { $gte: 50.0 } }")
    List<SmartUserIdStoredRecored> findAllBiggerThan50();

    @Query("{ 'smartScore': { $gte: ?0 } }")
    List<SmartUserIdStoredRecored> findAllBiggerThanRequestedScore(Double score);

    @Query("{ 'contextId': ?0 }")
    List<SmartUserIdStoredRecored> findByContextId(String contextId);

    @Query("{ 'smartScore': { $gte: 50.0 }, $and: [ { 'contextId': ?0 } ] }")
    List<SmartUserIdStoredRecored> findByContextIdAndGreaterThan50(String contextId);

    @Query("{ 'smartScore': { $gte: ?0 }, $and: [ { 'contextId': ?1 } ] }")
    List<SmartUserIdStoredRecored> findByContextIdAndRequestedScore(Double score, String contextId);

    SmartUserIdStoredRecored findFirstByOrderByCreatedDateAsc();

    @Query("{ 'dateTime': { $gte: ?0 }, $and: [ { 'dateTime': { $lt: ?1 } } ] }")
    List<SmartUserIdStoredRecored> findByStartInstant(Instant start, Instant end);
}
