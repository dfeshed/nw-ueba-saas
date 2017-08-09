package presidio.input.core.services.converters;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.ade.domain.record.enriched.EnrichedRecord;

/**
 * Created by alexp on 10-Jul-17.
 */
public interface InputAdeConverter {
    EnrichedRecord convert(AbstractAuditableDocument document);
}
