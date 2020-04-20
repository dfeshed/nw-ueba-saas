package com.rsa.netwitness.presidio.automation.utils.ade.inserter.dlpfile;

import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.dlpfile.EnrichedDlpFileRecord;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.dlpfile.DLPFileEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yaron DL on 7/10/2017.
 *
 */
public class AdeDLPFileEventsConverter{
    public List<? extends EnrichedRecord> convert(List<? extends Event> events) {

        List<EnrichedDlpFileRecord> enrichedEventsList = new ArrayList<>();

        for (DLPFileEvent event : (List<DLPFileEvent>)events) {
            EnrichedDlpFileRecord enrichedEvent = new EnrichedDlpFileRecord(event.getDateTime());

            enrichedEvent.setUserId(event.getNormalizedUsername());
            enrichedEvent.setSrcMachineId(event.getNormalized_src_machine());
            enrichedEvent.setSourcePath(event.getSourcePath());
            enrichedEvent.setSourceFileName(event.getSourceFileName());
            enrichedEvent.setSourceDriveType(event.getSourceDriveType());
            enrichedEvent.setDestinationPath(event.getDestinationPath());
            enrichedEvent.setDestinationFileName(event.getDestinationFileName());
            enrichedEvent.setDestinationDriveType(event.getDestinationDriveType());
            enrichedEvent.setFileSize(event.getFileSize());
            enrichedEvent.setOperationType(event.getEventType());
            enrichedEvent.setWasBlocked(event.getWasBlocked());
            enrichedEvent.setWasClassified(event.getWasClassified());
            enrichedEvent.setMalwareScanResult(event.getMalwareScanResult());
            enrichedEvent.setExecutingApplication(event.getExecutingApplication());

            enrichedEventsList.add(enrichedEvent);
        }
        return enrichedEventsList;
    }
}
