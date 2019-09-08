package presidio.input.core.services.transformation.managers;

import presidio.sdk.api.domain.AbstractInputDocument;

public class IocTransformerManager implements TransformationManager {

    @Override
    public <U extends AbstractInputDocument> U getTransformedDocument(AbstractInputDocument rawEvent) {
        return (U) rawEvent;
    }
}
