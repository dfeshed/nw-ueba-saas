package com.rsa.netwitness.presidio.automation.converter.conveters.network;

import com.rsa.netwitness.presidio.automation.converter.conveters.EventConverter;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import presidio.data.domain.event.network.TlsEvent;

public class NetwitnessTlsEventConverter implements EventConverter<TlsEvent> {

    @Override
    public NetwitnessEvent convert(TlsEvent event) {
        return new NetwitnessTlsEventConverterBuilder(event).getTls();
    }

}
