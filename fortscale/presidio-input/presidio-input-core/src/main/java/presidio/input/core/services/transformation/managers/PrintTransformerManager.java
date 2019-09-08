package presidio.input.core.services.transformation.managers;

import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.PrintRawEvent;
import presidio.sdk.api.domain.transformedevents.PrintTransformedEvent;

public class PrintTransformerManager implements TransformationManager {

    @Override
    public <U extends AbstractInputDocument> U getTransformedDocument(AbstractInputDocument rawEvent) {
        return (U) new PrintTransformedEvent((PrintRawEvent) rawEvent);
    }
}
