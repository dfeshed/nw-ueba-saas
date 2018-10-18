package presidio.input.core.services.converters.ade;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.registry.EnrichedRegistryRecord;
import presidio.sdk.api.domain.transformedevents.RegistryTransformedEvent;

public class RegistryInputToAdeConverter implements InputAdeConverter {

    @Override
    public EnrichedRecord convert(AbstractAuditableDocument document) {
        RegistryTransformedEvent registryTransformedEvent = (RegistryTransformedEvent) document;
        EnrichedRegistryRecord adeRecord = new EnrichedRegistryRecord(registryTransformedEvent.getDateTime());
        adeRecord.setEventId(registryTransformedEvent.getEventId());
        adeRecord.setDataSource(registryTransformedEvent.getDataSource());
        adeRecord.setUserId(registryTransformedEvent.getUserId());
        adeRecord.setOperationType(registryTransformedEvent.getOperationType());
        adeRecord.setMachineId(registryTransformedEvent.getMachineId());
        adeRecord.setProcessDirectory(registryTransformedEvent.getProcessDirectory());
        adeRecord.setProcessFileName(registryTransformedEvent.getProcessFileName());
        adeRecord.setProcessDirectoryGroups(registryTransformedEvent.getProcessDirectoryGroups());
        adeRecord.setProcessCategories(registryTransformedEvent.getProcessCategories());
        adeRecord.setProcessCertificateIssuer(registryTransformedEvent.getProcessCertificateIssuer());
        adeRecord.setRegistryKeyGroup(registryTransformedEvent.getRegistryKeyGroup());
        adeRecord.setRegistryKey(registryTransformedEvent.getRegistryKey());
        adeRecord.setRegistryValueName(registryTransformedEvent.getRegistryValueName());
        return adeRecord;
    }
}