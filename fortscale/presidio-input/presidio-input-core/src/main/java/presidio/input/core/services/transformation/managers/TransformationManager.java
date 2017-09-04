package presidio.input.core.services.transformation.managers;

import presidio.input.core.services.transformation.Transformer;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.util.List;
import java.util.Map;

public interface TransformationManager {
    List<Transformer> getTransformers();

    <U extends AbstractInputDocument> U getTransformedDocument(AbstractInputDocument rawEvent);
}
