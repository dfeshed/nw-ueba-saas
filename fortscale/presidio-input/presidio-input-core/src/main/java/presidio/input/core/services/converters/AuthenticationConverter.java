package presidio.input.core.services.converters;

import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.domain.core.AuthenticationRawEvent;
import presidio.ade.domain.record.enriched.EnrichedAuthenticationRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;

public class AuthenticationConverter implements InputAdeConverter {
    @Override
    public EnrichedRecord convert(AbstractAuditableDocument document) {
        AuthenticationRawEvent authenticationRawEvent = (AuthenticationRawEvent) document;
        EnrichedAuthenticationRecord adeRecord = new EnrichedAuthenticationRecord(authenticationRawEvent.getDateTime());
        return adeRecord;
    }
}
