package presidio.sdk.api.services;


import fortscale.common.general.Datasource;
import fortscale.domain.core.AbstractAuditableDocument;

import java.util.List;

public interface PresidioInputPersistencyService {
    boolean store(Datasource datasource, List<AbstractAuditableDocument> records);

    List<? extends AbstractAuditableDocument> find(Datasource dataSource, long startTime, long endTime); //todo: we can discuss the name. for now using spring's terminology
}
