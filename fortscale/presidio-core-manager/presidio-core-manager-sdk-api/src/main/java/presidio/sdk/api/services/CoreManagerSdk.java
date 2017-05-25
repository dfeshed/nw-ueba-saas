package presidio.sdk.api.services;


import fortscale.domain.core.AbstractAuditableDocument;

import java.util.List;

public interface CoreManagerSdk {
    boolean store(List<AbstractAuditableDocument> events);

}
