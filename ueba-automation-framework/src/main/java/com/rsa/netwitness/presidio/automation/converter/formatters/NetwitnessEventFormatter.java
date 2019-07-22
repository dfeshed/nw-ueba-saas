package com.rsa.netwitness.presidio.automation.converter.formatters;

import com.rsa.netwitness.presidio.automation.converter.events.ConverterEventBase;

public interface NetwitnessEventFormatter<T> {

    T format(ConverterEventBase event);
}
