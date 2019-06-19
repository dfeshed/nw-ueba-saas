package presidio.input.sdk.impl.repositories;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface DataSourceRepository {
    List<? extends AbstractAuditableDocument> getDataSourceDataBetweenDates(String collectionName, Instant startTime, Instant endTime);

    int cleanDataSourceDataBetweenDates(String collectionName, Instant startTime, Instant endTime);

    int cleanDataSourceDataUntilDate(String collectionName, Instant endTime);

    void insertDataSource(String collectionName, List<? extends AbstractAuditableDocument> documents);

    void cleanCollection(String collectionName);

    <U extends AbstractInputDocument> List<U> readRecords(String collectionName, Instant startDate, Instant endDate, int numOfItemsToSkip, int pageSize, Map<String, Object> filter);

    long count(String collectionName, Instant startDate, Instant endDate, Map<String, Object> filter);
}
