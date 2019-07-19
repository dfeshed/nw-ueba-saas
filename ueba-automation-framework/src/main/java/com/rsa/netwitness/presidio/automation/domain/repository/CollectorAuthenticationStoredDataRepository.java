package com.rsa.netwitness.presidio.automation.domain.repository;

import com.rsa.netwitness.presidio.automation.domain.authentication.CollectorAuthenticationStoredData;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

public interface CollectorAuthenticationStoredDataRepository extends MongoRepository<CollectorAuthenticationStoredData, String> {

    @Query("{ userSID : ?0 }")
    List<CollectorAuthenticationStoredData> findByUserSID(String userSID);

    @Query("{ userSID : ?0 }")
    List<CollectorAuthenticationStoredData> findBySID(String userSID, Sort sort);

    @CountQuery("{ 'timeDetected': { $gte: ?0 }, $and: [ { 'timeDetected': { $lt: ?1 } } ] }")
    long findByTime(Instant start, Instant end);

    @Query("{ userSID: ?0, event: ?1 }")
    List<CollectorAuthenticationStoredData> findByUserAndEvent(String userSID, String event);

    @Query("{}")
    CollectorAuthenticationStoredData findOneEvent(Sort sort);
}