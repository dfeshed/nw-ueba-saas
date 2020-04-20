package presidio.input.core.services.converters.ade;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.process.EnrichedProcessRecord;
import presidio.sdk.api.domain.transformedevents.ProcessTransformedEvent;

public class ProcessInputToAdeConverter implements InputAdeConverter {

    @Override
    public EnrichedRecord convert(AbstractAuditableDocument document) {
        ProcessTransformedEvent processTransformedEvent = (ProcessTransformedEvent) document;
        EnrichedProcessRecord adeRecord = new EnrichedProcessRecord(processTransformedEvent.getDateTime());
        adeRecord.setEventId(processTransformedEvent.getEventId());
        adeRecord.setDataSource(processTransformedEvent.getDataSource());
        adeRecord.setUserId(processTransformedEvent.getUserId());
        adeRecord.setOperationType(processTransformedEvent.getOperationType());
        adeRecord.setMachineId(processTransformedEvent.getMachineId());
        adeRecord.setSrcProcessDirectory(processTransformedEvent.getSrcProcessDirectory());
        adeRecord.setSrcProcessFileName(processTransformedEvent.getSrcProcessFileName());
        adeRecord.setSrcProcessDirectoryGroups(processTransformedEvent.getSrcProcessDirectoryGroups());
        adeRecord.setSrcProcessCategories(processTransformedEvent.getSrcProcessCategories());
        adeRecord.setSrcProcessCertificateIssuer(processTransformedEvent.getSrcProcessCertificateIssuer());
        adeRecord.setDstProcessDirectory(processTransformedEvent.getDstProcessDirectory());
        adeRecord.setDstProcessFileName(processTransformedEvent.getDstProcessFileName());
        adeRecord.setDstProcessDirectoryGroups(processTransformedEvent.getDstProcessDirectoryGroups());
        adeRecord.setDstProcessCategories(processTransformedEvent.getDstProcessCategories());
        adeRecord.setDstProcessCertificateIssuer(processTransformedEvent.getDstProcessCertificateIssuer());
        return adeRecord;
    }
}
