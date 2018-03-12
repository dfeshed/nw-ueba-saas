package presidio.input.core.services.converters.ade;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.authentication.EnrichedAuthenticationRecord;
import presidio.sdk.api.domain.transformedevents.AuthenticationTransformedEvent;

public class AuthenticationInputToAdeConverter implements InputAdeConverter {
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
        adeRecord.setDstMachineId(authenticationRawEvent.getResourceId());
        adeRecord.setDstMachineNameRegexCluster(authenticationRawEvent.getResourceCluster());
        adeRecord.setDstMachineDomain(authenticationRawEvent.getResourceDomain());
        adeRecord.setResultCode(authenticationRawEvent.getResultCode());
        adeRecord.setSite(authenticationRawEvent.getSite());
        return adeRecord;
    }
}
