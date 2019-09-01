package com.rsa.netwitness.presidio.automation.converter.conveters.endpoint;

import com.rsa.netwitness.presidio.automation.common.helpers.NamesConversionUtils;
import com.rsa.netwitness.presidio.automation.converter.events.CefHeader;
import com.rsa.netwitness.presidio.automation.converter.events.EndpointEvent;
import fortscale.common.general.Schema;
import presidio.data.domain.event.process.ProcessEvent;

import static com.rsa.netwitness.presidio.automation.common.helpers.NamesConversionUtils.revertCategories;
import static com.rsa.netwitness.presidio.automation.common.helpers.NamesConversionUtils.revertDirGroups;
import static org.assertj.core.util.Lists.list;


class NetwitnessProcessEventBuilder extends EndpointEvent {
    private final ProcessEvent event;


    NetwitnessProcessEventBuilder(ProcessEvent event) {
        super(event.getDateTime(), Schema.PROCESS);
        this.event = event;
        cefHeader = getCefHeader();
    }

    private CefHeader getCefHeader() {
        String cefVendor = "RSA";
        String cefProduct = "endpoint";
        String eventType = "Process Event";
        String eventDesc = "process event";
        return new CefHeader(cefVendor, cefProduct, eventType, eventDesc);
    }

    NetwitnessProcessEventBuilder getProcessEvent() {
        event_source_id = event.getEventId();
        device_type = "Netwitness Endpoint";
        user_src= event.getUser().getUserId();
        action = list(NamesConversionUtils.revertProcessOperationType(event.getProcessOperation().getOperationType().getName()));
        alias_host = list(event.getMachineEntity().getMachineId());
        owner= event.getMachineEntity().getOwner();
        directory_src = event.getProcessOperation().getSourceProcess().getProcessDirectory();
        filename_src = event.getProcessOperation().getSourceProcess().getProcessFileName();
        dir_path_src = revertDirGroups(event.getProcessOperation().getSourceProcess().getProcessDirectoryGroups());
        dir_path_dst = revertDirGroups(event.getProcessOperation().getDestinationProcess().getProcessDirectoryGroups());
        file_cat_src = revertCategories(event.getProcessOperation().getSourceProcess().getProcessCategories());
        file_cat_dst = revertCategories(event.getProcessOperation().getDestinationProcess().getProcessCategories());
        cert_common = event.getProcessOperation().getSourceProcess().getProcessCertificateIssuer();
        directory_dst = event.getProcessOperation().getDestinationProcess().getProcessDirectory();
        filename_dst = event.getProcessOperation().getDestinationProcess().getProcessFileName();

        /** optional */
        checksum$dash$id =  "c01b39c7a35ccc3b081a3e83d2c71fa9a767ebfeb45c69f08e17dfe3ef375a7b";
        agent$dash$id =  "agent_001";
        os$dash$type = event.getMachineEntity().getOsVersion();
        process$dash$vid$dash$src = event.getEventId().replace("EV","pr");
        nwe_callback_id = "nwe://64edb0e7-eda6-496f-8889-6c239b32bb5b";
        checksum_src = "1fa5a6c8438a4e6d373d39c96b77c0c84540d38b80628effdec89e77d02d7e57";
        checksum_dst = "1fa5a6c8438a4e6d373d39c96b77c0c84540d38b80628effdec89e77d02d7e57";
        process_vid_src= "-363757471601907552";
        agent_id = "807E600E-FBBB-E21B-FB17-7E44BFB6186B";
        os = event.getMachineEntity().getOsVersion();

        return this;
    }

}
