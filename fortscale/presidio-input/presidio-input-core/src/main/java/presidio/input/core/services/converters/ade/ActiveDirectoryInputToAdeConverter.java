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

        switch (activeDirectoryRawEvent.getOperationType()) {
            case "USER_PASSWORD_CHANGED":
                adeRecord.setOperationType("USER_PASSWORD_RESET");
                break;
            case "USER_ACCOUNT_ENABLED":
                adeRecord.setOperationType("STRONG_AUTHENTICATION_METHOD_CHANGED");
                break;
            case "USER_ACCOUNT_DISABLED":
                adeRecord.setOperationType("STRONG_AUTHENTICATION_PHONE_APP_DETAIL_CHANGED");
                break;
            case "USER_ACCOUNT_UNLOCKED":
                adeRecord.setOperationType("STRONG_AUTHENTICATION_PHONE_USER_DETAIL_CHANGED");
                break;
            case "USER_ACCOUNT_TYPE_CHANGED":
                adeRecord.setOperationType("STRONG_AUTHENTICATION_REQUIREMENT_CHANGED");
                break;
            default:
                adeRecord.setOperationType(activeDirectoryRawEvent.getOperationType());
                break;
        }

        adeRecord.setOperationTypeCategories(activeDirectoryRawEvent.getOperationTypeCategory());
        adeRecord.setResult(activeDirectoryRawEvent.getResult());
        adeRecord.setObjectId(activeDirectoryRawEvent.getObjectId());
        adeRecord.setUserAdmin(activeDirectoryRawEvent.isUserAdmin());
        adeRecord.setResultCode(activeDirectoryRawEvent.getResultCode());
        return adeRecord;
    }
}
