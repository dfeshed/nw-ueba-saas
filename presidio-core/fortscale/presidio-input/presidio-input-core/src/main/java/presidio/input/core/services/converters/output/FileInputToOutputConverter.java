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
        outputEvent.setOperationTypeCategories(transformedEvent.getOperationTypeCategories());
        outputEvent.setResult(transformedEvent.getResult());
        outputEvent.setUserName(transformedEvent.getUserName());
        outputEvent.setUserDisplayName(transformedEvent.getUserDisplayName());
        outputEvent.setAdditionalInfo(transformedEvent.getAdditionalInfo());
        outputEvent.setAbsoluteSrcFilePath(transformedEvent.getSrcFilePath());
        outputEvent.setAbsoluteSrcFolderFilePath(transformedEvent.getSrcFolderPath());
        outputEvent.setIsSrcDriveShared(transformedEvent.getIsSrcDriveShared());
        outputEvent.setAbsoluteDstFilePath(transformedEvent.getDstFilePath());
        outputEvent.setAbsoluteDstFolderFilePath(transformedEvent.getDstFolderPath());
        outputEvent.setIsDstDriveShared(transformedEvent.getIsDstDriveShared());
        outputEvent.setFileSize(transformedEvent.getFileSize());
        outputEvent.setResultCode(transformedEvent.getResultCode());
        return outputEvent;
    }
}
