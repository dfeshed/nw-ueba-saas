package com.rsa.netwitness.presidio.automation.utils.adapter.log_player.events;

import fortscale.common.general.Schema;

import java.util.Map;

public interface ConverterEventBase {

    Schema mongoSchema();

    default NetwitnessEvent getAsNetwitnessEvent() {
        throw new RuntimeException("Missing implementation for NetwitnessEvent");
    }

    default Map<String, Object> getAsMongoKeyValue() {
        throw new RuntimeException("Missing implementation");
    }
}
