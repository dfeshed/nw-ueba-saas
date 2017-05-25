package presidio.sdk.api.services;


import fortscale.domain.core.AbstractAuditableDocument;

import java.util.List;

public interface PresidioInputSdk {
    boolean store(List<AbstractAuditableDocument> events);
}
