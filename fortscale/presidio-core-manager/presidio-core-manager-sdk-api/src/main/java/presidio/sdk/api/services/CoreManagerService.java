package presidio.sdk.api.services;


import fortscale.common.general.PresidioSchemas;
import fortscale.domain.core.AbstractAuditableDocument;

import java.util.List;

public interface CoreManagerService {
    boolean store(PresidioSchemas presidioSchemas, List<AbstractAuditableDocument> events);

}
