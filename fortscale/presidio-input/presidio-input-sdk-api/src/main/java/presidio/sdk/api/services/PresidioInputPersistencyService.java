package presidio.sdk.api.services;


import fortscale.common.general.DataSource;
import fortscale.domain.core.AbstractAuditableDocument;

import java.util.List;

public interface PresidioInputPersistencyService {
    boolean store(DataSource dataSource, List<AbstractAuditableDocument> records);

    List<? extends AbstractAuditableDocument> find(DataSource dataSource, long startTime, long endTime) throws Exception; //todo: we can discuss the name. for now using spring's terminology

    int clean(DataSource dataSource, long startTime, long endTime) throws Exception;

    void cleanAll(DataSource dataSource) throws Exception;
}
