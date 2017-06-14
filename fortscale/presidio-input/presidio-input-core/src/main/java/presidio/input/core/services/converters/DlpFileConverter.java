package presidio.input.core.services.converters;

import presidio.ade.domain.record.enriched.EnrichedDlpFileRecord;
import presidio.sdk.api.domain.DlpFileEnrichedDocument;

/**
 * Created by alexp on 08-Jun-17.
 */
public class DlpFileConverter {
    public EnrichedDlpFileRecord convert(DlpFileEnrichedDocument dlpFileEnrichedDocument) {
        EnrichedDlpFileRecord adeRecord = new EnrichedDlpFileRecord(dlpFileEnrichedDocument.getDateTime());
        adeRecord.setNormalized_username(dlpFileEnrichedDocument.getNormalizedUsername());
        adeRecord.setNormalized_src_machine(dlpFileEnrichedDocument.getNormalizedSrcMachine());
        adeRecord.setSource_path(dlpFileEnrichedDocument.getSourcePath());
        adeRecord.setSource_file_name(dlpFileEnrichedDocument.getSourceFileName());
        adeRecord.setSource_drive_type(dlpFileEnrichedDocument.getSourceDriveType());
        adeRecord.setDestination_path(dlpFileEnrichedDocument.getDestinationPath());
        adeRecord.setDestination_file_name(dlpFileEnrichedDocument.getDestinationFileName());
        adeRecord.setDestination_drive_type(dlpFileEnrichedDocument.getDestinationDriveType());
        adeRecord.setFile_size(dlpFileEnrichedDocument.getFileSize());
        adeRecord.setEvent_type(dlpFileEnrichedDocument.getEventType());
        adeRecord.setWas_blocked(dlpFileEnrichedDocument.getWasBlocked());
        adeRecord.setWas_classified(dlpFileEnrichedDocument.getWasClassified());
        adeRecord.setMalware_scan_result(dlpFileEnrichedDocument.getMalwareScanResult());
        adeRecord.setExecuting_application(dlpFileEnrichedDocument.getExecutingApplication());
        return adeRecord;
    }
}

