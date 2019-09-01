package com.rsa.netwitness.presidio.automation.converter.conveters.endpoint;

import com.rsa.netwitness.presidio.automation.common.helpers.NamesConversionUtils;
import com.rsa.netwitness.presidio.automation.converter.events.CefHeader;
import com.rsa.netwitness.presidio.automation.converter.events.EndpointEvent;
import fortscale.common.general.Schema;
import presidio.data.domain.event.registry.RegistryEvent;

import java.util.List;
import java.util.function.UnaryOperator;

import static com.rsa.netwitness.presidio.automation.common.helpers.NamesConversionUtils.fixRegistryOperation;
import static com.rsa.netwitness.presidio.automation.common.helpers.NamesConversionUtils.revert2LowerCamel;
import static org.assertj.core.util.Lists.list;


class NetwitnessRegistryEventBuilder extends EndpointEvent {
    private final RegistryEvent event;

    NetwitnessRegistryEventBuilder(RegistryEvent event) {
        super(event.getDateTime(), Schema.REGISTRY);
        this.event = event;
        cefHeader = getCefHeader();
    }

    private CefHeader getCefHeader() {
        String cefVendor = "RSA";
        String cefProduct = "endpoint";
        String eventType = "Registry Event";
        String eventDesc = "registry event";
        return new CefHeader(cefVendor, cefProduct, eventType, eventDesc);
    }

    NetwitnessRegistryEventBuilder getRegistryEvent() {

        event_source_id = event.getEventId();
        device_type = "Netwitness Endpoint";
        user_src= event.getUser().getUserId();
        action = list(revert2LowerCamel(fixRegistryOperation(event.getRegistryOperation().getOperationType().getName())));
        alias_host = list(event.getMachineEntity().getMachineId());
        owner= event.getMachineEntity().getOwner();
        directory_src =  event.getRegistryOperation().getProcess().getProcessDirectory();
        filename_src =  event.getRegistryOperation().getProcess().getProcessFileName();

        dir_path_src = getOrDefault(event.getRegistryOperation().getProcess().getProcessDirectoryGroups(),
                NamesConversionUtils::revertDirGroups, list("WINDOWS_SYSTEM32","WINDOWS"));

        file_cat = getOrDefault(event.getRegistryOperation().getProcess().getProcessCategories(),
                NamesConversionUtils::revertCategories, list("WINDOWS_PROCESS"));

        cert_common = event.getRegistryOperation().getProcess().getProcessCertificateIssuer();
        ec_subject = revert2LowerCamel(event.getRegistryOperation().getRegistryEntry().getKeyGroup());
        registry_key = event.getRegistryOperation().getRegistryEntry().getKey() + event.getRegistryOperation().getRegistryEntry().getValueName();


        /** optional */
        nwe_callback_id = "nwe://64edb0e7-eda6-496f-8889-6c239b32bb5b";
        checksum_src = "1fa5a6c8438a4e6d373d39c96b77c0c84540d38b80628effdec89e77d02d7e57";
        checksum_dst = "1fa5a6c8438a4e6d373d39c96b77c0c84540d38b80628effdec89e77d02d7e57";
        process_vid_src= "-363757471601907552";
        process_vid_dst= "8014783080838410532";
        agent_id = "807E600E-FBBB-E21B-FB17-7E44BFB6186B";

        return this;
    }


    private List<String> getOrDefault(List<String> list, UnaryOperator<List<String>> converter, List<String> defaultList) {
        if(list != null && !list.isEmpty()) {
            return converter.apply(list);
        } else {
            return converter.apply(defaultList);
        }
    }
}
