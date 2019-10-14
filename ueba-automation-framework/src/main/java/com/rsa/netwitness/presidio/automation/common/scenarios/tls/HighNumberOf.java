package com.rsa.netwitness.presidio.automation.common.scenarios.tls;

import presidio.data.domain.event.network.TlsEvent;
import presidio.data.generators.IBaseGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.random.GaussianLongGenerator;
import presidio.data.generators.event.network.NetworkEventsGenerator;

import java.util.List;
import java.util.stream.Stream;

public class HighNumberOf extends NetworkScenarioBase {

    private final IBaseGenerator<Long> regularTrafficGenerator =
            new GaussianLongGenerator(1e9, 10e6);

    private final IBaseGenerator<Long> unusualTrafficGenerator =
            new GaussianLongGenerator(1.5e9, 10e6);


    public HighNumberOf(int dataPeriod, int uncommonStartDay) {
        this.daysBackFrom = dataPeriod;
        this.daysBackFromAnomaly = uncommonStartDay;
    }

    @Override
    String getScenarioName() {
        return "high_number_of";
    }

    public Stream<TlsEvent> distinctSourceIpForJA3() throws GeneratorException {

        NetworkEventsGenerator regularGen = new NetworkEventsGenerator(getDefaultRegularTimeGen());
        regularGen.setNumOfBytesSentGenerator(regularTrafficGenerator);
        List<TlsEvent> events = regularGen
                .modify()
                .setJa3EntityValue(Ja3Entity(0))
                .setDistinctSrcIps(5,7)
                .generate();

        NetworkEventsGenerator uncommonGen = new NetworkEventsGenerator(getDefaultUncommonTimeGen());
        uncommonGen.setNumOfBytesSentGenerator(unusualTrafficGenerator);
        uncommonGen
                .modify()
                .setJa3EntityValue(Ja3Entity(0))
                .setSSLSubjectEntityValue(SSLSubjEntity(0))
                .setDistinctSrcIps(10,50)
                .generateAndAppendTo(events);

        return events.stream();
    }
}
