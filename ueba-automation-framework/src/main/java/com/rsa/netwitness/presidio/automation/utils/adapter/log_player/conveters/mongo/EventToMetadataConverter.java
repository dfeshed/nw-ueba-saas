package com.rsa.netwitness.presidio.automation.utils.adapter.log_player.conveters.mongo;

import presidio.data.domain.event.Event;

import java.util.List;
import java.util.Map;

public interface EventToMetadataConverter {
    List<Map<String, Object>> convert(Map<String, String> config, List<? extends Event> events);
}
