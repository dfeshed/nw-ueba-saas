package presidio.input.core.services.transformation.managers;

import presidio.input.core.services.transformation.Transformer;
import presidio.sdk.api.domain.AbstractPresidioDocument;

import java.util.ArrayList;
import java.util.List;

public class ActiveDirectoryTransformationManager implements TransformationManager {

    private List<Transformer> transformers;

    @Override
    public List<Transformer> getTransformers() {
        if (transformers == null) {
            transformers = new ArrayList<>();
        }
        return transformers;
    }

    @Override
    public <U extends AbstractPresidioDocument> U getTransformedDocument(AbstractPresidioDocument rawEvent) {
        return (U) rawEvent;
    }
}
