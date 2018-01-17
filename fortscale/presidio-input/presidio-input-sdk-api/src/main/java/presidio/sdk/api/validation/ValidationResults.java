package presidio.sdk.api.validation;

import fortscale.domain.core.AbstractAuditableDocument;

import java.util.List;

/**
 * Created by efratn on 16/01/2018.
 */
public class ValidationResults {
    public final List<? extends AbstractAuditableDocument> validDocuments;
    public final List<? extends InvalidInputDocument> invalidDocuments;

    public ValidationResults(List<? extends AbstractAuditableDocument> validDocuments, List<? extends InvalidInputDocument> invalidDocuments) {
        this.validDocuments = validDocuments;
        this.invalidDocuments = invalidDocuments;
    }
}
