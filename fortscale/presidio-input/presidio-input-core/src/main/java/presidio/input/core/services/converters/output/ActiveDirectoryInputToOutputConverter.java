package presidio.input.core.services.converters.output;

import presidio.output.domain.records.events.ActiveDirectoryEnrichedEvent;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.ActiveDirectoryRawEvent;

public class ActiveDirectoryInputToOutputConverter implements InputOutputConverter {
    @Override
    public EnrichedEvent convert(AbstractInputDocument document) {
        ActiveDirectoryRawEvent transformedEvent = (ActiveDirectoryRawEvent) document;
        ActiveDirectoryEnrichedEvent outputEvent = new ActiveDirectoryEnrichedEvent();
        outputEvent.setEventId(transformedEvent.getEventId());
        outputEvent.setEventDate(transformedEvent.getDateTime());
        outputEvent.setDataSource(transformedEvent.getDataSource());
        outputEvent.setUserId(transformedEvent.getUserId());
        switch (transformedEvent.getOperationType()) {
            case "USER_PASSWORD_CHANGED":
                outputEvent.setOperationType("USER_PASSWORD_RESET");
                break;
            case "USER_ACCOUNT_ENABLED":
                outputEvent.setOperationType("STRONG_AUTHENTICATION_METHOD_CHANGED");
                break;
            case "USER_ACCOUNT_DISABLED":
                outputEvent.setOperationType("STRONG_AUTHENTICATION_PHONE_APP_DETAIL_CHANGED");
                break;
            case "USER_ACCOUNT_UNLOCKED":
                outputEvent.setOperationType("STRONG_AUTHENTICATION_PHONE_USER_DETAIL_CHANGED");
                break;
            case "USER_ACCOUNT_TYPE_CHANGED":
                outputEvent.setOperationType("STRONG_AUTHENTICATION_REQUIREMENT_CHANGED");
                break;
            default:
                outputEvent.setOperationType(transformedEvent.getOperationType());
                break;
        }
        outputEvent.setOperationTypeCategories(transformedEvent.getOperationTypeCategory());
        outputEvent.setResult(transformedEvent.getResult());
        outputEvent.setUserName(transformedEvent.getUserName());
        outputEvent.setUserDisplayName(transformedEvent.getUserDisplayName());
        outputEvent.setAdditionalInfo(transformedEvent.getAdditionalInfo());
        outputEvent.setObjectId(transformedEvent.getObjectId());
        outputEvent.setIsUserAdmin(transformedEvent.isUserAdmin());
        outputEvent.setResultCode(transformedEvent.getResultCode());
        return outputEvent;
    }
}
