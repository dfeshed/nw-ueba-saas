package presidio.input.core.services.transformation.transformer;

import presidio.sdk.api.domain.AbstractInputDocument;

import java.util.List;

public interface Transformer {
    List<AbstractInputDocument> transform(List<AbstractInputDocument> documents);
}
