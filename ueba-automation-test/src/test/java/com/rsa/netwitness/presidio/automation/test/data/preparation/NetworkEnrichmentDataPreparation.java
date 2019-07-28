package com.rsa.netwitness.presidio.automation.test.data.preparation;

import com.rsa.netwitness.presidio.automation.common.scenarios.tls.EnrichmentForSplittedEvents;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.network.NetworkEvent;
import presidio.data.generators.common.GeneratorException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class NetworkEnrichmentDataPreparation extends DataPreparationBase {
    private static  ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(NetworkEnrichmentDataPreparation.class.getName());

    @Override
    public List<? extends Event> generate() throws GeneratorException {
        EnrichmentForSplittedEvents simpleTest = new EnrichmentForSplittedEvents(historicalDaysBack, anomalyDay);

        Stream<NetworkEvent> resultingStream = Stream.of(
                simpleTest.simpleEnreachment()
        ).flatMap(i->i);

        return resultingStream.collect(Collectors.toList());
    }

    @Test
    public void dataReachedTheDestination() {
        generatorResultCount.forEach((schema, count) ->
                Assert.assertTrue(count > 0, "No events were sent from " + schema));
    }

}
