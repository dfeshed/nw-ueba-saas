package presidio.input.core.services.converters;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.ade.domain.record.enriched.EnrichedActiveDirectoryRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.sdk.api.domain.ActiveDirectoryRawEvent;

/**
 * Created by alexp on 11-Jul-17.
 */
public class ActiveDirectoryConverter implements InputAdeConverter {
    @Override
    public EnrichedRecord convert(AbstractAuditableDocument document) {
        ActiveDirectoryRawEvent activeDirectoryRawEvent = (ActiveDirectoryRawEvent) document;
        EnrichedActiveDirectoryRecord adeRecord = new EnrichedActiveDirectoryRecord(activeDirectoryRawEvent.getDateTime());
        adeRecord.setEventId(activeDirectoryRawEvent.getEventId());
        adeRecord.setResult(activeDirectoryRawEvent.getResult().toString());
        adeRecord.setOperationType(activeDirectoryRawEvent.getOperationType().toString());
        adeRecord.setNormalizedUsername(activeDirectoryRawEvent.getNormalizesUsername());
        adeRecord.setObjectName(activeDirectoryRawEvent.getObjectName());
        adeRecord.setSecuritySensitiveOperation(activeDirectoryRawEvent.isSecuritySensitiveOperation());
        adeRecord.setUserAdministrator(activeDirectoryRawEvent.isUserAdministrator());
        //???? adeRecord.setStartInstant();
        return adeRecord;
    }
}
