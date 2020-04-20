package com.rsa.netwitness.presidio.automation.domain.repository;

import com.rsa.netwitness.presidio.automation.domain.activedirectory.CollectorActiveDirectoryStoredData;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

public interface CollectorActiveDirectoryStoredDataRepository extends MongoRepository<CollectorActiveDirectoryStoredData, String>{

    @Query("{ userDisplay : ?0 }")
    List<CollectorActiveDirectoryStoredData> findByUserDisplay(String userDisplay);

    @Query("{ userSID : ?0 }")
    List<CollectorActiveDirectoryStoredData> findByUserSID(String userSID);

    @Query("{ userSID :?0 }")
    List<CollectorActiveDirectoryStoredData> findBySID(String userSID, Sort sort);

    @CountQuery("{ 'timeDetected': { $gte: ?0 }, $and: [ { 'timeDetected': { $lt: ?1 } } ], 'event': { $eq: ?2 } }")
    long findByTimeAndEvent(Instant start, Instant end, String event);

    @CountQuery("{ 'timeDetected': { $gte: ?0 }, $and: [ { 'timeDetected': { $lt: ?1 } } ] }")
    long findByTime(Instant start, Instant end);

    @Query("{ userSID: ?0, event: ?1 }")
    List<CollectorActiveDirectoryStoredData> findByUserAndEvent(String userSID, String event);

    @Query("{}")
    CollectorActiveDirectoryStoredData findOneEvent(Sort sort);

}

