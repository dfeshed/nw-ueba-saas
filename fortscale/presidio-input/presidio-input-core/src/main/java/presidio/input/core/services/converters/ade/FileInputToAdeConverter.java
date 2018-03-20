package presidio.input.core.services.converters.ade;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.file.EnrichedFileRecord;
import presidio.sdk.api.domain.transformedevents.FileTransformedEvent;

public class FileInputToAdeConverter implements InputAdeConverter {

    @Override
    public EnrichedRecord convert(AbstractAuditableDocument document) {
        FileTransformedEvent fileRawEvent = (FileTransformedEvent) document;
        EnrichedFileRecord adeRecord = new EnrichedFileRecord(fileRawEvent.getDateTime());
        adeRecord.setEventId(fileRawEvent.getEventId());
        adeRecord.setDataSource(fileRawEvent.getDataSource());
        adeRecord.setUserId(fileRawEvent.getUserId());
        adeRecord.setOperationType(fileRawEvent.getOperationType());
        adeRecord.setOperationTypeCategories(fileRawEvent.getOperationTypeCategories());
        adeRecord.setResult(fileRawEvent.getResult());
        adeRecord.setAbsoluteSrcFilePath(fileRawEvent.getSrcFilePath());
        adeRecord.setAbsoluteSrcFolderFilePath(fileRawEvent.getSrcFolderPath());
        adeRecord.setSrcDriveShared(fileRawEvent.getIsSrcDriveShared());
        adeRecord.setAbsoluteDstFilePath(fileRawEvent.getDstFilePath());
        adeRecord.setAbsoluteDstFolderFilePath(fileRawEvent.getDstFolderPath());
        adeRecord.setDstDriveShared(fileRawEvent.getIsDstDriveShared());
        adeRecord.setFileSize(fileRawEvent.getFileSize());
        adeRecord.setResultCode(fileRawEvent.getResultCode());
        return adeRecord;
    }
}
