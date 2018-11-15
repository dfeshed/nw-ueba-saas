package presidio.input.core.services.converters.output;

import presidio.output.domain.records.events.ProcessEnrichedEvent;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.transformedevents.ProcessTransformedEvent;

public class ProcessInputToOutputConverter implements InputOutputConverter {

    @Override
    public EnrichedEvent convert(AbstractInputDocument document) {
        ProcessTransformedEvent transformedEvent = (ProcessTransformedEvent) document;
        ProcessEnrichedEvent outputEvent = new ProcessEnrichedEvent();
        outputEvent.setEventId(transformedEvent.getEventId());
        outputEvent.setDataSource(transformedEvent.getDataSource());
        outputEvent.setUserId(transformedEvent.getUserId());
        outputEvent.setEventDate(transformedEvent.getDateTime());
        outputEvent.setUserName(transformedEvent.getUserName());
        outputEvent.setUserDisplayName(transformedEvent.getUserDisplayName());
        outputEvent.setOperationType(transformedEvent.getOperationType());
        outputEvent.setMachineId(transformedEvent.getMachineId());
        outputEvent.setMachineName(transformedEvent.getMachineName());
        outputEvent.setSrcProcessDirectory(transformedEvent.getSrcProcessDirectory());
        outputEvent.setSrcProcessFileName(transformedEvent.getSrcProcessFileName());
        outputEvent.setSrcProcessFilePath(transformedEvent.getSrcProcessFilePath());
        outputEvent.setSrcProcessDirectoryGroups(transformedEvent.getSrcProcessDirectoryGroups());
        outputEvent.setSrcProcessCategories(transformedEvent.getSrcProcessCategories());
        outputEvent.setSrcProcessCertificateIssuer(transformedEvent.getSrcProcessCertificateIssuer());
        outputEvent.setDstProcessDirectory(transformedEvent.getDstProcessDirectory());
        outputEvent.setDstProcessFileName(transformedEvent.getDstProcessFileName());
        outputEvent.setDstProcessFilePath(transformedEvent.getDstProcessFilePath());
        outputEvent.setDstProcessDirectoryGroups(transformedEvent.getDstProcessDirectoryGroups());
        outputEvent.setDstProcessCategories(transformedEvent.getDstProcessCategories());
        outputEvent.setDstProcessCertificateIssuer(transformedEvent.getDstProcessCertificateIssuer());
        outputEvent.setAdditionalInfo(transformedEvent.getAdditionalInfo());
        return outputEvent;
    }
}
