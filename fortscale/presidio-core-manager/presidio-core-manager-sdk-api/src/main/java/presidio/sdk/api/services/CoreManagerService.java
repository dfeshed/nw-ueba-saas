package presidio.sdk.api.services;


import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractAuditableDocument;

import java.util.List;

public interface CoreManagerService {
    boolean store(Schema schema, List<AbstractAuditableDocument> events);

}
