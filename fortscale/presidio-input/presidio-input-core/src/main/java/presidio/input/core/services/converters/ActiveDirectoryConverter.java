package presidio.input.core.services.converters;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.ade.domain.record.enriched.EnrichedActiveDirectoryRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import fortscale.domain.core.ActiveDirectoryRawEvent;

public class ActiveDirectoryConverter implements InputAdeConverter {
    @Override
    public EnrichedRecord convert(AbstractAuditableDocument document) {
        ActiveDirectoryRawEvent activeDirectoryRawEvent = (ActiveDirectoryRawEvent) document;
        EnrichedActiveDirectoryRecord adeRecord = new EnrichedActiveDirectoryRecord(activeDirectoryRawEvent.getDateTime());
        adeRecord.setEventId(activeDirectoryRawEvent.getEventId());
        adeRecord.setResult(activeDirectoryRawEvent.getResult().toString());
        adeRecord.setOperationType(activeDirectoryRawEvent.getOperationType().toString());
        adeRecord.setNormalizedUsername(activeDirectoryRawEvent.getUserId());
        adeRecord.setObjectName(activeDirectoryRawEvent.getObjectId());
//        adeRecord.setSecuritySensitiveOperation(activeDirectoryRawEvent.getIsSecuritySensitiveOperation());
        adeRecord.setUserAdministrator(activeDirectoryRawEvent.getIsUserAdministrator());
        return adeRecord;
    }
}
