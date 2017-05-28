package presidio.sdk.api.services;


import fortscale.common.general.Datasource;
import fortscale.domain.core.AbstractAuditableDocument;

import java.util.List;

public interface CoreManagerSdk {
    boolean store(Datasource dataSource, List<AbstractAuditableDocument> records);

}
