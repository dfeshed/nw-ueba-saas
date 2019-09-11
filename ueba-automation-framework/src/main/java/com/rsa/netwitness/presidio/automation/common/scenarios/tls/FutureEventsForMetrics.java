package com.rsa.netwitness.presidio.automation.common.scenarios.tls;

import presidio.data.domain.event.network.NetworkEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.event.network.NetworkEventsGenerator;

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


    public Stream<NetworkEvent> get() throws GeneratorException {
        NetworkEventsGenerator regularGen = new NetworkEventsGenerator(getDefaultRegularTimeGen());
        List<NetworkEvent> events = regularGen.generate();
        return events.stream();
    }

}
