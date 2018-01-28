package presidio.input.core.services.converters.output;

import presidio.output.domain.records.events.EnrichedEvent;
import presidio.sdk.api.domain.AbstractInputDocument;

public class PrintInputToOutputConverter implements InputOutputConverter {
    @Override
    public EnrichedEvent convert(AbstractInputDocument document) {
        return null;
    }
}
