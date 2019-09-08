package presidio.input.core.services.transformation.managers;

import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.ProcessRawEvent;
import presidio.sdk.api.domain.transformedevents.ProcessTransformedEvent;

public class ProcessTransformerManager implements TransformationManager {

    @Override
    public <U extends AbstractInputDocument> U getTransformedDocument(AbstractInputDocument rawEvent) {
        return (U) new ProcessTransformedEvent((ProcessRawEvent) rawEvent);
    }
}
