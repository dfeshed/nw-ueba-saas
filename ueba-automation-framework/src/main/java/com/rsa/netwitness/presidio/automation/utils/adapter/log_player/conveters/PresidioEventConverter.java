package com.rsa.netwitness.presidio.automation.utils.adapter.log_player.conveters;

import presidio.data.domain.event.Event;
import com.rsa.netwitness.presidio.automation.utils.adapter.log_player.events.ConverterEventBase;

public interface PresidioEventConverter {
    ConverterEventBase convert(Event event);
}
