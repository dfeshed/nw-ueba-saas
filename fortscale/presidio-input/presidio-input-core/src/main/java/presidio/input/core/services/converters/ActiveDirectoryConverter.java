package presidio.input.core.services.converters;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.ade.domain.record.enriched.activedirectory.EnrichedActiveDirectoryRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.sdk.api.domain.ActiveDirectoryRawEvent;

public class ActiveDirectoryConverter implements InputAdeConverter {
    @Override
    public EnrichedRecord convert(AbstractAuditableDocument document) {
        ActiveDirectoryRawEvent activeDirectoryRawEvent = (ActiveDirectoryRawEvent) document;
        EnrichedActiveDirectoryRecord adeRecord = new EnrichedActiveDirectoryRecord(activeDirectoryRawEvent.getDateTime());
        return adeRecord;
    }
}
