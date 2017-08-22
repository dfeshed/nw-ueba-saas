package presidio.input.sdk.impl.repositories;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.sdk.api.domain.AbstractPresidioDocument;

import java.time.Instant;
import java.util.List;

public interface DataSourceRepository {
    List<? extends AbstractAuditableDocument> getDataSourceDataBetweenDates(String collectionName, Instant startTime, Instant endTime);

    int cleanDataSourceDataBetweenDates(String collectionName, Instant startTime, Instant endTime);

    void insertDataSource(String collectionName, List<? extends AbstractAuditableDocument> documents);

    void cleanCollection(String collectionName);

    <U extends AbstractPresidioDocument> List<U> readRecords(String collectionName, Instant startDate, Instant endDate, int numOfItemsToSkip, int pageSize);

    long count(String collectionName, Instant startDate, Instant endDate);
}
