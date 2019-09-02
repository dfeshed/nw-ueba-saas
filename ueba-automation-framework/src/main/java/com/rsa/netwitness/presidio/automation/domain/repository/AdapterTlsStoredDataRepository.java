package com.rsa.netwitness.presidio.automation.domain.repository;

import com.rsa.netwitness.presidio.automation.domain.tls.AdapterTlsStoredData;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

public interface AdapterTlsStoredDataRepository extends MongoRepository<AdapterTlsStoredData, String> {
    @CountQuery("{ 'dateTime': { $gte: ?0 }, $and: [ { 'dateTime': { $lt: ?1 } } ] }")
    long countByTime(Instant start, Instant end);

    @Query("{ 'dateTime': { $gte: ?0 }, $and: [ { 'dateTime': { $lt: ?1 } } ] }")
    List<AdapterTlsStoredData> findByTime(Instant start, Instant end);

    AdapterTlsStoredData findByEventId(String eventId);

    Long countByDataSource(String dataSource);

    AdapterTlsStoredData findTopByDateTimeBetween(Instant from, Instant to, Sort sort);
}
