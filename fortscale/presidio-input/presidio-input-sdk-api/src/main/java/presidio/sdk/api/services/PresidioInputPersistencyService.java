package presidio.sdk.api.services;


import fortscale.common.general.DataSource;
import fortscale.domain.core.AbstractAuditableDocument;

import java.time.Instant;
import java.util.List;

public interface PresidioInputPersistencyService {
    boolean store(DataSource dataSource, List<? extends AbstractAuditableDocument> records);

    List<? extends AbstractAuditableDocument> find(DataSource dataSource, Instant startTime, Instant endTime) throws Exception; //todo: we can discuss the name. for now using spring's terminology

    int clean(DataSource dataSource, Instant startTime, Instant endTime) throws Exception;

    void cleanAll(DataSource dataSource) throws Exception;
}
