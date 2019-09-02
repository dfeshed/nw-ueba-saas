package com.rsa.netwitness.presidio.automation.domain.repository;

import com.rsa.netwitness.presidio.automation.domain.process.AdapterProcessStoredData;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

// TODO : change AdapterFileStoredData to AdapterProcessStoredData, when ready
public interface AdapterProcessStoredDataRepository extends MongoRepository<AdapterProcessStoredData, String> {
    @CountQuery("{ 'dateTime': { $gte: ?0 }, $and: [ { 'dateTime': { $lt: ?1 } } ] }")
    long countByTime(Instant start, Instant end);

    @Query("{ 'dateTime': { $gte: ?0 }, $and: [ { 'dateTime': { $lt: ?1 } } ] }")
    List<AdapterProcessStoredData> findByTime(Instant start, Instant end);

    AdapterProcessStoredData findByEventId(String eventId);

    Long countByDataSource(String dataSource);

    AdapterProcessStoredData findFirstByOrderByDateTimeDesc();
}
