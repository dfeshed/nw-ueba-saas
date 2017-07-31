package presidio.input.core.services.converters;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.ade.domain.record.enriched.EnrichedFileRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.sdk.api.domain.FileRawEvent;

public class FileConverter implements InputAdeConverter {

    @Override
    public EnrichedRecord convert(AbstractAuditableDocument document) {
        FileRawEvent fileRawEvent = (FileRawEvent) document;
        EnrichedFileRecord adeRecord = new EnrichedFileRecord(fileRawEvent.getDateTime());
        adeRecord.setAbsoluteSrcFilePath(fileRawEvent.getSrcFolderPath());
        adeRecord.setAbsoluteSrcFolderFilePath(fileRawEvent.getSrcFilePath());
        adeRecord.setAbsoluteDstFilePath(fileRawEvent.getDstFilePath());
        adeRecord.setAbsoluteDstFolderFilePath(fileRawEvent.getDstFolderPath());
        adeRecord.setDstDriveShared(fileRawEvent.getIsDstDriveShared());
        adeRecord.setSrcDriveShared(fileRawEvent.getIsSrcDriveShared());
        adeRecord.setFileSize(fileRawEvent.getFileSize());
        adeRecord.setNormalizedUsername(fileRawEvent.getNormalizedUsername());
        adeRecord.setOperationType(fileRawEvent.getOperationType().toString());
        adeRecord.setResult(fileRawEvent.getResult().toString());
        adeRecord.setEventId(fileRawEvent.getId());
        return adeRecord;
    }
}
