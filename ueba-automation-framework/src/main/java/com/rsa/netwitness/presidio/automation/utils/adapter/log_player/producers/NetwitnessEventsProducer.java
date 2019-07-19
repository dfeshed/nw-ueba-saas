package com.rsa.netwitness.presidio.automation.utils.adapter.log_player.producers;

import fortscale.common.general.Schema;
import com.rsa.netwitness.presidio.automation.utils.adapter.log_player.events.ConverterEventBase;

import java.util.List;
import java.util.Map;

public interface NetwitnessEventsProducer {
    Map<Schema, Long> send(List<ConverterEventBase> eventsList);
}
