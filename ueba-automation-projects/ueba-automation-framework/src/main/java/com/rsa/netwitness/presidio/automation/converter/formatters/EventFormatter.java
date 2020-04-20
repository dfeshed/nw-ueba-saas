package com.rsa.netwitness.presidio.automation.converter.formatters;

@FunctionalInterface
public interface EventFormatter<T,U> {
    U format(T event);
}
