package com.rsa.netwitness.presidio.automation.converter.conveters;

import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import fortscale.common.general.Schema;
import presidio.data.domain.event.process.ProcessEvent;
import com.rsa.netwitness.presidio.automation.common.helpers.NamesConversionUtils;

import java.util.List;


class NetwitnessProcessEventBuilder extends NetwitnessEvent {
    private final ProcessEvent event;

    NetwitnessProcessEventBuilder(ProcessEvent event) {
        cefVendor =  "RSA";
        cefProduct = "endpoint";
        cefEventType = "Process event";
        cefEventDesc = "process event";
        schema = Schema.PROCESS;
        eventTimeEpoch = event.getDateTime();

        event_time = event.getDateTime().toString().replace("T"," ").replace("Z","");
        category= "Process Event";
        device_type = "nwendpoint";
        event_source_id = event.getEventId();
        this.event = event;
    }

    NetwitnessProcessEventBuilder getProcessEvent() {
        user_src= event.getUser().getUserId();
        action = NamesConversionUtils.revertProcessOperationType(event.getProcessOperation().getOperationType().getName());
        alias_host = event.getMachineEntity().getMachineId();
        directory_src = event.getProcessOperation().getSourceProcess().getProcessDirectory().replace("\\","\\\\");
        filename_src = event.getProcessOperation().getSourceProcess().getProcessFileName();
        cert_common = event.getProcessOperation().getSourceProcess().getProcessCertificateIssuer();
        directory_dst = event.getProcessOperation().getDestinationProcess().getProcessDirectory().replace("\\","\\\\");
        filename_dst = event.getProcessOperation().getDestinationProcess().getProcessFileName();
        nwe_callback_id = "nwe://64edb0e7-eda6-496f-8889-6c239b32bb5b";
        checksum_src = "1fa5a6c8438a4e6d373d39c96b77c0c84540d38b80628effdec89e77d02d7e57";
        checksum_dst = "1fa5a6c8438a4e6d373d39c96b77c0c84540d38b80628effdec89e77d02d7e57";
        process_vid_src= "-363757471601907552";
        agent_id = "807E600E-FBBB-E21B-FB17-7E44BFB6186B";
        os = event.getMachineEntity().getOsVersion();
        owner= event.getMachineEntity().getOwner();

        dir_path_src = getTopSrcDirGroups(0);
        dir_path_src$1 = getTopSrcDirGroups(1);
        dir_path_src$2 = getTopSrcDirGroups(2);

        dir_path_dst = getTopDestDirGroups(0);
        dir_path_dst$1 = getTopDestDirGroups(1);
        dir_path_dst$2 = getTopDestDirGroups(2);
        return this;
    }

    private String getTopSrcDirGroups(int index) {
        List<String> dirGrps = NamesConversionUtils.revertDirGroups(event.getProcessOperation().getSourceProcess().getProcessDirectoryGroups());
        if (dirGrps != null && !dirGrps.isEmpty()  && index <  dirGrps.size()) return dirGrps.get(index);
        else return null;
    }

    private String getTopDestDirGroups(int index) {
        List<String> dirGrps = NamesConversionUtils.revertDirGroups(event.getProcessOperation().getDestinationProcess().getProcessDirectoryGroups());
        if (dirGrps != null && !dirGrps.isEmpty() && index <  dirGrps.size()) return dirGrps.get(index);
        else return null;
    }
}
