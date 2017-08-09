package presidio.sdk.api.services;


import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractAuditableDocument;

import java.time.Instant;
import java.util.List;

public interface PresidioInputPersistencyService {
    boolean store(Schema schema, List<? extends AbstractAuditableDocument> records);

    List<? extends AbstractAuditableDocument> find(Schema schema, Instant startTime, Instant endTime) throws Exception; //todo: we can discuss the name. for now using spring's terminology

    int clean(Schema schema, Instant startTime, Instant endTime) throws Exception;

    void cleanAll(Schema schema) throws Exception;
}
