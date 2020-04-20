package com.rsa.netwitness.presidio.automation.converter.conveters;

import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import presidio.data.domain.event.Event;

public interface EventConverter<T extends Event> {
    NetwitnessEvent convert(T event);
}
