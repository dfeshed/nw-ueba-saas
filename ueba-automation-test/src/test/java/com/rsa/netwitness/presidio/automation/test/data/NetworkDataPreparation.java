package com.rsa.netwitness.presidio.automation.test.data;

import com.rsa.netwitness.presidio.automation.common.scenarios.tls.HighNumberOf;
import com.rsa.netwitness.presidio.automation.common.scenarios.tls.UncommonValuesAlerts;
import com.rsa.netwitness.presidio.automation.common.scenarios.tls.UnusualTrafficVolumeAlerts;
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
    private static  ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(NetworkDataPreparation.class.getName());

    @Override
    public List<? extends Event> generate() throws GeneratorException {
        // adapterTestManager.clearAllCollections();

        UncommonValuesAlerts uncommonValuesAlerts = new UncommonValuesAlerts(historicalDaysBack, anomalyDay);
        UnusualTrafficVolumeAlerts unusualTrafficVolumeAlerts = new UnusualTrafficVolumeAlerts(historicalDaysBack, anomalyDay);
        HighNumberOf highNumberOfGen = new HighNumberOf(historicalDaysBack, anomalyDay);

        Stream<NetworkEvent> resultingStream = Stream.of(
                uncommonValuesAlerts.uncommonJa3StartInstantCountryForSrcNetnameSslSubj(),
                uncommonValuesAlerts.uncommonDomainDestOrganisationSslSubjectForJa3SrcNetname(),
                uncommonValuesAlerts.uncommonDestPortForSslSubjectJa3SrcNetnameDestOrgDomain(),

                unusualTrafficVolumeAlerts.fromSourceIpToSslSubjectDomainOrganisationDestPort(),
                unusualTrafficVolumeAlerts.toSslSubjectDomainOrganisationDestPortJa3(),

                highNumberOfGen.distinctSourceIpForJA3()                            // ?


//                uncommonValuesAlerts.createUncommonCountryForSslSubject(),          // 90
//                uncommonValuesAlerts.createUncommonDestPortForSslSubject(),         // 90
//                uncommonValuesAlerts.createUncommonDomainForJa3(),                  // 90
//                uncommonValuesAlerts.createUncommonDestPortForJa3(),                // 90
//                uncommonValuesAlerts.createUncommonDomainForSrcNetname(),           // 90
//                uncommonValuesAlerts.createUncommonDestPortForSrcNetname(),         // 90
//                uncommonValuesAlerts.createUncommonDestPortForDomain(),             // 90
//                uncommonValuesAlerts.createUncommonDestPortForDestOrg(),            // 90
//                uncommonValuesAlerts.createUncommonSslSubjectForSrcNetname(),       // 90
//                uncommonValuesAlerts.createUncommonDestOrganisationForSrcNetname(), // 90
//                uncommonValuesAlerts.createUncommonJa3ForSrcNetname(),              // 90
//                uncommonValuesAlerts.createUncommonSslSubjForJa3(),                 // 90
//
//                uncommonValuesAlerts.createUncommonStartInstantForSslSubject(),     // 90
//                uncommonValuesAlerts.createUncommonStartInstantForJa3(),            // 90

//                unusualTrafficVolumeAlerts.fromSourceIpToDestPort(),                   // ?
//                unusualTrafficVolumeAlerts.fromSourceIpToDomain(),                     // 100
//                unusualTrafficVolumeAlerts.fromSourceIpToOrganisation(),               // 100
//                unusualTrafficVolumeAlerts.fromSourceIpToSslSubject(),                 // 100
//                unusualTrafficVolumeAlerts.toDestPort(),                               // ?
//                unusualTrafficVolumeAlerts.toDomain(),                                 // 100
//                unusualTrafficVolumeAlerts.toJa3(),                                    // 100
//                unusualTrafficVolumeAlerts.toOrganisation(),                           // 100
//                unusualTrafficVolumeAlerts.toSslSubject(),                             // 100

        ).flatMap(i->i);

        return resultingStream.collect(Collectors.toList());
    }

    @Test
    public void dataReachedTheDestination() {
        generatorResultCount.forEach((schema, count) ->
                Assert.assertTrue(count > 0, "No events were sent from " + schema));
    }

}
