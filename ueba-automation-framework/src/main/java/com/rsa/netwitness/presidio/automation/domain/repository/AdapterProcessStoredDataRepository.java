package com.rsa.netwitness.presidio.automation.domain.repository;

import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.rsa.netwitness.presidio.automation.domain.process.AdapterProcessStoredData;

import java.time.Instant;

// TODO : change AdapterFileStoredData to AdapterProcessStoredData, when ready
public interface AdapterProcessStoredDataRepository extends MongoRepository<AdapterProcessStoredData, String> {
    @CountQuery("{ 'dateTime': { $gte: ?0 }, $and: [ { 'dateTime': { $lt: ?1 } } ] }")
    long findByTime(Instant start, Instant end);


    AdapterProcessStoredData findByEventId(String eventId);

    Long countByDataSource(String dataSource);

    AdapterProcessStoredData findFirstByOrderByDateTimeDesc();
}
