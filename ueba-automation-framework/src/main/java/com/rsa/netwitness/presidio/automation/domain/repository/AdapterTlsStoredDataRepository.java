package com.rsa.netwitness.presidio.automation.domain.repository;

import com.rsa.netwitness.presidio.automation.domain.process.AdapterRegistryStoredData;
import com.rsa.netwitness.presidio.automation.domain.tls.AdapterTlsStoredData;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;

public interface AdapterTlsStoredDataRepository extends MongoRepository<AdapterTlsStoredData, String> {
    @CountQuery("{ 'dateTime': { $gte: ?0 }, $and: [ { 'dateTime': { $lt: ?1 } } ] }")
    long findByTime(Instant start, Instant end);

    AdapterRegistryStoredData findByEventId(String eventId);

    Long countByDataSource(String dataSource);

    AdapterRegistryStoredData findTopByDateTimeBetween(Instant from, Instant to, Sort sort);
}
