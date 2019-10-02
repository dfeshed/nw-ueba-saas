package com.rsa.netwitness.presidio.automation.data.tls.events;

import com.rsa.netwitness.presidio.automation.data.tls.feilds.TlsEventsGen;
import com.rsa.netwitness.presidio.automation.data.tls.model.EventsGenerator;
import presidio.data.domain.event.network.NetworkEvent;

import java.util.List;
import java.util.stream.Stream;

public class EntityHistoricalDataSupplier extends IndicatorGen implements EventsGenerator {
    TlsEventsGen gen;


    public EntityHistoricalDataSupplier(TlsEventsGen gen) {
        this.gen = gen;
    }

    @Override
    public Stream<NetworkEvent> generate() {
        gen.setTimeGenerator(getEntityHistoricalDataTimeGen());
        List<NetworkEvent> events = generate.apply(gen);
        return events.stream();
    }
}
