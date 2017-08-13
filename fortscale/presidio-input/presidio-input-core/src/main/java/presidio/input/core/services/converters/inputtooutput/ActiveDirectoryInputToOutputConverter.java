package presidio.input.core.services.converters.inputtooutput;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.output.domain.records.events.ActiveDirectoryEnrichedEvent;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.sdk.api.domain.rawevents.ActiveDirectoryRawEvent;

public class ActiveDirectoryInputToOutputConverter implements InputOutputConverter {
    @Override
    public EnrichedEvent convert(AbstractAuditableDocument document) {
        ActiveDirectoryRawEvent transformedEvent = (ActiveDirectoryRawEvent) document;
        ActiveDirectoryEnrichedEvent outputEvent = new ActiveDirectoryEnrichedEvent();
        outputEvent.setEventId(transformedEvent.getEventId());
        outputEvent.setEventDate(transformedEvent.getDateTime());
        outputEvent.setDataSource(transformedEvent.getDataSource());
        outputEvent.setUserId(transformedEvent.getUserId());
        outputEvent.setOperationType(transformedEvent.getOperationType());
        outputEvent.setOperationTypeCategories(transformedEvent.getOperationTypeCategory());
        outputEvent.setResult(transformedEvent.getResult());
        outputEvent.setUserName(transformedEvent.getUserName());
        outputEvent.setUserDisplayName(transformedEvent.getUserDisplayName());
        outputEvent.setAdditionalnfo(transformedEvent.getAdditionalInfo());
        outputEvent.setObjectId(transformedEvent.getObjectId());
        outputEvent.setUserAdmin(transformedEvent.isUserAdmin());
        return outputEvent;
    }
}
