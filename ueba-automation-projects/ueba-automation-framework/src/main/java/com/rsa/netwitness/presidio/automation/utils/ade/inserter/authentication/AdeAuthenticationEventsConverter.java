package com.rsa.netwitness.presidio.automation.utils.ade.inserter.authentication;

import fortscale.domain.core.EventResult;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.authentication.EnrichedAuthenticationRecord;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.authentication.AuthenticationEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts AuthenticationEvent list of events generator
 * into EnrichedAuthenticationRecord list of ADE SDK.
 */
public class AdeAuthenticationEventsConverter {
    public List<? extends EnrichedRecord> convert(List<? extends Event> events) {

        List<EnrichedAuthenticationRecord> enrichedEventsList = new ArrayList<>();

        for (AuthenticationEvent event : (List<AuthenticationEvent>)events) {
            EnrichedAuthenticationRecord enrichedEvent = new EnrichedAuthenticationRecord(event.getDateTime());
            enrichedEvent.setEventId(event.getEventId());
            enrichedEvent.setDataSource(event.getDataSource());
            enrichedEvent.setOperationType(event.getAuthenticationOperation().getOperationType().getName());
            enrichedEvent.setOperationTypeCategories(event.getAuthenticationOperation().getOperationType().getCategories());
            enrichedEvent.setResult(EventResult.valueOf(event.getResult()));
            enrichedEvent.setResultCode(event.getResultCode());
            enrichedEvent.setUserId(event.getUser().getUserId());
            enrichedEvent.setSrcMachineId(event.getSrcMachineEntity().getMachineId());
            enrichedEvent.setDstMachineId(event.getDstMachineEntity().getMachineId());
            enrichedEvent.setSrcMachineNameRegexCluster(event.getSrcMachineEntity().getMachineId());
            enrichedEvent.setDstMachineNameRegexCluster(event.getDstMachineEntity().getMachineId());
            enrichedEvent.setDstMachineDomain(event.getDstMachineEntity().getMachineDomain());

            enrichedEventsList.add(enrichedEvent);
        }
        return enrichedEventsList;
    }
}
