package presidio.input.core.services.converters.inputtoade;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.activedirectory.EnrichedActiveDirectoryRecord;
import presidio.sdk.api.domain.rawevents.ActiveDirectoryRawEvent;

public class ActiveDirectoryConverter implements InputAdeConverter {
    @Override
    public EnrichedRecord convert(AbstractAuditableDocument document) {
        ActiveDirectoryRawEvent activeDirectoryRawEvent = (ActiveDirectoryRawEvent) document;
        EnrichedActiveDirectoryRecord adeRecord = new EnrichedActiveDirectoryRecord(activeDirectoryRawEvent.getDateTime());
        adeRecord.setEventId(activeDirectoryRawEvent.getEventId());
        adeRecord.setDataSource(activeDirectoryRawEvent.getDataSource());
        adeRecord.setUserId(activeDirectoryRawEvent.getUserId());
        adeRecord.setOperationType(activeDirectoryRawEvent.getOperationType());
        adeRecord.setOperationTypeCategories(activeDirectoryRawEvent.getOperationTypeCategory());
        adeRecord.setResult(activeDirectoryRawEvent.getResult());
        return adeRecord;
    }
}
