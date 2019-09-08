package presidio.input.core.services.transformation.managers;

import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.TlsRawEvent;
import presidio.sdk.api.domain.transformedevents.TlsTransformedEvent;

public class TlsTransformerManager implements TransformationManager {

    @Override
    @SuppressWarnings("unchecked")
    public <U extends AbstractInputDocument> U getTransformedDocument(AbstractInputDocument rawEvent) {
        return (U) new TlsTransformedEvent((TlsRawEvent) rawEvent);
    }
}
