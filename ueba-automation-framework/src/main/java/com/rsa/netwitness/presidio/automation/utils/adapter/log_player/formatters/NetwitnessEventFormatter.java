package com.rsa.netwitness.presidio.automation.utils.adapter.log_player.formatters;

import com.rsa.netwitness.presidio.automation.utils.adapter.log_player.events.ConverterEventBase;

public interface NetwitnessEventFormatter<T> {

    T format(ConverterEventBase event);
}
