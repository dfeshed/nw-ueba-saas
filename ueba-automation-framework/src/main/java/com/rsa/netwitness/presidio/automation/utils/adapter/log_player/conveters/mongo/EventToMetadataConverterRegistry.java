package com.rsa.netwitness.presidio.automation.utils.adapter.log_player.conveters.mongo;

import com.rsa.netwitness.presidio.automation.common.helpers.NamesConversionUtils;
import edu.emory.mathcs.backport.java.util.Arrays;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.registry.RegistryEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventToMetadataConverterRegistry implements EventToMetadataConverter {

    @Override
    public List<Map<String, Object>> convert(Map<String, String> config, List<? extends Event> events) {
        List<Map<String, Object>> metadataList = new ArrayList<>(events.size());

        for (RegistryEvent event : (List<RegistryEvent>)events) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("event_source_id", event.getEventId());
            metadata.put("event_time", String.valueOf(event.getDateTime().toEpochMilli()));
            metadata.put("device_type", "Netwitness Endpoint");
            metadata.put("mongo_source_event_time", event.getDateTime());
            metadata.put("user_src", event.getUser().getUserId());
            metadata.put("action", new String[] {NamesConversionUtils.revert2LowerCamel(NamesConversionUtils.fixRegistryOperation(event.getRegistryOperation().getOperationType().getName()))});
            metadata.put("alias_host", new String[] {event.getMachineEntity().getMachineId()});
            metadata.put("owner", event.getMachineEntity().getOwner());
            metadata.put("directory_src", event.getRegistryOperation().getProcess().getProcessDirectory());
            metadata.put("filename_src", event.getRegistryOperation().getProcess().getProcessFileName());
            List<String> processDirectoryGroups = event.getRegistryOperation().getProcess().getProcessDirectoryGroups();
            if(processDirectoryGroups != null && !processDirectoryGroups.isEmpty()) {
                metadata.put("dir_path_src", NamesConversionUtils.revertDirGroups(processDirectoryGroups));
            } else {
                metadata.put("dir_path_src", NamesConversionUtils.revertDirGroups(Arrays.asList(new String[] {"WINDOWS_SYSTEM32","WINDOWS"})));
            }
            List<String> processCategories = event.getRegistryOperation().getProcess().getProcessCategories();
            if(processCategories != null && !processCategories.isEmpty()) {
                metadata.put("file_cat", NamesConversionUtils.revertCategories(processCategories));
            } else {
                metadata.put("file_cat", NamesConversionUtils.revertCategories(Arrays.asList(new String[] {"WINDOWS_PROCESS"})));
            }
            metadata.put("cert_common", event.getRegistryOperation().getProcess().getProcessCertificateIssuer());
            metadata.put("ec_subject", NamesConversionUtils.revert2LowerCamel(event.getRegistryOperation().getRegistryEntry().getKeyGroup()));
            metadata.put("registry_key", event.getRegistryOperation().getRegistryEntry().getKey() + event.getRegistryOperation().getRegistryEntry().getValueName());
            metadataList.add(metadata);
        }

        return metadataList;
    }
}
