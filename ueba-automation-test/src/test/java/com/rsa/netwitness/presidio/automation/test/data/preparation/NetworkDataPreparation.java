package com.rsa.netwitness.presidio.automation.test.data.preparation;

import com.rsa.netwitness.presidio.automation.common.scenarios.tls.*;
import com.rsa.netwitness.presidio.automation.data.tls.TlsAlerts;
import com.rsa.netwitness.presidio.automation.data.tls.model.TlsIndicator;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.network.NetworkEvent;
import presidio.data.generators.common.GeneratorException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class NetworkDataPreparation extends DataPreparationBase {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(NetworkDataPreparation.class.getName());

    @Override
    public List<? extends Event> generate() throws GeneratorException {
        // adapterTestManager.clearAllCollections();

        UncommonValuesAlerts uncommonValuesAlerts = new UncommonValuesAlerts(historicalDaysBack, anomalyDay);
        UnusualTrafficVolumeAlerts unusualTrafficVolumeAlerts = new UnusualTrafficVolumeAlerts(historicalDaysBack, anomalyDay);
        SessionSplitEnrichmentData sessionSplitEnrichmentData = new SessionSplitEnrichmentData();
        FutureEventsForMetrics futureEventsGen = new FutureEventsForMetrics(10);

        TlsAlerts data = new TlsAlerts(historicalDaysBack,anomalyDay);

        Stream<NetworkEvent> resultingStream = Stream.of(
                sessionSplitEnrichmentData.generateAll(),
                futureEventsGen.get(),

                uncommonValuesAlerts.uncommonJa3StartInstantCountryForSrcNetnameSslSubj(11),
                uncommonValuesAlerts.uncommonJa3StartInstantCountryForSrcNetnameSslSubj(12),
                uncommonValuesAlerts.uncommonJa3StartInstantCountryForSrcNetnameSslSubj(13),
                uncommonValuesAlerts.uncommonJa3StartInstantCountryForSrcNetnameSslSubj(14),
                uncommonValuesAlerts.uncommonDomainDestOrganisationSslSubjectForJa3SrcNetname(21),
                uncommonValuesAlerts.uncommonDomainDestOrganisationSslSubjectForJa3SrcNetname(22),
                uncommonValuesAlerts.uncommonDomainDestOrganisationSslSubjectForJa3SrcNetname(23),
                uncommonValuesAlerts.uncommonDomainDestOrganisationSslSubjectForJa3SrcNetname(24),
                uncommonValuesAlerts.uncommonDestPortForSslSubjectJa3SrcNetnameDestOrgDomain(31),
                uncommonValuesAlerts.uncommonDestPortForSslSubjectJa3SrcNetnameDestOrgDomain(32),
                uncommonValuesAlerts.uncommonDestPortForSslSubjectJa3SrcNetnameDestOrgDomain(33),
                uncommonValuesAlerts.uncommonDestPortForSslSubjectJa3SrcNetnameDestOrgDomain(34),

                unusualTrafficVolumeAlerts.fromSourceIpToSslSubjectDomainOrganisationDestPort(),
                unusualTrafficVolumeAlerts.toSslSubjectDomainOrganisationDestPortJa3()

        ).flatMap(i -> i);

        return resultingStream.collect(Collectors.toList());
    }

    @Test
    public void dataReachedTheDestination() {
        generatorResultCount.forEach((schema, count) ->
                Assert.assertTrue(count > 0, "No events were sent from " + schema));
    }

}
