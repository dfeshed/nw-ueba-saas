package com.rsa.netwitness.presidio.automation.converter.formatters;


import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.toMap;

public class MongoAdapterFormatter implements EventFormatter<NetwitnessEvent, Map<String, Object>> {

    public Map<String, Object> format(NetwitnessEvent converted) {
        Map<String, Object> event = converted.getEvent()
                .entrySet().stream()
                .sorted(comparingByKey())
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        event.put("mongo_source_event_time", converted.mongo_source_event_time);
        event.put("insert_time", Instant.now());
        if (!event.containsKey("time")) event.put("event_time", converted.timeMillis);
        return event;
    }

}