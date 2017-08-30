package presidio.input.core.services.transformation;

import presidio.sdk.api.domain.AbstractInputDocument;

import java.util.List;

public interface Transformer {
    List<AbstractInputDocument> transform(List<AbstractInputDocument> documents);
}
