package presidio.input.core.services.converters.output;

import org.apache.commons.lang.StringUtils;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.events.PrintEnrichedEvent;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.transformedevents.PrintTransformedEvent;

public class PrintInputToOutputConverter implements InputOutputConverter {
    @Override
    public EnrichedEvent convert(AbstractInputDocument document) {
        PrintTransformedEvent transformedEvent = (PrintTransformedEvent) document;
        PrintEnrichedEvent outputEvent = new PrintEnrichedEvent();
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
        outputEvent.setFileSize(transformedEvent.getFileSize());
        outputEvent.setNumOfPages(transformedEvent.getNumOfPages());
        outputEvent.setIsSrcDriveShared(transformedEvent.getSrcDriveShared());
        outputEvent.setSrcFileExtension(transformedEvent.getSrcFileExtension());
        outputEvent.setSrcFilePath(transformedEvent.getSrcFilePath());
        outputEvent.setSrcFolderPath(transformedEvent.getSrcFolderPath());
        outputEvent.setSrcMachineId(StringUtils.isEmpty(transformedEvent.getSrcMachineId()) ? transformedEvent.getSrcMachineName() : transformedEvent.getSrcMachineId());
        outputEvent.setSrcMachineCluster(StringUtils.isEmpty(transformedEvent.getSrcMachineCluster()) ? transformedEvent.getSrcMachineName() : transformedEvent.getSrcMachineCluster());
        outputEvent.setPrinterId(StringUtils.isEmpty(transformedEvent.getPrinterId()) ? transformedEvent.getPrinterName() : transformedEvent.getPrinterId());
        outputEvent.setPrinterCluster(StringUtils.isEmpty(transformedEvent.getPrinterCluster()) ? transformedEvent.getPrinterName() : transformedEvent.getPrinterCluster());

        return outputEvent;
    }
}
