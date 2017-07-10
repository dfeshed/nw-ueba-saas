package presidio.input.core.services.converters;

import presidio.ade.domain.record.enriched.EnrichedDlpFileRecord;
import presidio.sdk.api.domain.DlpFileEnrichedDocument;

/**
 * Created by alexp on 08-Jun-17.
 */
public class DlpFileConverter {
    public EnrichedDlpFileRecord convert(DlpFileEnrichedDocument dlpFileEnrichedDocument) {
        EnrichedDlpFileRecord adeRecord = new EnrichedDlpFileRecord(dlpFileEnrichedDocument.getDateTime());
        adeRecord.setNormalizedUsername(dlpFileEnrichedDocument.getNormalizedUsername());
        adeRecord.setNormalizedSrcMachine(dlpFileEnrichedDocument.getNormalizedSrcMachine());
        adeRecord.setSourcePath(dlpFileEnrichedDocument.getSourcePath());
        adeRecord.setSourceFileName(dlpFileEnrichedDocument.getSourceFileName());
        adeRecord.setSourceDriveType(dlpFileEnrichedDocument.getSourceDriveType());
        adeRecord.setDestinationPath(dlpFileEnrichedDocument.getDestinationPath());
        adeRecord.setDestinationFileName(dlpFileEnrichedDocument.getDestinationFileName());
        adeRecord.setDestinationDriveType(dlpFileEnrichedDocument.getDestinationDriveType());
        adeRecord.setFileSize(dlpFileEnrichedDocument.getFileSize());
        adeRecord.setEventType(dlpFileEnrichedDocument.getEventType());
        adeRecord.setWasBlocked(dlpFileEnrichedDocument.getWasBlocked());
        adeRecord.setWasClassified(dlpFileEnrichedDocument.getWasClassified());
        adeRecord.setMalwareScanResult(dlpFileEnrichedDocument.getMalwareScanResult());
        adeRecord.setExecutingApplication(dlpFileEnrichedDocument.getExecutingApplication());
        return adeRecord;
    }
}

