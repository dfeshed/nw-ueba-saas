package com.rsa.netwitness.presidio.automation.utils.ade.inserter.process;

import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.process.EnrichedProcessRecord;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.process.ProcessEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts FileEvent list of events generator
 * into EnrichedFileRecord list of ADE SDK.
 */
public class AdeProcessEventsConverter {
    public List<? extends EnrichedRecord> convert(List<? extends Event> events) {

        List<EnrichedProcessRecord> enrichedEventsList = new ArrayList<>();

        for (ProcessEvent event : (List<ProcessEvent>)events) {

            EnrichedProcessRecord enrichedEvent = new EnrichedProcessRecord(event.getDateTime());
            enrichedEvent.setUserId(event.getUser().getUserId());
            enrichedEvent.setEventId(event.getEventId());
            enrichedEvent.setDataSource(event.getDataSource());
            enrichedEvent.setMachineId(event.getMachineEntity().getMachineId());

            enrichedEvent.setSrcProcessDirectory(event.getProcessOperation().getSourceProcess().getProcessDirectory());
            enrichedEvent.setSrcProcessFileName(event.getProcessOperation().getSourceProcess().getProcessFileName());
            enrichedEvent.setSrcProcessDirectoryGroups(event.getProcessOperation().getSourceProcess().getProcessDirectoryGroups());
            enrichedEvent.setSrcProcessCategories(event.getProcessOperation().getSourceProcess().getProcessCategories());
            enrichedEvent.setSrcProcessCertificateIssuer(event.getProcessOperation().getSourceProcess().getProcessCertificateIssuer());

            enrichedEvent.setDstProcessDirectory(event.getProcessOperation().getDestinationProcess().getProcessDirectory());
            enrichedEvent.setDstProcessFileName(event.getProcessOperation().getDestinationProcess().getProcessFileName());
            enrichedEvent.setDstProcessDirectoryGroups(event.getProcessOperation().getDestinationProcess().getProcessDirectoryGroups());
            enrichedEvent.setDstProcessCategories(event.getProcessOperation().getDestinationProcess().getProcessCategories());
            enrichedEvent.setDstProcessCertificateIssuer(event.getProcessOperation().getDestinationProcess().getProcessCertificateIssuer());

            enrichedEvent.setOperationType(event.getProcessOperation().getOperationType().getName());



            enrichedEventsList.add(enrichedEvent);
        }
        return enrichedEventsList;
    }
}
