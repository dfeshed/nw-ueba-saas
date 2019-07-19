package com.rsa.netwitness.presidio.automation.domain.repository;

import com.rsa.netwitness.presidio.automation.domain.activedirectory.AdapterActiveDirectoryStoredData;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

public interface AdapterActiveDirectoryStoredDataRepository extends MongoRepository<AdapterActiveDirectoryStoredData, String> {
    @Query("{ 'dateTime' : { $gte: ?0, $lt: ?1} }")
    List<AdapterActiveDirectoryStoredData> findByUsername(Instant start, Instant end, String username);

    @CountQuery("{ 'dateTime': { $gte: ?0 }, $and: [ { 'dateTime': { $lt: ?1 } } ] }")
    long findByTime(Instant start, Instant end);

    @Query("{ userId :?0 }")
    List<AdapterActiveDirectoryStoredData> findByUserId(String userId, Sort sort);

    @Query("{ userId :?0 }")
    List<AdapterActiveDirectoryStoredData> findByuserId(String userId);
    AdapterActiveDirectoryStoredData findByEventId(String eventId);

    AdapterActiveDirectoryStoredData findTopByDateTimeBetween(Instant from, Instant to, Sort sort);

    Long countByDataSource(String dataSource);

}
