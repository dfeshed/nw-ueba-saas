package presidio.input.core.services.converters.output;

import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.events.FileEnrichedEvent;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.transformedevents.FileTransformedEvent;

public class FileInputToOutputConverter implements InputOutputConverter {
    @Override
    public EnrichedEvent convert(AbstractInputDocument document) {
        FileTransformedEvent transformedEvent = (FileTransformedEvent) document;
        FileEnrichedEvent outputEvent = new FileEnrichedEvent();
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
        outputEvent.setAbsoluteSrcFilePath(transformedEvent.getSrcFilePath());
        outputEvent.setAbsoluteSrcFolderFilePath(transformedEvent.getSrcFolderPath());
        outputEvent.setSrcDriveShared(transformedEvent.isSrcDriveShared());
        outputEvent.setAbsoluteDstFilePath(transformedEvent.getDstFilePath());
        outputEvent.setAbsoluteDstFolderFilePath(transformedEvent.getDstFolderPath());
        outputEvent.setDstDriveShared(transformedEvent.isDstDriveShared());
        outputEvent.setFileSize(transformedEvent.getFileSize());
        return outputEvent;
    }
}
