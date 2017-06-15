package presidio.sdk.api.services;


import fortscale.common.general.DataSource;
import fortscale.domain.core.AbstractAuditableDocument;

import java.util.List;

public interface CoreManagerService {
    boolean store(DataSource dataSource, List<AbstractAuditableDocument> events);

}
