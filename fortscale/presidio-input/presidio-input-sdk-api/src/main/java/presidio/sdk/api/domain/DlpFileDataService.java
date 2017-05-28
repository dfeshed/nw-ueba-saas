package presidio.sdk.api.domain;

import fortscale.domain.core.AbstractAuditableDocument;

import java.util.List;


public interface DlpFileDataService {

    boolean store(List<? extends AbstractAuditableDocument> documents);

}
