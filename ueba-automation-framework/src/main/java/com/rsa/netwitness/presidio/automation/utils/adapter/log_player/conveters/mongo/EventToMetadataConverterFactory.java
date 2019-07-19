package com.rsa.netwitness.presidio.automation.utils.adapter.log_player.conveters.mongo;

import fortscale.common.general.Schema;

import java.util.HashMap;
import java.util.Map;

public class EventToMetadataConverterFactory {
    private final Map<Schema, EventToMetadataConverter> schemaToConverterMap = new HashMap<>();

    public EventToMetadataConverterFactory() {
        schemaToConverterMap.put(Schema.ACTIVE_DIRECTORY, new EventToMetadataConverterActiveDirectory());
        schemaToConverterMap.put(Schema.AUTHENTICATION, new EventToMetadataConverterAuthentication());
        schemaToConverterMap.put(Schema.FILE, new EventToMetadataConverterFile());
        schemaToConverterMap.put(Schema.PROCESS, new EventToMetadataConverterProcess());
        schemaToConverterMap.put(Schema.REGISTRY, new EventToMetadataConverterRegistry());
    }

    public EventToMetadataConverter getConverter(Schema schema) {
        return schemaToConverterMap.get(schema);
    }
}
