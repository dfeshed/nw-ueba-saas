package com.rsa.netwitness.presidio.automation.utils.ade.inserter.file;

import fortscale.domain.core.EventResult;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.file.EnrichedFileRecord;
import presidio.data.domain.FileEntity;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.domain.event.file.FileOperation;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts FileEvent list of events generator
 * into EnrichedFileRecord list of ADE SDK.
 */
public class AdeFileEventsConverter {
    public List<? extends EnrichedRecord> convert(List<? extends Event> events) {

        List<EnrichedFileRecord> enrichedEventsList = new ArrayList<>();

        for (FileEvent event : (List<FileEvent>)events) {

            FileOperation fileOperation = event.getFileOperation();

            EnrichedFileRecord enrichedEvent = new EnrichedFileRecord(event.getDateTime());
            enrichedEvent.setEventId(event.getEventId());
            enrichedEvent.setDataSource(event.getDataSource());
            enrichedEvent.setOperationType(fileOperation.getOperationType().getName());
            enrichedEvent.setOperationTypeCategories(fileOperation.getOperationType().getCategories());
            enrichedEvent.setResult(EventResult.valueOf(fileOperation.getOperationResult()));
            enrichedEvent.setResultCode(fileOperation.getOperationResultCode());

            enrichedEvent.setUserId(event.getUser().getUserId());

            FileEntity srcFile = fileOperation.getSourceFile();
            FileEntity destFile = fileOperation.getDestinationFile();
            enrichedEvent.setAbsoluteSrcFilePath(srcFile.getAbsoluteFilePath());
            enrichedEvent.setAbsoluteDstFilePath (destFile.getAbsoluteFilePath());
            enrichedEvent.setAbsoluteSrcFolderFilePath(srcFile.getFilePath());
            enrichedEvent.setAbsoluteDstFolderFilePath(destFile.getFilePath());
            enrichedEvent.setFileSize(srcFile.getFileSize());
            enrichedEvent.setSrcDriveShared(srcFile.isDriveShared());
            enrichedEvent.setDstDriveShared(destFile.isDriveShared());

            enrichedEventsList.add(enrichedEvent);
        }
        return enrichedEventsList;
    }
}
