package presidio.input.core.services.converters;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.ade.domain.record.enriched.EnrichedDlpFileRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.sdk.api.domain.DlpFileEnrichedDocument;

public class DlpFileConverter implements InputAdeConverter {

    @Override
    public EnrichedRecord convert(AbstractAuditableDocument document) {
        EnrichedDlpFileRecord adeRecord = new EnrichedDlpFileRecord(document.getDateTime());
        DlpFileEnrichedDocument dlpFileEnrichedDocument = (DlpFileEnrichedDocument) document;
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

