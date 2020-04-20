package com.rsa.netwitness.presidio.automation.converter.producers.stream_converters;

import java.util.List;

@FunctionalInterface
public interface ProducerStreamConverter<T, U> {
    T convert(List<U> lines);
}
