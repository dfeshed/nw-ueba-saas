package presidio.input.core.services.converters.inputtooutput;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.output.domain.records.events.EnrichedEvent;

public class AuthenticationInputToOutputConverter implements InputOutputConverter {
    @Override
    public EnrichedEvent convert(AbstractAuditableDocument document) {
        return null;
    }
}
