package com.rsa.netwitness.presidio.automation.converter.producers;

import fortscale.common.general.Schema;

import java.util.Map;
import java.util.stream.Stream;

public interface EventsProducer<T> {
    Map<Schema, Long> send(Stream<T> eventsList);
}
