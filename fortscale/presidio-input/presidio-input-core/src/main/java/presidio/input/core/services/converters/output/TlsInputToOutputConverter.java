package presidio.input.core.services.converters.output;

import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.events.TlsEnrichedEvent;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.transformedevents.TlsTransformedEvent;

public class TlsInputToOutputConverter implements InputOutputConverter {
    @Override
    public EnrichedEvent convert(AbstractInputDocument document) {
        TlsTransformedEvent tlsTransformedEvent = (TlsTransformedEvent) document;
        TlsEnrichedEvent outputEvent = new TlsEnrichedEvent();

        outputEvent.setEventId(tlsTransformedEvent.getEventId());
        outputEvent.setEventDate(tlsTransformedEvent.getDateTime());
        outputEvent.setDataSource(tlsTransformedEvent.getDataSource());
        outputEvent.setDomain(tlsTransformedEvent.getDomain());
        outputEvent.setDstAsn(tlsTransformedEvent.getDstAsn());
        outputEvent.setSrcCountry(tlsTransformedEvent.getSrcCountry());
        outputEvent.setDstCountry(tlsTransformedEvent.getDstCountry());
        outputEvent.setDstNetname(tlsTransformedEvent.getDstNetname());
        outputEvent.setDstOrg(tlsTransformedEvent.getDstOrg());
        outputEvent.setDstPort(tlsTransformedEvent.getDstPort());
        outputEvent.setJa3(tlsTransformedEvent.getJa3());
        outputEvent.setJa3s(tlsTransformedEvent.getJa3s());
        outputEvent.setNumOfBytesReceived(tlsTransformedEvent.getNumOfBytesReceived());
        outputEvent.setNumOfBytesSent(tlsTransformedEvent.getNumOfBytesSent());
        outputEvent.setDirection(tlsTransformedEvent.getDirection());
        outputEvent.setSrcIp(tlsTransformedEvent.getSrcIp());
        outputEvent.setDstIp(tlsTransformedEvent.getDstIp());
        outputEvent.setSrcNetname(tlsTransformedEvent.getSrcNetname());
        outputEvent.setSslSubject(tlsTransformedEvent.getSslSubject());
        outputEvent.setAdditionalInfo(tlsTransformedEvent.getAdditionalInfo());

        return outputEvent;
    }
}
