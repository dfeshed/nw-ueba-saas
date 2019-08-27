package com.rsa.netwitness.presidio.automation.converter.formatters;

import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;

public interface EventFormatter<T> {

    T format(NetwitnessEvent event);
}
