package com.rsa.netwitness.presidio.automation.domain.repository;

import com.rsa.netwitness.presidio.automation.domain.process.AdapterProcessStoredData;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

import static com.rsa.netwitness.presidio.automation.utils.common.LambdaUtils.getOrNull;

public interface AdapterProcessStoredDataRepository extends AdapterAbstractStoredDataRepository<AdapterProcessStoredData, String> {
    @Override
    default String getName() {
        return "Process";
    }

    @Override
    @CountQuery("{ 'dateTime': { $gte: ?0 }, $and: [ { 'dateTime': { $lt: ?1 } } ] }")
    long countByTime(Instant start, Instant end);

    @Override
    default Instant maxDateTimeBetween(Instant start, Instant end) {
        Sort sort = new Sort(Sort.Direction.DESC, "dateTime");
        return getOrNull(findTopByDateTimeBetween(start, end, sort), AdapterProcessStoredData::getDateTime);
    }

    @Query("{ 'dateTime': { $gte: ?0 }, $and: [ { 'dateTime': { $lt: ?1 } } ] }")
    List<AdapterProcessStoredData> findByTime(Instant start, Instant end);

    AdapterProcessStoredData findTopByDateTimeBetween(Instant from, Instant to, Sort sort);

    AdapterProcessStoredData findByEventId(String eventId);

    Long countByDataSource(String dataSource);

    AdapterProcessStoredData findFirstByOrderByDateTimeDesc();
}
