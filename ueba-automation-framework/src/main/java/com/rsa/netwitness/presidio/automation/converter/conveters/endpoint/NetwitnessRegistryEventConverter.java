package com.rsa.netwitness.presidio.automation.converter.conveters.endpoint;

import com.rsa.netwitness.presidio.automation.converter.conveters.INetwitnessEventConverter;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import presidio.data.domain.event.registry.RegistryEvent;

public class NetwitnessRegistryEventConverter implements INetwitnessEventConverter<RegistryEvent> {

    @Override
    public NetwitnessEvent toNetwitnessEvent(RegistryEvent event) {
        return new NetwitnessRegistryEventBuilder(event).getRegistryEvent();
    }

}
