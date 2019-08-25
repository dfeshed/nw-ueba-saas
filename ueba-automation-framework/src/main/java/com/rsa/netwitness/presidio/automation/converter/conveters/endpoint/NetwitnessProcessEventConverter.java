package com.rsa.netwitness.presidio.automation.converter.conveters.endpoint;

import com.rsa.netwitness.presidio.automation.converter.conveters.INetwitnessEventConverter;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import presidio.data.domain.event.process.ProcessEvent;

public class NetwitnessProcessEventConverter implements INetwitnessEventConverter<ProcessEvent> {

    @Override
    public NetwitnessEvent toNetwitnessEvent(ProcessEvent event) {
        return new NetwitnessProcessEventBuilder(event).getProcessEvent();
    }

}
