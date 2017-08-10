package presidio.input.core.services.converters.inputtoade;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.dlpfile.EnrichedDlpFileRecord;
import presidio.sdk.api.domain.DlpFileEnrichedDocument;

/**
 * Created by alexp on 08-Jun-17.
 */
public class DlpFileConverter implements InputAdeConverter {

    @Override
    public EnrichedRecord convert(AbstractAuditableDocument document) {
        DlpFileEnrichedDocument dlpFileEnrichedDocument = (DlpFileEnrichedDocument) document;
        EnrichedDlpFileRecord adeRecord = new EnrichedDlpFileRecord(dlpFileEnrichedDocument.getDateTime());
        adeRecord.setUserId(dlpFileEnrichedDocument.getNormalizedUsername());
        adeRecord.setSrcMachineId(dlpFileEnrichedDocument.getNormalizedSrcMachine());
        adeRecord.setSourcePath(dlpFileEnrichedDocument.getSourcePath());
        adeRecord.setSourceFileName(dlpFileEnrichedDocument.getSourceFileName());
        adeRecord.setSourceDriveType(dlpFileEnrichedDocument.getSourceDriveType());
        adeRecord.setDestinationPath(dlpFileEnrichedDocument.getDestinationPath());
        adeRecord.setDestinationFileName(dlpFileEnrichedDocument.getDestinationFileName());
        adeRecord.setDestinationDriveType(dlpFileEnrichedDocument.getDestinationDriveType());
        adeRecord.setFileSize(dlpFileEnrichedDocument.getFileSize());
        adeRecord.setOperationType(dlpFileEnrichedDocument.getEventType());
        adeRecord.setWasBlocked(dlpFileEnrichedDocument.getWasBlocked());
        adeRecord.setWasClassified(dlpFileEnrichedDocument.getWasClassified());
        adeRecord.setMalwareScanResult(dlpFileEnrichedDocument.getMalwareScanResult());
        adeRecord.setExecutingApplication(dlpFileEnrichedDocument.getExecutingApplication());
        return adeRecord;
    }
}

