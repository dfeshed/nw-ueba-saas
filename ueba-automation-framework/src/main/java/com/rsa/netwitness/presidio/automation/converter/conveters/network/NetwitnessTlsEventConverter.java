package com.rsa.netwitness.presidio.automation.converter.conveters.network;

import com.rsa.netwitness.presidio.automation.converter.conveters.EventConverter;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import presidio.data.domain.event.network.NetworkEvent;

public class NetwitnessTlsEventConverter implements EventConverter<NetworkEvent> {

    @Override
    public NetwitnessEvent convert(NetworkEvent event) {
        return new NetwitnessTlsEventBuilder(event).getTls();
    }

}
