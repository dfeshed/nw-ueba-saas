package com.rsa.netwitness.presidio.automation.converter.producers;

import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import fortscale.common.general.Schema;

import java.util.List;
import java.util.Map;

public interface EventsProducer {
    Map<Schema, Long> send(List<NetwitnessEvent> eventsList);
}
