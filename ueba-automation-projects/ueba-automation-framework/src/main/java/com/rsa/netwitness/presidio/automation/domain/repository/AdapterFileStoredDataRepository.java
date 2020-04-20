package com.rsa.netwitness.presidio.automation.domain.repository;

import com.rsa.netwitness.presidio.automation.domain.file.AdapterFileStoredData;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

import static com.rsa.netwitness.presidio.automation.utils.common.LambdaUtils.getOrNull;

public interface AdapterFileStoredDataRepository extends AdapterAbstractStoredDataRepository<AdapterFileStoredData, String> {
    @Override
    default String getName() {
        return "File";
    }

    @Override
    @CountQuery("{ 'dateTime': { $gte: ?0 }, $and: [ { 'dateTime': { $lt: ?1 } } ] }")
    long countByTime(Instant start, Instant end);

    @Override
    default Instant maxDateTimeBetween(Instant start, Instant end) {
        Sort sort = new Sort(Sort.Direction.DESC, "dateTime");
        return getOrNull(findTopByDateTimeBetween(start, end, sort), AdapterFileStoredData::getDateTime);
    }

    @Query("{ 'dateTime': { $gte: ?0 }, $and: [ { 'dateTime': { $lt: ?1 } } ] }")
    List<AdapterFileStoredData> findByTime(Instant start, Instant end);

    AdapterFileStoredData findByEventId(String eventId);

    Long countByDataSource(String dataSource);

    AdapterFileStoredData findTopByDateTimeBetween(Instant from, Instant to, Sort sort);
}
