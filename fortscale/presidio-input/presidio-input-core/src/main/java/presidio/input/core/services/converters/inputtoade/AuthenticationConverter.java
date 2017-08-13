package presidio.input.core.services.converters.inputtoade;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.authentication.EnrichedAuthenticationRecord;

import presidio.sdk.api.domain.transformedevents.AuthenticationTransformedEvent;

public class AuthenticationConverter implements InputAdeConverter {
    @Override
    public EnrichedRecord convert(AbstractAuditableDocument document) {
        AuthenticationTransformedEvent authenticationRawEvent = (AuthenticationTransformedEvent) document;
        EnrichedAuthenticationRecord adeRecord = new EnrichedAuthenticationRecord(authenticationRawEvent.getDateTime());
        adeRecord.setEventId(authenticationRawEvent.getEventId());
        adeRecord.setDataSource(authenticationRawEvent.getDataSource());
        adeRecord.setUserId(authenticationRawEvent.getUserId());
        adeRecord.setOperationType(authenticationRawEvent.getOperationType());
        adeRecord.setOperationTypeCategories(authenticationRawEvent.getOperationTypeCategory());
        adeRecord.setResult(authenticationRawEvent.getResult());
        adeRecord.setSrcMachineId(authenticationRawEvent.getSrcMachineId());
        adeRecord.setSrcMachineNameRegexCluster(authenticationRawEvent.getSrcMachineCluster());
        adeRecord.setDstMachineId(authenticationRawEvent.getDstMachineId());
        adeRecord.setDstMachineNameRegexCluster(authenticationRawEvent.getDstMachineCluster());
        adeRecord.setDstMachineDomain(authenticationRawEvent.getDstMachineDomain());
        return adeRecord;
    }
}
