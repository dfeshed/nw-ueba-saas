package com.rsa.netwitness.presidio.automation.common.scenarios.tls;

import presidio.data.domain.event.network.TlsEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.event.tls.TlsRangeEventsGen;

import java.util.List;
import java.util.stream.Stream;

public class FutureEventsForMetrics extends NetworkScenarioBase{

    public FutureEventsForMetrics(int daysForward) {
        this.daysBackFrom = 0;
        this.daysBackTo = -daysForward-1;
    }


    @Override
    String getScenarioName() {
        return "future_metrics";
    }


    public Stream<TlsEvent> get() throws GeneratorException {
        TlsRangeEventsGen tlsRangeEventsGen = new TlsRangeEventsGen(3);
        tlsRangeEventsGen.setTimeGenerator(getDefaultRegularTimeGen());

        List<TlsEvent> events = tlsRangeEventsGen.generate();
        return events.stream();
    }

}
