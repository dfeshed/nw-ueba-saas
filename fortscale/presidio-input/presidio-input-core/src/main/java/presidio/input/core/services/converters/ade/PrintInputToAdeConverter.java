package presidio.input.core.services.converters.ade;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.print.EnrichedPrintRecord;
import presidio.sdk.api.domain.transformedevents.PrintTransformedEvent;

public class PrintInputToAdeConverter implements InputAdeConverter {
    @Override
    public EnrichedRecord convert(AbstractAuditableDocument document) {
        PrintTransformedEvent printTransformedEvent = (PrintTransformedEvent) document;
        EnrichedPrintRecord adeRecord = new EnrichedPrintRecord(printTransformedEvent.getDateTime());
        adeRecord.setAbsoluteFilePath(printTransformedEvent.getSrcFilePath());
        adeRecord.setAbsoluteFolderPath(printTransformedEvent.getSrcFolderPath());
        adeRecord.setFileExtension(printTransformedEvent.getSrcFileExtension());
        adeRecord.setDriveShared(printTransformedEvent.getSrcDriveShared());
        adeRecord.setDstMachineId(printTransformedEvent.getPrinterId());
        adeRecord.setDstMachineNameRegexCluster(printTransformedEvent.getPrinterCluster());
        adeRecord.setFileSizeInBytes(printTransformedEvent.getFileSize());
        adeRecord.setNumOfPages(printTransformedEvent.getNumOfPages());
        adeRecord.setSrcMachineId(printTransformedEvent.getSrcMachineId());
        adeRecord.setSrcMachineNameRegexCluster(printTransformedEvent.getSrcMachineCluster());
        adeRecord.setUserId(printTransformedEvent.getUserId());
        adeRecord.setDataSource(printTransformedEvent.getDataSource());
        adeRecord.setEventId(printTransformedEvent.getEventId());
        adeRecord.setOperationType(printTransformedEvent.getOperationType());
        adeRecord.setOperationTypeCategories(printTransformedEvent.getOperationTypeCategories());
        adeRecord.setResult(printTransformedEvent.getResult());
        adeRecord.setResultCode(printTransformedEvent.getResultCode());
        return adeRecord;
    }
}
