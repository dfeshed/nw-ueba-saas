package com.rsa.netwitness.presidio.automation.domain.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.rsa.netwitness.presidio.automation.domain.file.AdapterFileStoredData;

import java.time.Instant;

public interface AdapterFileStoredDataRepository extends MongoRepository<AdapterFileStoredData, String> {
    @CountQuery("{ 'dateTime': { $gte: ?0 }, $and: [ { 'dateTime': { $lt: ?1 } } ] }")
    long findByTime(Instant start, Instant end);


    AdapterFileStoredData findByEventId(String eventId);

    Long countByDataSource(String dataSource);

    AdapterFileStoredData findTopByDateTimeBetween(Instant from, Instant to, Sort sort);
}
