package presidio.sdk.api.services;


import fortscale.common.general.PresidioSchemas;
import fortscale.domain.core.AbstractAuditableDocument;

import java.time.Instant;
import java.util.List;

public interface PresidioInputPersistencyService {
    boolean store(PresidioSchemas presidioSchemas, List<? extends AbstractAuditableDocument> records);

    List<? extends AbstractAuditableDocument> find(PresidioSchemas presidioSchemas, Instant startTime, Instant endTime) throws Exception; //todo: we can discuss the name. for now using spring's terminology

    int clean(PresidioSchemas presidioSchemas, Instant startTime, Instant endTime) throws Exception;

    void cleanAll(PresidioSchemas presidioSchemas) throws Exception;
}
