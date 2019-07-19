package com.rsa.netwitness.presidio.automation.domain.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.rsa.netwitness.presidio.automation.domain.authentication.AdapterAuthenticationStoredData;

import java.time.Instant;

public interface AdapterAuthenticationStoredDataRepository extends MongoRepository<AdapterAuthenticationStoredData, String> {

    @CountQuery("{ 'dateTime': { $gte: ?0 }, $and: [ { 'dateTime': { $lt: ?1 } } ] }")
    long findByTime(Instant start, Instant end);

    AdapterAuthenticationStoredData findByEventId(String eventId);

    Long countByDataSource(String dataSource);

    AdapterAuthenticationStoredData findTopByDateTimeBetween(Instant from, Instant to, Sort sort);

}
