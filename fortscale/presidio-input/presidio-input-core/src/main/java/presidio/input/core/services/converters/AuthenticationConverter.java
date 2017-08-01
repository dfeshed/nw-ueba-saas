package presidio.input.core.services.converters;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.ade.domain.record.enriched.EnrichedAuthenticationRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import fortscale.domain.core.AuthenticationRawEvent;

public class AuthenticationConverter implements InputAdeConverter {
    @Override
    public EnrichedRecord convert(AbstractAuditableDocument document) {
        AuthenticationRawEvent authenticationRawEvent = (AuthenticationRawEvent) document;
        EnrichedAuthenticationRecord adeRecord = new EnrichedAuthenticationRecord(authenticationRawEvent.getDateTime());
        adeRecord.setNormalizedUsername(adeRecord.getNormalizedUsername());
        adeRecord.setResult(authenticationRawEvent.getResult().toString());
        adeRecord.setAuthenticationType(authenticationRawEvent.getAuthenticationType().toString());
        adeRecord.setEventId(authenticationRawEvent.getEventId());
        adeRecord.setDstMachineRemote(authenticationRawEvent.getIsDstMachineRemote());
        adeRecord.setNormalizedDstMachine(authenticationRawEvent.getNormalizedDstMachine());
        adeRecord.setNormalizedSrcMachine(authenticationRawEvent.getNormalizedSrcMachine());
        adeRecord.setResultCode(authenticationRawEvent.getResultCode().toString());
        return adeRecord;
    }
}
