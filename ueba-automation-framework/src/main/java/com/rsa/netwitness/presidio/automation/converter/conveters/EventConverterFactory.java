package com.rsa.netwitness.presidio.automation.converter.conveters;

import presidio.data.domain.event.Event;

public class EventConverterFactory {

    /** currently, there is only one converter  **/
    public EventConverter<Event> get() {
        return new PresidioEventConverter<>();
    }
}
