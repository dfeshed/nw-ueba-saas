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
        outputEvent.setOperationType(transformedEvent.getOperationType());
        outputEvent.setOperationTypeCategories(transformedEvent.getOperationTypeCategories());
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
