package presidio.ade.test.utils.converters;

import fortscale.domain.core.EventResult;
import org.springframework.core.convert.converter.Converter;
import presidio.ade.domain.record.enriched.dlpfile.EnrichedDlpFileRecord;
import presidio.ade.domain.record.enriched.file.EnrichedFileRecord;
import presidio.data.domain.event.dlpfile.DLPFileEvent;
import presidio.data.domain.event.file.FileEvent;

import java.util.LinkedList;
import java.util.List;

public class FileRaw2EnrichedConverter implements Converter<FileEvent,EnrichedFileRecord> {
    public List<EnrichedFileRecord> convert(List<FileEvent> sources)
    {
        List <EnrichedFileRecord> convertedList = new LinkedList<>();
        sources.forEach(source -> convertedList.add(this.convert(source)));
        return convertedList;
    }

    @Override
    public EnrichedFileRecord convert(FileEvent source) {
        EnrichedFileRecord enrichedEvent = new EnrichedFileRecord(source.getDateTime());
        enrichedEvent.setUserId(source.getUser().getUserId());
        enrichedEvent.setEventId(source.getEventId());
        enrichedEvent.setAbsoluteDstFilePath(source.getFileOperation().getDestinationFile().getAbsoluteFilePath());
        enrichedEvent.setAbsoluteDstFolderFilePath(source.getFileOperation().getDestinationFile().getAbsoluteFilePath());
        enrichedEvent.setAbsoluteSrcFilePath(source.getFileOperation().getSourceFile().getAbsoluteFilePath());
        enrichedEvent.setAbsoluteSrcFolderFilePath(source.getFileOperation().getSourceFile().getAbsoluteFilePath());
        enrichedEvent.setDstDriveShared(source.getFileOperation().getDestinationFile().isDriveShared());
        enrichedEvent.setSrcDriveShared(source.getFileOperation().getSourceFile().isDriveShared());
        enrichedEvent.setFileSize(source.getFileOperation().getDestinationFile().getFileSize());
        enrichedEvent.setOperationType(source.getFileOperation().getOperationType());
        enrichedEvent.setOperationTypeCategories(source.getFileOperation().getOperationTypesCategories());
        enrichedEvent.setDataSource(source.getDataSource());
        enrichedEvent.setResult(EventResult.getEventResult(source.getFileOperation().getOperationResult()));
        enrichedEvent.setResultCode(source.getFileOperation().getOperationResultCode());

        return enrichedEvent;
    }
}
