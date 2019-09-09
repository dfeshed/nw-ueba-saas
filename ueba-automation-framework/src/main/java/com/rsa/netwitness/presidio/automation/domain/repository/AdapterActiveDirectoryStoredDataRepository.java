package com.rsa.netwitness.presidio.automation.domain.repository;

import com.rsa.netwitness.presidio.automation.domain.activedirectory.AdapterActiveDirectoryStoredData;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

import static com.rsa.netwitness.presidio.automation.utils.common.LambdaUtils.getOrNull;

public interface AdapterActiveDirectoryStoredDataRepository extends AdapterAbstractStoredDataRepository<AdapterActiveDirectoryStoredData, String> {
    @Override
    default String getName() {
        return "ActiveDirectory";
    }

    @Query("{ 'dateTime' : { $gte: ?0, $lt: ?1} }")
    List<AdapterActiveDirectoryStoredData> findByUsername(Instant start, Instant end, String username);

    @Override
    @CountQuery("{ 'dateTime': { $gte: ?0 }, $and: [ { 'dateTime': { $lt: ?1 } } ] }")
    long countByTime(Instant start, Instant end);

    @Override
    default Instant maxDateTimeBetween(Instant start, Instant end) {
        Sort sort = new Sort(Sort.Direction.DESC, "dateTime");
        return getOrNull(findTopByDateTimeBetween(start, end, sort), AdapterActiveDirectoryStoredData::getDateTime);
    }

    @Query("{ 'dateTime': { $gte: ?0 }, $and: [ { 'dateTime': { $lt: ?1 } } ] }")
    List<AdapterActiveDirectoryStoredData> findByTime(Instant start, Instant end);

    @Query("{ userId :?0 }")
    List<AdapterActiveDirectoryStoredData> findByUserId(String userId, Sort sort);

    @Query("{ userId :?0 }")
    List<AdapterActiveDirectoryStoredData> findByuserId(String userId);

    AdapterActiveDirectoryStoredData findByEventId(String eventId);

    AdapterActiveDirectoryStoredData findTopByDateTimeBetween(Instant from, Instant to, Sort sort);

    Long countByDataSource(String dataSource);

}
