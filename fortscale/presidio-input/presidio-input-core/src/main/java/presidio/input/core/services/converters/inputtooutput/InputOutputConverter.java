package presidio.input.core.services.converters.inputtooutput;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.output.domain.records.events.EnrichedEvent;

/**
 * Created by alexp on 10-Jul-17.
 */
public interface InputOutputConverter {
    EnrichedEvent convert(AbstractAuditableDocument document);
}
