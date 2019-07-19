package com.rsa.netwitness.presidio.automation.utils.adapter.log_player.events;

import fortscale.common.general.Schema;

import java.util.Map;

public class MongoKeyValueEvent implements ConverterEventBase {
    private Map<String, Object> event;
    private Schema schema;

    public MongoKeyValueEvent(Map<String, Object> event, Schema schema) {
        this.event = event;
        this.schema = schema;
    }

    @Override
    public Map<String, Object> getAsMongoKeyValue() {
        return event;
    }

    @Override
    public Schema mongoSchema() {
        return schema;
    }
}
