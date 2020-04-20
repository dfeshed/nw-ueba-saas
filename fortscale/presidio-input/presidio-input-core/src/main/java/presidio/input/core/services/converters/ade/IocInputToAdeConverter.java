package presidio.input.core.services.converters.ade;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.ioc.EnrichedIocRecord;
import presidio.sdk.api.domain.transformedevents.IocTransformedEvent;

public class IocInputToAdeConverter implements InputAdeConverter {

    @Override
    public EnrichedRecord convert(AbstractAuditableDocument document) {
        IocTransformedEvent iocTransformedEvent = (IocTransformedEvent) document;
        EnrichedIocRecord adeRecord = new EnrichedIocRecord(iocTransformedEvent.getDateTime());
        adeRecord.setEventId(iocTransformedEvent.getEventId());
        adeRecord.setDataSource(iocTransformedEvent.getDataSource());
        adeRecord.setUserId(iocTransformedEvent.getUserId());
        adeRecord.setMachineId(iocTransformedEvent.getMachineId());
        adeRecord.setName(iocTransformedEvent.getName());
        adeRecord.setTactic(iocTransformedEvent.getTactic());
        adeRecord.setLevel(iocTransformedEvent.getLevel());
        return adeRecord;
    }
}
