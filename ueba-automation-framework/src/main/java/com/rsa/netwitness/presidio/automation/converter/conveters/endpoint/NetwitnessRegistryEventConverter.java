package com.rsa.netwitness.presidio.automation.converter.conveters.endpoint;

import com.rsa.netwitness.presidio.automation.converter.conveters.EventConverter;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import presidio.data.domain.event.registry.RegistryEvent;

public class NetwitnessRegistryEventConverter implements EventConverter<RegistryEvent> {

    @Override
    public NetwitnessEvent convert(RegistryEvent event) {
        return new NetwitnessRegistryEventBuilder(event).getRegistryEvent();
    }

}
