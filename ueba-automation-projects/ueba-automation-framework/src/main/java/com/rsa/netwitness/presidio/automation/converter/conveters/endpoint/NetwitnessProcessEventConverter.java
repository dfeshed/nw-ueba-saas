package com.rsa.netwitness.presidio.automation.converter.conveters.endpoint;

import com.rsa.netwitness.presidio.automation.converter.conveters.EventConverter;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import presidio.data.domain.event.process.ProcessEvent;

public class NetwitnessProcessEventConverter implements EventConverter<ProcessEvent> {

    @Override
    public NetwitnessEvent convert(ProcessEvent event) {
        return new NetwitnessProcessEventBuilder(event).getProcessEvent();
    }

}
