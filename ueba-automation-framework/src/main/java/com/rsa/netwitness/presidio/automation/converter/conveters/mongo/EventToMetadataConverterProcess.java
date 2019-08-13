package com.rsa.netwitness.presidio.automation.converter.conveters.mongo;

import com.rsa.netwitness.presidio.automation.common.helpers.NamesConversionUtils;
import presidio.data.domain.event.process.ProcessEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventToMetadataConverterProcess implements EventToMetadataConverter<ProcessEvent> {

    @Override
    public Map<String, Object> convert(ProcessEvent event) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("event_source_id", event.getEventId());
        metadata.put("event_time", String.valueOf(event.getDateTime().toEpochMilli()));
        metadata.put("device_type", "Netwitness Endpoint");
        metadata.put("mongo_source_event_time", event.getDateTime());
        metadata.put("user_src", event.getUser().getUserId());
        metadata.put("action", new String[] {NamesConversionUtils.revertProcessOperationType(event.getProcessOperation().getOperationType().getName())});
        metadata.put("alias_host", new String[] {event.getMachineEntity().getMachineId()});
        metadata.put("owner", event.getMachineEntity().getOwner());
        metadata.put("directory_src", event.getProcessOperation().getSourceProcess().getProcessDirectory());
        metadata.put("filename_src", event.getProcessOperation().getSourceProcess().getProcessFileName());

        List<String> processDirectoryGroups = event.getProcessOperation().getSourceProcess().getProcessDirectoryGroups();
        if(processDirectoryGroups != null && !processDirectoryGroups.isEmpty()) {
            metadata.put("dir_path_src", NamesConversionUtils.revertDirGroups(processDirectoryGroups));
        }

        processDirectoryGroups = event.getProcessOperation().getDestinationProcess().getProcessDirectoryGroups();
        if(processDirectoryGroups != null && !processDirectoryGroups.isEmpty()) {
            metadata.put("dir_path_dst", NamesConversionUtils.revertDirGroups(processDirectoryGroups));
        }

        List<String> processCategories = event.getProcessOperation().getSourceProcess().getProcessCategories();
        if(processCategories != null && !processCategories.isEmpty()) {
            metadata.put("file_cat_src", NamesConversionUtils.revertCategories(processCategories));
        }
        processCategories = event.getProcessOperation().getDestinationProcess().getProcessCategories();
        if(processCategories != null && !processCategories.isEmpty()) {
            metadata.put("file_cat_dst", NamesConversionUtils.revertCategories(processCategories));
        }
        metadata.put("cert_common", event.getProcessOperation().getSourceProcess().getProcessCertificateIssuer());
        metadata.put("directory_dst", event.getProcessOperation().getDestinationProcess().getProcessDirectory());
        metadata.put("filename_dst", event.getProcessOperation().getDestinationProcess().getProcessFileName());

        // Fields for fake links to "investigate", not used by ADE
        metadata.put("checksum-id", "c01b39c7a35ccc3b081a3e83d2c71fa9a767ebfeb45c69f08e17dfe3ef375a7b");
        metadata.put("agent-id", "agent_001");
        metadata.put("os-type", event.getMachineEntity().getOsVersion());
        metadata.put("process-vid-src", event.getEventId().replace("EV","pr"));

        return metadata;
    }

}
