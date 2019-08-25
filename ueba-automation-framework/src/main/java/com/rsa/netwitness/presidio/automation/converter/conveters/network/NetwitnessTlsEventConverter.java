package com.rsa.netwitness.presidio.automation.converter.conveters.network;

import com.rsa.netwitness.presidio.automation.converter.conveters.INetwitnessEventConverter;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import presidio.data.domain.event.network.NetworkEvent;

public class NetwitnessTlsEventConverter implements INetwitnessEventConverter<NetworkEvent> {

    @Override
    public NetwitnessEvent toNetwitnessEvent(NetworkEvent event) {
        return new NetwitnessTlsEventBuilder(event).getAsNetwitnessEvent();
    }

}
