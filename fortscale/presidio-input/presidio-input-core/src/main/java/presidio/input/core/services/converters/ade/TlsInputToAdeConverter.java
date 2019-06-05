package presidio.input.core.services.converters.ade;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.tls.EnrichedTlsRecord;
import presidio.sdk.api.domain.transformedevents.TlsTransformedEvent;

public class TlsInputToAdeConverter implements InputAdeConverter {

    @Override
    public EnrichedRecord convert(AbstractAuditableDocument document) {
        TlsTransformedEvent tlsTransformedEvent = (TlsTransformedEvent) document;
        EnrichedTlsRecord adeRecord = new EnrichedTlsRecord(tlsTransformedEvent.getDateTime());
        adeRecord.setEventId(tlsTransformedEvent.getEventId());
        adeRecord.setDataSource(tlsTransformedEvent.getDataSource());
        adeRecord.setDomain(tlsTransformedEvent.getDomain());
        adeRecord.setDstAsn(tlsTransformedEvent.getDstAsn());
        adeRecord.setDstCountry(tlsTransformedEvent.getDstCountry());
        adeRecord.setDstNetname(tlsTransformedEvent.getDstNetname());
        adeRecord.setDstOrg(tlsTransformedEvent.getDstOrg());
        adeRecord.setDstPort(tlsTransformedEvent.getDstPort());
        adeRecord.setJa3(tlsTransformedEvent.getJa3());
        adeRecord.setJa3s(tlsTransformedEvent.getJa3s());
        adeRecord.setNumOfBytesReceived(tlsTransformedEvent.getNumOfBytesReceived());
        adeRecord.setNumOfBytesSent(tlsTransformedEvent.getNumOfBytesSent());
        adeRecord.setDirection(tlsTransformedEvent.getDirection());
        adeRecord.setSrcIp(tlsTransformedEvent.getSrcIp());
        adeRecord.setDstIp(tlsTransformedEvent.getDstIp());
        adeRecord.setSrcNetname(tlsTransformedEvent.getSrcNetname());
        adeRecord.setSslSubject(tlsTransformedEvent.getSslSubject());

        return adeRecord;
    }
}