package presidio.input.core.services.transformation.managers;

import presidio.input.core.services.transformation.Transformer;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.util.List;

public interface TransformationManager {
    List<Transformer> getTransformers();

    <U extends AbstractInputDocument> U getTransformedDocument(AbstractInputDocument rawEvent);
}
