package presidio.input.core.services.transformation.transformer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fortscale.utils.transform.GenericTransformer;
import presidio.sdk.api.domain.AbstractInputDocument;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public interface InputDocumentTransformer extends GenericTransformer<AbstractInputDocument> {
    AbstractInputDocument transform(AbstractInputDocument document);
}
