package com.rsa.netwitness.presidio.automation.utils.input.inserter.registry;

import presidio.data.domain.event.Event;
import presidio.data.domain.event.registry.RegistryEvent;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.RegistryRawEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InputRegistryEventsConverter {
    public List<? extends AbstractInputDocument> convert(List<? extends Event> events) {

        ArrayList<RegistryRawEvent> records = new ArrayList<>();
        for (RegistryEvent event : (List<RegistryEvent>) events) {
            HashMap<String, String> additionalInfo = new HashMap();
            // prepare Additional Info

            RegistryRawEvent storeRecord = new RegistryRawEvent(
                    event.getDateTime(),
                    event.getEventId(),
                    event.getDataSource(),
                    event.getUser().getUserId(),
                    event.getRegistryOperation().getOperationType().getName(),
                    event.getUser().getUsername(),
                    event.getUser().getFirstName() + " " + event.getUser().getLastName(),
                    additionalInfo,
                    event.getMachineEntity().getMachineId(),
                    event.getMachineEntity().getMachineNameRegexCluster(),
                    "machineOwner",
                    event.getRegistryOperation().getProcess().getProcessDirectory(),
                    event.getRegistryOperation().getProcess().getProcessFileName(),
                    event.getRegistryOperation().getProcess().getProcessDirectoryGroups(),
                    event.getRegistryOperation().getProcess().getProcessCategories(),
                    event.getRegistryOperation().getProcess().getProcessCertificateIssuer(),
                    event.getRegistryOperation().getRegistryEntry().getKeyGroup(),
                    event.getRegistryOperation().getRegistryEntry().getKey(),
                    event.getRegistryOperation().getRegistryEntry().getValueName());
            records.add(storeRecord);
        }
        return records;
    }
}