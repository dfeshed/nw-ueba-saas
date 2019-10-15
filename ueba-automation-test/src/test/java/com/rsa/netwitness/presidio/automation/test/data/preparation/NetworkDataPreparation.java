package com.rsa.netwitness.presidio.automation.test.data.preparation;

import com.rsa.netwitness.presidio.automation.common.scenarios.tls.*;
import com.rsa.netwitness.presidio.automation.data.tls.model.TlsAlert;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.network.TlsEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;


public class NetworkDataPreparation extends DataPreparationBase {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(NetworkDataPreparation.class.getName());

    @Override
    public List<? extends Event> generate() {
        List<TlsEvent> networkEvents = new LinkedList<>();

        SessionSplitEnrichmentData sessionSplitEnrichmentData = new SessionSplitEnrichmentData();

        /** TLS alerts generators **/
        List<TlsAlert> tlsAlerts = Stream.of(
                new Ja3UncommonAlerts(historicalDaysBack,anomalyDay).get(),
                new Ja3HighBytesSentAlerts(historicalDaysBack,anomalyDay).get(),
                new SslSubjectUncommonAlerts(historicalDaysBack,anomalyDay).get(),
                new SslSubjectHighBytesSentAlerts(historicalDaysBack,anomalyDay).get()

        ).flatMap(e -> e).collect(toList());

        for (TlsAlert alert : tlsAlerts) {
            networkEvents.addAll(alert.getIndicators().stream().flatMap(e -> e.generateEvents().stream()).collect(toList()));
        }

        /** future time events **/
        FutureEventsForMetrics futureEventsGen = new FutureEventsForMetrics(10);
        networkEvents.addAll(futureEventsGen.get().collect(toList()));

        return networkEvents;
    }

    @Test
    public void dataReachedTheDestination() {
        generatorResultCount.forEach((schema, count) ->
                Assert.assertTrue(count > 0, "No events were sent from " + schema));
    }

}
