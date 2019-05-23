package presidio.input.core.services.transformation.managers;

import presidio.input.core.services.transformation.transformer.Transformer;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.util.ArrayList;
import java.util.List;

public class TlsTransformerManager implements TransformationManager {

    private List<Transformer> transformers = new ArrayList<>();

    @Override
    public List<Transformer> getTransformers() {
        return transformers;
    }

    @Override
    public <U extends AbstractInputDocument> U getTransformedDocument(AbstractInputDocument rawEvent) {
        return (U) rawEvent;
    }
}
