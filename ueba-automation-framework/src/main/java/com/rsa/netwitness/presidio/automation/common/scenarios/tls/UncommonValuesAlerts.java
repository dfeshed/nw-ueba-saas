package com.rsa.netwitness.presidio.automation.common.scenarios.tls;

import presidio.data.domain.event.network.TlsEvent;
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
    public Stream<TlsEvent> uncommonDestPortForSslSubjectJa3SrcNetnameDestOrgDomain(int index) {

        NetworkEventsGenerator regularGen = new NetworkEventsGenerator(getDefaultRegularTimeGen(), String.valueOf(index));
        List<TlsEvent> events = regularGen.modify()
                .fixDstPort()
                .setJa3EntityValue(Ja3Entity(index))
                .setSSLSubjectEntityValue(SSLSubjEntity(index))
                .fixSourceNetname()
                .fixDestinationOrganization()
                .fixFqdn()
                .generate();

        NetworkEventsGenerator uncommonGen = new NetworkEventsGenerator(getDefaultUncommonTimeGen(), String.valueOf(index));
        uncommonGen.modify()
                .nextDstPort()
                .setJa3EntityValue(Ja3Entity(index))
                .setSSLSubjectEntityValue(SSLSubjEntity(index))
                .fixSourceNetname()
                .fixDestinationOrganization()
                .fixFqdn()
                .generateAndAppendTo(events);

        return events.stream();
    }


    // uncommon domain sslSubj DestOrganisation, startInstant for ja3, srcNetname,
    public Stream<TlsEvent> uncommonDomainDestOrganisationSslSubjectForJa3SrcNetname(int index) {

        NetworkEventsGenerator regularGen = new NetworkEventsGenerator(getDefaultRegularTimeGen(), String.valueOf(index));
        List<TlsEvent> events = regularGen.modify()
                .fixFqdn()
                .setSSLSubjectEntityValue(SSLSubjEntity(index * 100))
                .fixDestinationOrganization()
                .setJa3EntityValue(Ja3Entity(index))
                .fixSourceNetname()
                .setDistinctSrcIps(5,7)
                .generate();

        NetworkEventsGenerator sslSubjGen = new NetworkEventsGenerator(getDefaultRegularTimeGen(), String.valueOf(index));
        sslSubjGen.modify()
                .setSSLSubjectEntityValue(SSLSubjEntity(index))
                .generateAndAppendTo(events);

        NetworkEventsGenerator uncommonGen = new NetworkEventsGenerator(getDefaultUnregularHoursTimeGen(), String.valueOf(index));
        uncommonGen.modify()
                .nextFqdn()
                .setSSLSubjectEntityValue(SSLSubjEntity(index))
                .nextDestinationOrganization()
                .setJa3EntityValue(Ja3Entity(index))
                .fixSourceNetname()
                .setDistinctSrcIps(10,50)
                .generateAndAppendTo(events);

        return events.stream();
    }


    // uncommon ja3 startInstant country for srcNetname sslSubj
    public Stream<TlsEvent>  uncommonJa3StartInstantCountryForSrcNetnameSslSubj(int index) {

        NetworkEventsGenerator regularGen = new NetworkEventsGenerator(getDefaultRegularTimeGen(), String.valueOf(index));
        List<TlsEvent> events = regularGen.modify()
                .setJa3EntityValue(Ja3Entity(index * 100))
                .fixLocation()
                .setSSLSubjectEntityValue(SSLSubjEntity(index))
                .fixSourceNetname()
                .generate();

        NetworkEventsGenerator ja3Gen = new NetworkEventsGenerator(getDefaultRegularTimeGen(), String.valueOf(index));
        ja3Gen.modify()
                .setJa3EntityValue(Ja3Entity(index))
                .generateAndAppendTo(events);

        NetworkEventsGenerator uncommonGen = new NetworkEventsGenerator(getDefaultUncommonTimeGen(), String.valueOf(index));
        uncommonGen.modify()
                .setJa3EntityValue(Ja3Entity(index))
                .nextLocation()
                .setSSLSubjectEntityValue(SSLSubjEntity(index))
                .fixSourceNetname()
                .generateAndAppendTo(events);
        return events.stream();
    }

    public Stream<TlsEvent>  criticalSeverity(int index) {

        NetworkEventsGenerator regularGen = new NetworkEventsGenerator(getDefaultRegularTimeGen(), String.valueOf(index));
        List<TlsEvent> events = regularGen.modify()
                .setJa3EntityValue(Ja3Entity(index * 100))
                .fixDstPort()
                .fixLocation()
                .setSSLSubjectEntityValue(SSLSubjEntity(index))
                .fixSourceNetname()
                .fixDestinationOrganization()
                .fixFqdn()
                .generate();

        NetworkEventsGenerator ja3Gen = new NetworkEventsGenerator(getDefaultRegularTimeGen(), String.valueOf(index));
        ja3Gen.modify()
                .setJa3EntityValue(Ja3Entity(index))
                .generateAndAppendTo(events);

        NetworkEventsGenerator uncommonGen = new NetworkEventsGenerator(getDefaultUnregularHoursTimeGen(), String.valueOf(index));
        uncommonGen.modify()
                .setJa3EntityValue(Ja3Entity(index))
                .nextDstPort()
                .nextLocation()
                .setSSLSubjectEntityValue(SSLSubjEntity(index))
                .fixDestinationOrganization()
                .fixSourceNetname()
                .nextFqdn()
                .generateAndAppendTo(events);
        return events.stream();
    }

}
