package com.rsa.netwitness.presidio.automation.converter.conveters.endpoint;

import com.rsa.netwitness.presidio.automation.common.helpers.NamesConversionUtils;
import com.rsa.netwitness.presidio.automation.converter.events.EndpointEvent;
import edu.emory.mathcs.backport.java.util.Arrays;
import fortscale.common.general.Schema;
import presidio.data.domain.event.registry.RegistryEvent;

import java.util.List;


class NetwitnessRegistryEventBuilder extends EndpointEvent {
    private final RegistryEvent event;

    NetwitnessRegistryEventBuilder(RegistryEvent event) {
        cefVendor =  "RSA";
        cefProduct = "endpoint";
        cefEventType = "Registry event";
        cefEventDesc = "registry event";
        schema = Schema.REGISTRY;
        eventTimeEpoch = event.getDateTime();

        event_time = eventTimeFormatter.format(event.getDateTime());
        category= "Registry Event";
        device_type = "nwendpoint";
        event_source_id = event.getEventId();
        this.event = event;
    }

    NetwitnessRegistryEventBuilder getRegistryEvent() {
        user_src= event.getUser().getUserId();
        action =  NamesConversionUtils.revertProcessOperationType(NamesConversionUtils.fixRegistryOperation(event.getRegistryOperation().getOperationType().getName()));
        alias_host = event.getMachineEntity().getMachineId();
        owner= event.getMachineEntity().getOwner();
        directory_src = event.getRegistryOperation().getProcess().getProcessDirectory().replace("\\","\\\\");
        filename_src = event.getRegistryOperation().getProcess().getProcessFileName();
        dir_path_src = "WINDOWS_SYSTEM32";
        dir_path_src$1 = "WINDOWS";
        file_cat = getTopFileCategories(0);
        file_cat$1 = getTopFileCategories(1);
        file_cat$2 = getTopFileCategories(2);

        cert_common = event.getRegistryOperation().getProcess().getProcessCertificateIssuer();
        ec_subject = NamesConversionUtils.revert2LowerCamel(event.getRegistryOperation().getRegistryEntry().getKeyGroup());
        registry_key = (event.getRegistryOperation().getRegistryEntry().getKey() + event.getRegistryOperation().getRegistryEntry().getValueName()).replace("\\","\\\\");
        nwe_callback_id = "nwe://64edb0e7-eda6-496f-8889-6c239b32bb5b";
        checksum_src = "1fa5a6c8438a4e6d373d39c96b77c0c84540d38b80628effdec89e77d02d7e57";
        checksum_dst = "1fa5a6c8438a4e6d373d39c96b77c0c84540d38b80628effdec89e77d02d7e57";

        process_vid_src= "-363757471601907552";
        process_vid_dst= "8014783080838410532";
        agent_id = "807E600E-FBBB-E21B-FB17-7E44BFB6186B";
        os = event.getMachineEntity().getOsVersion();

        return this;
    }

    private String getTopFileCategories(int index) {
        List<String> processCategories = NamesConversionUtils.revertCategories(event.getRegistryOperation().getProcess().getProcessCategories());

        if (processCategories != null && !processCategories.isEmpty() && index <  processCategories.size())
            return processCategories.get(index);
        else if (index == 0)
            return NamesConversionUtils.revertCategories(Arrays.asList(new String[] {"WINDOWS_PROCESS"})).get(0).toString();
            else return null;
    }

}
