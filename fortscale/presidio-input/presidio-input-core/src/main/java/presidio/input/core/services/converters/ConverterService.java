package presidio.input.core.services.converters;

import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractAuditableDocument;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.util.List;

public interface ConverterService {
    List<EnrichedEvent> convertToOutput(List<? extends AbstractInputDocument> documents, Schema schema);

    List<EnrichedRecord> convertToAde(List<? extends AbstractAuditableDocument> documents, Schema schema);
}
