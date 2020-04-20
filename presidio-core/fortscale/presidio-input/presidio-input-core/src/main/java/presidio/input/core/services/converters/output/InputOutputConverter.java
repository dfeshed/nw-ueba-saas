package presidio.input.core.services.converters.output;

import presidio.output.domain.records.events.EnrichedEvent;
import presidio.sdk.api.domain.AbstractInputDocument;

public interface InputOutputConverter {
    EnrichedEvent convert(AbstractInputDocument document);
}
