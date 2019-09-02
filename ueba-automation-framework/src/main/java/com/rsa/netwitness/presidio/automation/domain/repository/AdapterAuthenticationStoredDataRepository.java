package com.rsa.netwitness.presidio.automation.domain.repository;

import com.rsa.netwitness.presidio.automation.domain.authentication.AdapterAuthenticationStoredData;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

public interface AdapterAuthenticationStoredDataRepository extends MongoRepository<AdapterAuthenticationStoredData, String> {

    @Query("{ 'dateTime': { $gte: ?0 }, $and: [ { 'dateTime': { $lt: ?1 } } ] }")
    List<AdapterAuthenticationStoredData> findByTime(Instant start, Instant end);

    @CountQuery("{ 'dateTime': { $gte: ?0 }, $and: [ { 'dateTime': { $lt: ?1 } } ] }")
    long countByTime(Instant start, Instant end);

    AdapterAuthenticationStoredData findByEventId(String eventId);

    Long countByDataSource(String dataSource);

    AdapterAuthenticationStoredData findTopByDateTimeBetween(Instant from, Instant to, Sort sort);

}
