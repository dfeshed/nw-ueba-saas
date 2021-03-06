package presidio.input.core.services.converters.output;

import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.events.RegistryEnrichedEvent;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.transformedevents.RegistryTransformedEvent;

public class RegistryInputToOutputConverter implements InputOutputConverter {
    @Override
    public EnrichedEvent convert(AbstractInputDocument document) {
        RegistryTransformedEvent transformedEvent = (RegistryTransformedEvent) document;
        RegistryEnrichedEvent outputEvent = new RegistryEnrichedEvent();
        outputEvent.setEventId(transformedEvent.getEventId());
        outputEvent.setEventDate(transformedEvent.getDateTime());
        outputEvent.setDataSource(transformedEvent.getDataSource());
        outputEvent.setUserId(transformedEvent.getUserId());
        outputEvent.setUserName(transformedEvent.getUserName());
        outputEvent.setUserDisplayName(transformedEvent.getUserDisplayName());
        outputEvent.setOperationType(transformedEvent.getOperationType());
        outputEvent.setMachineId(transformedEvent.getMachineId());
        outputEvent.setMachineName(transformedEvent.getMachineName());
        outputEvent.setMachineOwner(transformedEvent.getMachineOwner());
        outputEvent.setProcessDirectory(transformedEvent.getProcessDirectory());
        outputEvent.setProcessFileName(transformedEvent.getProcessFileName());
        outputEvent.setProcessFilePath(transformedEvent.getProcessFilePath());
        outputEvent.setProcessDirectoryGroups(transformedEvent.getProcessDirectoryGroups());
        outputEvent.setProcessCategories(transformedEvent.getProcessCategories());
        outputEvent.setProcessCertificateIssuer(transformedEvent.getProcessCertificateIssuer());
        outputEvent.setRegistryKeyGroup(transformedEvent.getRegistryKeyGroup());
        outputEvent.setRegistryKey(transformedEvent.getRegistryKey());
        outputEvent.setRegistryValueName(transformedEvent.getRegistryValueName());
        outputEvent.setAdditionalInfo(transformedEvent.getAdditionalInfo());

        return outputEvent;
    }
}
