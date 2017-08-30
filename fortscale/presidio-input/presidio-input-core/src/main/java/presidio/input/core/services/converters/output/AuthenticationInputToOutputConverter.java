package presidio.input.core.services.converters.output;

import presidio.output.domain.records.events.AuthenticationEnrichedEvent;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.transformedevents.AuthenticationTransformedEvent;

public class AuthenticationInputToOutputConverter implements InputOutputConverter {
    @Override
    public EnrichedEvent convert(AbstractInputDocument document) {
        AuthenticationTransformedEvent transformedEvent = (AuthenticationTransformedEvent) document;
        AuthenticationEnrichedEvent outputEvent = new AuthenticationEnrichedEvent();
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
        outputEvent.setSrcMachineId(transformedEvent.getSrcMachineId());
        outputEvent.setSrcMachineNameRegexCluster(transformedEvent.getSrcMachineCluster());
        outputEvent.setDstMachineId(transformedEvent.getDstMachineId());
        outputEvent.setDstMachineNameRegexCluster(transformedEvent.getDstMachineCluster());
        outputEvent.setDstMachineDomain(transformedEvent.getDstMachineDomain());
        return outputEvent;
    }
}
