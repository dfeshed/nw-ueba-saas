package com.rsa.netwitness.presidio.automation.data.tls.model;

import presidio.data.domain.event.network.NetworkEvent;

import java.util.stream.Stream;

public interface EventsGenerator {
    Stream<NetworkEvent> generate();
}
