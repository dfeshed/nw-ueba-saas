package presidio.sdk.api.services;

import fortscale.common.general.Schema;
import fortscale.common.general.SchemaEntityCount;
import fortscale.domain.core.AbstractAuditableDocument;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.validation.ValidationResults;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface DataService {

    ValidationResults store(List<? extends AbstractAuditableDocument> documents, Schema schema);

    List<? extends AbstractAuditableDocument> find(Instant startDate, Instant endDate, Schema schema);

    int clean(Instant startDate, Instant endDate, Schema schema);

    int cleanUntil(Instant endDate, Schema schema);

    void cleanAll(Schema schema);

    <U extends AbstractInputDocument> List<U> readRecords(Schema schema, Instant startDate, Instant endDate, int numOfItemsToSkip, int pageSize);

    <U extends AbstractInputDocument> List<U> readRecords(Schema schema, Instant startDate, Instant endDate, int numOfItemsToSkip, int pageSize, Map<String, Object> filter, List<String> projectionFields);

    long count(Schema schema, Instant startDate, Instant endDate);

    long count(Schema schema, Instant startDate, Instant endDate, Map<String, Object> filter, List<String> projectionFields);

    Map<String, Instant> aggregateKeysMaxInstant(Instant startDate, Instant endDate, String fieldPath, long skip, long limit, Schema schema, boolean allowDiskUse);

    List<SchemaEntityCount> getMostCommonEntityIds(Instant startInstant, Instant endInstant, String entityType, long limit, Schema schema);
}
