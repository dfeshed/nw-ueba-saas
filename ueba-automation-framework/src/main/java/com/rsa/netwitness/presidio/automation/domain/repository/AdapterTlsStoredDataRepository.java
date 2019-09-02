package com.rsa.netwitness.presidio.automation.domain.repository;

import com.rsa.netwitness.presidio.automation.domain.tls.AdapterTlsStoredData;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

public interface AdapterTlsStoredDataRepository extends AdapterAbstractStoredDataRepository<AdapterTlsStoredData, String> {
    @Override
    default String getName() {
        return "TLS";
    }

    @Override
    @CountQuery("{ 'dateTime': { $gte: ?0 }, $and: [ { 'dateTime': { $lt: ?1 } } ] }")
    long countByTime(Instant start, Instant end);

    @Query("{ 'dateTime': { $gte: ?0 }, $and: [ { 'dateTime': { $lt: ?1 } } ] }")
    List<AdapterTlsStoredData> findByTime(Instant start, Instant end);

    @Override
    default Instant maxDateTimeBetween(Instant start, Instant end) {
        Sort sort = new Sort(Sort.Direction.DESC, "dateTime");
        return findTopByDateTimeBetween(start, end, sort).getDateTime();
    }

    AdapterTlsStoredData findByEventId(String eventId);

    Long countByDataSource(String dataSource);

    AdapterTlsStoredData findTopByDateTimeBetween(Instant from, Instant to, Sort sort);
}
