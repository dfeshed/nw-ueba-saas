package com.rsa.netwitness.presidio.automation.converter.conveters.mongo;

import presidio.data.domain.event.Event;

import java.util.Map;

public interface EventToMetadataConverter<T extends Event> {
    Map<String, Object> convert(T event);
}
