package com.rsa.netwitness.presidio.automation.converter.conveters;

import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;

public interface INetwitnessEventConverter<T> {

    NetwitnessEvent convert(T event);
}
