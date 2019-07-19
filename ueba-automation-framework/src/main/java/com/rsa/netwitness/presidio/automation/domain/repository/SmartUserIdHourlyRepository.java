package com.rsa.netwitness.presidio.automation.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.rsa.netwitness.presidio.automation.domain.output.SmartUserIdStoredRecored;

import java.util.List;

public interface SmartUserIdHourlyRepository extends MongoRepository<SmartUserIdStoredRecored, String>  {


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



}
