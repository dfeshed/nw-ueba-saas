package presidio.input.core.services.converters;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.authentication.EnrichedAuthenticationRecord;
import presidio.sdk.api.domain.AuthenticationRawEvent;

public class AuthenticationConverter implements InputAdeConverter {
    @Override
    public EnrichedRecord convert(AbstractAuditableDocument document) {
        AuthenticationRawEvent authenticationRawEvent = (AuthenticationRawEvent) document;
        EnrichedAuthenticationRecord adeRecord = new EnrichedAuthenticationRecord(authenticationRawEvent.getDateTime());
        return adeRecord;
    }
}
