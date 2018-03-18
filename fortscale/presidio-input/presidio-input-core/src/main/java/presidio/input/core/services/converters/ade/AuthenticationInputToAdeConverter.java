package presidio.input.core.services.converters.ade;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.authentication.EnrichedAuthenticationRecord;
import presidio.sdk.api.domain.transformedevents.AuthenticationTransformedEvent;

public class AuthenticationInputToAdeConverter implements InputAdeConverter {
    @Override
    public EnrichedRecord convert(AbstractAuditableDocument document) {
        AuthenticationTransformedEvent authenticationTransformedEvent = (AuthenticationTransformedEvent) document;
        EnrichedAuthenticationRecord adeRecord = new EnrichedAuthenticationRecord(authenticationTransformedEvent.getDateTime());
        adeRecord.setEventId(authenticationTransformedEvent.getEventId());
        adeRecord.setDataSource(authenticationTransformedEvent.getDataSource());
        adeRecord.setUserId(authenticationTransformedEvent.getUserId());
        adeRecord.setOperationType(authenticationTransformedEvent.getOperationType());
        adeRecord.setOperationTypeCategories(authenticationTransformedEvent.getOperationTypeCategories());
        adeRecord.setResult(authenticationTransformedEvent.getResult());
        adeRecord.setSrcMachineId(authenticationTransformedEvent.getSrcMachineId());
        adeRecord.setSrcMachineNameRegexCluster(authenticationTransformedEvent.getSrcMachineCluster());
        adeRecord.setDstMachineId(authenticationTransformedEvent.getDstMachineId());
        adeRecord.setDstMachineNameRegexCluster(authenticationTransformedEvent.getDstMachineCluster());
        adeRecord.setDstMachineDomain(authenticationTransformedEvent.getDstMachineDomain());
        adeRecord.setResultCode(authenticationTransformedEvent.getResultCode());
        adeRecord.setSite(authenticationTransformedEvent.getSite());
        adeRecord.setCity(authenticationTransformedEvent.getCity());
        adeRecord.setCountry(authenticationTransformedEvent.getCountry());
        return adeRecord;
    }
}
