package com.rsa.netwitness.presidio.automation.common.scenarios.tls;

import presidio.data.domain.event.network.NetworkEvent;
import presidio.data.generators.IBaseGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.random.GaussianLongGenerator;
import presidio.data.generators.event.network.NetworkEventsGenerator;

import java.util.List;
import java.util.stream.Stream;

public class UnusualTrafficVolumeAlerts extends NetworkScenarioBase {

    private final IBaseGenerator<Long> regularTrafficGenerator =
            new GaussianLongGenerator(1e9, 10e6);

    private final IBaseGenerator<Long> unusualTrafficGenerator =
            new GaussianLongGenerator(1.5e9, 10e6);


    public UnusualTrafficVolumeAlerts(int dataPeriod, int uncommonStartDay) {
        this.daysBackFrom = dataPeriod;
        this.daysBackFromAnomaly = uncommonStartDay;
    }


    @Override
    String getScenarioName() {
        return "unusual_traffic";
    }

    // unusual from SourceIp To SslSubject Domain Organisation DestPort
    public Stream<NetworkEvent> fromSourceIpToSslSubjectDomainOrganisationDestPort() throws GeneratorException {
        NetworkEventsGenerator regularGen = new NetworkEventsGenerator(getDefaultRegularTimeGen());
        regularGen.setNumOfBytesSentGenerator(regularTrafficGenerator);
        List<NetworkEvent> events = regularGen
                .modify()
                .setSSLSubjectEntityValue(SSLSubjEntity(0))
                .fixSourceIp()
                .fixLocation()
                .fixDestinationOrganization()
                .fixDstPort()
                .generate();

        NetworkEventsGenerator uncommonGen = new NetworkEventsGenerator(getDefaultUncommonTimeGen());
        uncommonGen.setNumOfBytesSentGenerator(unusualTrafficGenerator);
        uncommonGen
                .modify()
                .setJa3EntityValue(Ja3Entity(0))
                .setSSLSubjectEntityValue(SSLSubjEntity(0))
                .fixSourceIp()
                .fixLocation()
                .fixDestinationOrganization()
                .fixDstPort()
                .generateAndAppendTo(events);

        return events.stream();
    }


    // unusual To SslSubject Domain Organisation DestPort ja3
    public Stream<NetworkEvent> toSslSubjectDomainOrganisationDestPortJa3() throws GeneratorException {
        NetworkEventsGenerator regularGen = new NetworkEventsGenerator(getDefaultRegularTimeGen());
        regularGen.setNumOfBytesSentGenerator(regularTrafficGenerator);
        List<NetworkEvent> events = regularGen
                .modify()
                .setJa3EntityValue(Ja3Entity(2))
                .setSSLSubjectEntityValue(SSLSubjEntity(2))
                .fixLocation()
                .fixDestinationOrganization()
                .fixDstPort()
                .fixFqdn()
                .generate();

        NetworkEventsGenerator uncommonGen = new NetworkEventsGenerator(getDefaultUncommonTimeGen());
        uncommonGen.setNumOfBytesSentGenerator(unusualTrafficGenerator);
        uncommonGen
                .modify()
                .setJa3EntityValue(Ja3Entity(2))
                .setSSLSubjectEntityValue(SSLSubjEntity(2))
                .fixLocation()
                .fixDestinationOrganization()
                .fixDstPort()
                .fixFqdn()
                .generateAndAppendTo(events);

        return events.stream();
    }

}
