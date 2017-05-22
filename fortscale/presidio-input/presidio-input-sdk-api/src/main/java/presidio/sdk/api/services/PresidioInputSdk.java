package presidio.sdk.api.services;


import presidio.sdk.api.domain.AbstractRecordDocument;

import java.util.List;

public interface PresidioInputSdk {
    boolean store(List<AbstractRecordDocument> event);
}
