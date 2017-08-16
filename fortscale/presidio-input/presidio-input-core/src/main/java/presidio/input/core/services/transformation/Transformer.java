package presidio.input.core.services.transformation;

import presidio.sdk.api.domain.AbstractPresidioDocument;

import java.util.List;

public interface Transformer {
    List<AbstractPresidioDocument> transform(List<AbstractPresidioDocument> documents);
}
