package com.rsa.netwitness.presidio.automation.utils.ade.inserter.activedirectory;

import fortscale.domain.core.EventResult;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.activedirectory.EnrichedActiveDirectoryRecord;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts AuthenticationEvent list of events generator
 * into EnrichedAuthenticationRecord list of ADE SDK.
 */
public class AdeActiveDirectoryEventsConverter {
    public List<? extends EnrichedRecord> convert(List<? extends Event> events) {

        List<EnrichedActiveDirectoryRecord> enrichedEventsList = new ArrayList<>();

        for (ActiveDirectoryEvent event : (List<ActiveDirectoryEvent>)events) {
            EnrichedActiveDirectoryRecord enrichedEvent = new EnrichedActiveDirectoryRecord(event.getDateTime());
            enrichedEvent.setEventId(event.getEventId());
            enrichedEvent.setDataSource(event.getDataSource());
            enrichedEvent.setResult(EventResult.valueOf(event.getOperation().getOperationResult()));
            enrichedEvent.setResultCode(event.getOperation().getOperationResultCode());

            enrichedEvent.setUserId(event.getUser().getUserId());
            enrichedEvent.setOperationType(event.getOperation().getOperationType().getName());
            enrichedEvent.setOperationTypeCategories(event.getOperation().getOperationType().getCategories());
            enrichedEvent.setObjectId(event.getOperation().getObjectName());
            enrichedEventsList.add(enrichedEvent);
        }
        return enrichedEventsList;
    }
}