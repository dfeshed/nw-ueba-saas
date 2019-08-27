package com.rsa.netwitness.presidio.automation.converter.producers;

import fortscale.common.general.Schema;

import java.util.Map;

public interface EventsProducer<T> {
    Map<Schema, Long> send(T eventsList);
}
