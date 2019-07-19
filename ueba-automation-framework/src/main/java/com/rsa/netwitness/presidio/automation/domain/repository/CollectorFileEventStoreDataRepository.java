package com.rsa.netwitness.presidio.automation.domain.repository;


import com.rsa.netwitness.presidio.automation.domain.file.CollectorFileStoredData;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

public interface CollectorFileEventStoreDataRepository extends MongoRepository<CollectorFileStoredData, String> {

    @Query("{ userSID : ?0 }")
    List<CollectorFileStoredData> findbyUserSID(String userSID);

    @Query("{ userSID :?0 }")
    List<CollectorFileStoredData> findBySID(String userSID, Sort sort);

    @Query("{ from : ?0 }")
    List<CollectorFileStoredData> findbyFromPath(String fromPath);

    @CountQuery("{ 'timeDetected': { $gte: ?0 }, $and: [ { 'timeDetected': { $lt: ?1 } } ] }")
    long findByTime(Instant start, Instant end);

    @Query("{ userSID: ?0, event: ?1 }")
    List<CollectorFileStoredData> findByUserAndEvent(String userSID, String event);

    @Query("{}")
    CollectorFileStoredData findOneEvent(Sort sort);
}