package com.rsa.netwitness.presidio.automation.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.rsa.netwitness.presidio.automation.domain.output.SmartSslSubjectHourly;

import java.util.List;

public interface SmartSslSubjectHourlyRepository extends MongoRepository<SmartSslSubjectHourly, String>  {


    @Query("{ 'smartScore': { $gte: 50.0 } }")
    List<SmartSslSubjectHourlyRepository> findAllBiggerThan50();

    @Query("{ 'smartScore': { $gte: ?0 } }")
    List<SmartSslSubjectHourlyRepository> findAllBiggerThanRequestedScore(Double score);

    @Query("{ 'contextId': ?0 }")
    List<SmartSslSubjectHourlyRepository> findByContextId(String contextId);

    @Query("{ 'smartScore': { $gte: 50.0 }, $and: [ { 'contextId': ?0 } ] }")
    List<SmartSslSubjectHourlyRepository> findByContextIdAndGreaterThan50(String contextId);

    @Query("{ 'smartScore': { $gte: ?0 }, $and: [ { 'contextId': ?1 } ] }")
    List<SmartSslSubjectHourlyRepository> findByContextIdAndRequestedScore(Double score, String contextId);



}
