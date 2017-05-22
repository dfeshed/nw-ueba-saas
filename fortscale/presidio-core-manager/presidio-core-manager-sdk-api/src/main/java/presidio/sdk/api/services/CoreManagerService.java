package presidio.sdk.api.services;


import presidio.sdk.api.domain.AbstractRecordDocument;

import java.util.List;

public interface CoreManagerService {
    boolean store(List<AbstractRecordDocument> events);

}
