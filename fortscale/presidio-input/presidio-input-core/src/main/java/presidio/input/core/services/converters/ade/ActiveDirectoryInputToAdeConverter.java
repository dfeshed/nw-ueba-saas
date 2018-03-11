package presidio.input.core.services.converters.ade;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.activedirectory.EnrichedActiveDirectoryRecord;
import presidio.sdk.api.domain.rawevents.ActiveDirectoryRawEvent;

public class ActiveDirectoryInputToAdeConverter implements InputAdeConverter {
    @Override
    public EnrichedRecord convert(AbstractAuditableDocument document) {
        ActiveDirectoryRawEvent activeDirectoryRawEvent = (ActiveDirectoryRawEvent) document;
        EnrichedActiveDirectoryRecord adeRecord = new EnrichedActiveDirectoryRecord(activeDirectoryRawEvent.getDateTime());
        adeRecord.setEventId(activeDirectoryRawEvent.getEventId());
        adeRecord.setDataSource(activeDirectoryRawEvent.getDataSource());
        adeRecord.setUserId(activeDirectoryRawEvent.getUserId());
        adeRecord.setOperationType(activeDirectoryRawEvent.getOperationType());
        adeRecord.setOperationTypeCategories(activeDirectoryRawEvent.getOperationTypeCategories());
        adeRecord.setResult(activeDirectoryRawEvent.getResult());
        adeRecord.setObjectId(activeDirectoryRawEvent.getObjectId());
        adeRecord.setUserAdmin(activeDirectoryRawEvent.isUserAdmin());
        adeRecord.setResultCode(activeDirectoryRawEvent.getResultCode());
        return adeRecord;
    }
}
