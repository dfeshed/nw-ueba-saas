package presidio.input.core.services.converters;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.ade.domain.record.enriched.activedirectory.EnrichedActiveDirectoryRecord;
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
        adeRecord.setResult(activeDirectoryRawEvent.getResult());
        adeRecord.setOperationType(activeDirectoryRawEvent.getOperationType().toString());
        adeRecord.setUserId(activeDirectoryRawEvent.getNormalizedUsername());
        adeRecord.setObjectId(activeDirectoryRawEvent.getObjectName());
        adeRecord.setUserAdmin(activeDirectoryRawEvent.getIsUserAdministrator());
        return adeRecord;
    }
}
