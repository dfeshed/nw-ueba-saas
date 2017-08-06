package presidio.ade.test.utils.converters;

import org.springframework.core.convert.converter.Converter;
import presidio.ade.domain.record.enriched.dlpfile.EnrichedDlpFileRecord;
import presidio.data.domain.event.dlpfile.DLPFileEvent;

import java.util.LinkedList;
import java.util.List;

public class DLPFileRaw2EnrichedConverter implements Converter<DLPFileEvent,EnrichedDlpFileRecord> {

    public List<EnrichedDlpFileRecord> convert(List<DLPFileEvent> sources)
    {
        List <EnrichedDlpFileRecord> convertedList = new LinkedList<>();
        sources.forEach(source -> convertedList.add(this.convert(source)));
        return convertedList;
    }

    @Override
    public EnrichedDlpFileRecord convert(DLPFileEvent source) {
        EnrichedDlpFileRecord enrichedEvent = new EnrichedDlpFileRecord(source.getDateTime());

        enrichedEvent.setUserId(source.getNormalizedUsername());
        enrichedEvent.setSrcMachineId(source.getNormalized_src_machine());
        enrichedEvent.setSourcePath(source.getSourcePath());
        enrichedEvent.setSourceFileName(source.getSourceFileName());
        enrichedEvent.setSourceDriveType(source.getSourceDriveType());
        enrichedEvent.setDestinationPath(source.getDestinationPath());
        enrichedEvent.setDestinationFileName(source.getDestinationFileName());
        enrichedEvent.setDestinationDriveType(source.getDestinationDriveType());
        enrichedEvent.setFileSize(source.getFileSize());
        enrichedEvent.setOperationType(source.getEventType());
        enrichedEvent.setWasBlocked(source.getWasBlocked());
        enrichedEvent.setWasClassified(source.getWasClassified());
        enrichedEvent.setMalwareScanResult(source.getMalwareScanResult());
        enrichedEvent.setExecutingApplication(source.getExecutingApplication());

        return enrichedEvent;
    }
}
