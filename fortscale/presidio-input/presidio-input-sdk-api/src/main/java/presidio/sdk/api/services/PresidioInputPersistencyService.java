package presidio.sdk.api.services;


import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractAuditableDocument;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.validation.ValidationResults;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface PresidioInputPersistencyService {
    ValidationResults store(Schema schema, List<? extends AbstractAuditableDocument> records);

    List<? extends AbstractAuditableDocument> find(Schema schema, Instant startTime, Instant endTime) throws Exception; //todo: we can discuss the name. for now using spring's terminology

    int clean(Schema schema, Instant startTime, Instant endTime) throws Exception;

    int cleanUntil(Schema schema, Instant endTime) throws Exception;

    void cleanAll(Schema schema) throws Exception;

    <U extends AbstractInputDocument> List<U> readRecords(Schema schema, Instant startDate, Instant endDate, int numOfItemsToSkip, int pageSize, Map<String, Object> filter, List<String> projectionFields);

    <U extends AbstractInputDocument> List<U> readRecords(Schema schema, Instant startDate, Instant endDate, int numOfItemsToSkip, int pageSize);

    long count(Schema schema, Instant startDate, Instant endDate, Map<String, Object> filter, List<String> projectionFields);

    long count(Schema schema, Instant startDate, Instant endDate);
}
