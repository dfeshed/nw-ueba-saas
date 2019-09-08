package presidio.input.core.services.transformation.managers;

import presidio.input.core.services.transformation.transformer.Transformer;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public interface TransformationManager {
    <U extends AbstractInputDocument> U getTransformedDocument(AbstractInputDocument rawEvent);
}
