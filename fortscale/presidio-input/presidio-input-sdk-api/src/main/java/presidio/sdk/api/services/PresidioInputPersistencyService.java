package presidio.sdk.api.services;


import fortscale.domain.core.AbstractAuditableDocument;

import java.util.List;

public interface PresidioInputPersistencyService {
    boolean store(List<AbstractAuditableDocument> event);
}
