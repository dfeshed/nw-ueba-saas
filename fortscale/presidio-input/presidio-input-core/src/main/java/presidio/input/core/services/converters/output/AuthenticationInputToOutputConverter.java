package presidio.input.core.services.converters.output;

import org.apache.commons.lang.StringUtils;
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
        outputEvent.setAdditionalInfo(transformedEvent.getAdditionalInfo());
        outputEvent.setSrcMachineId(StringUtils.isEmpty(transformedEvent.getSrcMachineId()) ? transformedEvent.getSrcMachineName() : transformedEvent.getSrcMachineId());
        outputEvent.setSrcMachineNameRegexCluster(StringUtils.isEmpty(transformedEvent.getSrcMachineCluster()) ? transformedEvent.getSrcMachineName() : transformedEvent.getSrcMachineCluster());
        outputEvent.setDstMachineId(StringUtils.isEmpty(transformedEvent.getDstMachineId()) ? transformedEvent.getDstMachineName() : transformedEvent.getDstMachineId());
        outputEvent.setDstMachineNameRegexCluster(StringUtils.isEmpty(transformedEvent.getDstMachineCluster()) ? transformedEvent.getDstMachineName() : transformedEvent.getDstMachineCluster());
        outputEvent.setDstMachineDomain(transformedEvent.getDstMachineDomain());
        outputEvent.setResultCode(transformedEvent.getResultCode());
        return outputEvent;
    }
}
