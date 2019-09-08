package presidio.input.core.services.transformation.managers;


import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.RegistryRawEvent;
import presidio.sdk.api.domain.transformedevents.RegistryTransformedEvent;

public class RegistryTransformerManager implements TransformationManager {

    @Override
    public <U extends AbstractInputDocument> U getTransformedDocument(AbstractInputDocument rawEvent) {
        return (U) new RegistryTransformedEvent((RegistryRawEvent) rawEvent);
    }
}