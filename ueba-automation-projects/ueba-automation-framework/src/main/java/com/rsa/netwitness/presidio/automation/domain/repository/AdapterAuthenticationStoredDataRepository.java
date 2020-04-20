package com.rsa.netwitness.presidio.automation.domain.repository;

import com.rsa.netwitness.presidio.automation.domain.authentication.AdapterAuthenticationStoredData;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

import static com.rsa.netwitness.presidio.automation.utils.common.LambdaUtils.getOrNull;

public interface AdapterAuthenticationStoredDataRepository extends AdapterAbstractStoredDataRepository<AdapterAuthenticationStoredData, String> {
    @Override
    default String getName() {
        return "Authentication";
    }

    @Query("{ 'dateTime': { $gte: ?0 }, $and: [ { 'dateTime': { $lt: ?1 } } ] }")
    List<AdapterAuthenticationStoredData> findByTime(Instant start, Instant end);

    @Override
    @CountQuery("{ 'dateTime': { $gte: ?0 }, $and: [ { 'dateTime': { $lt: ?1 } } ] }")
    long countByTime(Instant start, Instant end);

    @Override
    default Instant maxDateTimeBetween(Instant start, Instant end) {
        Sort sort = new Sort(Sort.Direction.DESC, "dateTime");
        return getOrNull(findTopByDateTimeBetween(start, end, sort), AdapterAuthenticationStoredData::getDateTime);
    }

    AdapterAuthenticationStoredData findByEventId(String eventId);

    Long countByDataSource(String dataSource);

    AdapterAuthenticationStoredData findTopByDateTimeBetween(Instant from, Instant to, Sort sort);

}
