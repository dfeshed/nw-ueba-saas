package com.rsa.netwitness.presidio.automation.common.scenarios.tls;

import presidio.data.domain.event.network.NetworkEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.event.network.NetworkEventsGenerator;

import java.util.List;
import java.util.stream.Stream;

public class UncommonValuesAlerts extends NetworkScenarioBase{

    public UncommonValuesAlerts(int dataPeriod, int uncommonStartDay) {
        this.daysBackFrom = dataPeriod;
        this.daysBackFromAnomaly = uncommonStartDay;
    }


    @Override
    String getScenarioName() {
        return "uncommon";
    }


    // uncommon destPort for ja3, sslsubj, SrcNetname, DestOrg, Domain
    public Stream<NetworkEvent> uncommonDestPortForSslSubjectJa3SrcNetnameDestOrgDomain() throws GeneratorException {

        NetworkEventsGenerator regularGen = new NetworkEventsGenerator(getDefaultRegularTimeGen());
        List<NetworkEvent> events = regularGen.modify()
                .fixDstPort()
                .setJa3EntityValue(Ja3Entity(0))
                .setSSLSubjectEntityValue(SSLSubjEntity(0))
                .fixSourceNetname()
                .fixDestinationOrganization()
                .fixFqdn()
                .generate();

        NetworkEventsGenerator uncommonGen = new NetworkEventsGenerator(getDefaultUncommonTimeGen());
        uncommonGen.modify()
                .nextDstPort()
                .setJa3EntityValue(Ja3Entity(0))
                .setSSLSubjectEntityValue(SSLSubjEntity(0))
                .fixSourceNetname()
                .fixDestinationOrganization()
                .fixFqdn()
                .generateAndAppendTo(events);

        return events.stream();
    }


    // uncommon domain sslSubj DestOrganisation, startInstant for ja3, srcNetname,
    public Stream<NetworkEvent> uncommonDomainDestOrganisationSslSubjectForJa3SrcNetname() throws GeneratorException {

        NetworkEventsGenerator regularGen = new NetworkEventsGenerator(getDefaultRegularTimeGen());
        List<NetworkEvent> events = regularGen.modify()
                .fixFqdn()
                .setSSLSubjectEntityValue(SSLSubjEntity(111))
                .fixDestinationOrganization()
                .setJa3EntityValue(Ja3Entity(1))
                .fixSourceNetname()
                .setDistinctSrcIps(5,7)
                .generate();

        NetworkEventsGenerator sslSubjGen = new NetworkEventsGenerator(getDefaultRegularTimeGen());
        sslSubjGen.modify()
                .setSSLSubjectEntityValue(SSLSubjEntity(1))
                .generateAndAppendTo(events);

        NetworkEventsGenerator uncommonGen = new NetworkEventsGenerator(getDefaultUnregularHoursTimeGen());
        uncommonGen.modify()
                .nextFqdn()
                .setSSLSubjectEntityValue(SSLSubjEntity(1))
                .nextDestinationOrganization()
                .setJa3EntityValue(Ja3Entity(1))
                .fixSourceNetname()
                .setDistinctSrcIps(10,50)
                .generateAndAppendTo(events);

        return events.stream();
    }


    // uncommon ja3 startInstant country for srcNetname sslSubj
    public Stream<NetworkEvent>  uncommonJa3StartInstantCountryForSrcNetnameSslSubj() throws GeneratorException {

        NetworkEventsGenerator regularGen = new NetworkEventsGenerator(getDefaultRegularTimeGen());
        List<NetworkEvent> events = regularGen.modify()
                .setJa3EntityValue(Ja3Entity(333))
                .fixLocation()
                .setSSLSubjectEntityValue(SSLSubjEntity(3))
                .fixSourceNetname()
                .generate();

        NetworkEventsGenerator ja3Gen = new NetworkEventsGenerator(getDefaultRegularTimeGen());
        ja3Gen.modify()
                .setJa3EntityValue(Ja3Entity(3))
                .generateAndAppendTo(events);

        NetworkEventsGenerator uncommonGen = new NetworkEventsGenerator(getDefaultUnregularHoursTimeGen());
        uncommonGen.modify()
                .setJa3EntityValue(Ja3Entity(3))
                .nextLocation()
                .setSSLSubjectEntityValue(SSLSubjEntity(3))
                .fixSourceNetname()
                .generateAndAppendTo(events);
        return events.stream();
    }

}
