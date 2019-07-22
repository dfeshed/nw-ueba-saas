package com.rsa.netwitness.presidio.automation.converter.producers;

import fortscale.common.general.Schema;
import com.rsa.netwitness.presidio.automation.converter.events.ConverterEventBase;

import java.util.List;
import java.util.Map;

public interface NetwitnessEventsProducer {
    Map<Schema, Long> send(List<ConverterEventBase> eventsList);
}
