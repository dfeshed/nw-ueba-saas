package com.rsa.netwitness.presidio.automation.converter.conveters;

import com.rsa.netwitness.presidio.automation.converter.events.ConverterEventBase;
import presidio.data.domain.event.Event;

public interface PresidioEventConverter {
    ConverterEventBase convert(Event event);
}
