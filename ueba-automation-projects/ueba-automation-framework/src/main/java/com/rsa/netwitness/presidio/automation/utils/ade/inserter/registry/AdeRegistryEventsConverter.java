package com.rsa.netwitness.presidio.automation.utils.ade.inserter.registry;

import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.registry.EnrichedRegistryRecord;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.registry.RegistryEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts FileEvent list of events generator
 * into EnrichedFileRecord list of ADE SDK.
 */
public class AdeRegistryEventsConverter {
    public List<? extends EnrichedRecord> convert(List<? extends Event> events) {

        List<EnrichedRegistryRecord> enrichedEventsList = new ArrayList<>();

        for (RegistryEvent event : (List<RegistryEvent>)events) {

            EnrichedRegistryRecord enrichedEvent = new EnrichedRegistryRecord(event.getDateTime());
            enrichedEvent.setUserId(event.getUser().getUserId());
            enrichedEvent.setEventId(event.getEventId());
            enrichedEvent.setDataSource(event.getDataSource());
            enrichedEvent.setMachineId(event.getMachineEntity().getMachineId());

            enrichedEvent.setProcessDirectory(event.getRegistryOperation().getProcess().getProcessDirectory());
            enrichedEvent.setProcessFileName(event.getRegistryOperation().getProcess().getProcessFileName());
            enrichedEvent.setProcessDirectoryGroups(event.getRegistryOperation().getProcess().getProcessDirectoryGroups());
            enrichedEvent.setProcessCategories(event.getRegistryOperation().getProcess().getProcessCategories());
            enrichedEvent.setProcessCertificateIssuer(event.getRegistryOperation().getProcess().getProcessCertificateIssuer());
            enrichedEvent.setRegistryKeyGroup(event.getRegistryOperation().getRegistryEntry().getKeyGroup());
            enrichedEvent.setRegistryKey(event.getRegistryOperation().getRegistryEntry().getKey());
            enrichedEvent.setRegistryValueName(event.getRegistryOperation().getRegistryEntry().getValueName());

            enrichedEvent.setOperationType(event.getRegistryOperation().getOperationType().getName());

            enrichedEventsList.add(enrichedEvent);
        }
        return enrichedEventsList;
    }
}
