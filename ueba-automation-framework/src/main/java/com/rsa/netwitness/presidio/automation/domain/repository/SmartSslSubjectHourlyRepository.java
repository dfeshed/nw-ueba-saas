package com.rsa.netwitness.presidio.automation.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.rsa.netwitness.presidio.automation.domain.output.SmartSslSubjectHourly;

import java.time.Instant;
import java.util.List;

public interface SmartSslSubjectHourlyRepository extends MongoRepository<SmartSslSubjectHourly, String>  {


    @Query("{ 'smartScore': { $gte: 50.0 } }")
    List<SmartSslSubjectHourly> findAllBiggerThan50();

    @Query("{ 'smartScore': { $gte: ?0 } }")
    List<SmartSslSubjectHourly> findAllBiggerThanRequestedScore(Double score);

    @Query("{ 'contextId': ?0 }")
    List<SmartSslSubjectHourly> findByContextId(String contextId);

    @Query("{ 'smartScore': { $gte: 50.0 }, $and: [ { 'contextId': ?0 } ] }")
    List<SmartSslSubjectHourly> findByContextIdAndGreaterThan50(String contextId);

    @Query("{ 'smartScore': { $gte: ?0 }, $and: [ { 'contextId': ?1 } ] }")
    List<SmartSslSubjectHourly> findByContextIdAndRequestedScore(Double score, String contextId);

    SmartSslSubjectHourly findFirstByOrderByCreatedDateAsc();

    @Query("{ 'dateTime': { $gte: ?0 }, $and: [ { 'dateTime': { $lt: ?1 } } ] }")
    List<SmartSslSubjectHourly> findByStartInstant(Instant start, Instant end);

}
