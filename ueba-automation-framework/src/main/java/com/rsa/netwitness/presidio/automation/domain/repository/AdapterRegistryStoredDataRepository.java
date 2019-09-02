package com.rsa.netwitness.presidio.automation.domain.repository;

import com.rsa.netwitness.presidio.automation.domain.process.AdapterRegistryStoredData;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

public interface AdapterRegistryStoredDataRepository extends MongoRepository<AdapterRegistryStoredData, String> {
    @CountQuery("{ 'dateTime': { $gte: ?0 }, $and: [ { 'dateTime': { $lt: ?1 } } ] }")
    long countByTime(Instant start, Instant end);

    @Query("{ 'dateTime': { $gte: ?0 }, $and: [ { 'dateTime': { $lt: ?1 } } ] }")
    List<AdapterRegistryStoredData> findByTime(Instant start, Instant end);

    AdapterRegistryStoredData findByEventId(String eventId);

    Long countByDataSource(String dataSource);

    AdapterRegistryStoredData findTopByDateTimeBetween(Instant from, Instant to, Sort sort);
}
