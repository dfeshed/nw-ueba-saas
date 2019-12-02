package com.rsa.netwitness.presidio.automation.test.data.preparation;

import ch.qos.logback.classic.Logger;
import com.rsa.netwitness.presidio.automation.data.preparation.tls.model.TlsAlert;
import com.rsa.netwitness.presidio.automation.data.preparation.tls.scenarios.*;
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
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(NetworkDataPreparation.class);

    @Override
    public List<? extends Event> generate() {
        List<TlsEvent> networkEvents = new LinkedList<>();

        /** TLS alerts generators **/
        List<TlsAlert> tlsAlerts = Stream.of(
                new Ja3UncommonAlerts(historicalDaysBack, anomalyDay).get(),
                new Ja3HighBytesSentAlerts(historicalDaysBack, anomalyDay).get(),
                new SslSubjectUncommonAlerts(historicalDaysBack, anomalyDay).get(),
                new SslSubjectHighBytesSentAlerts(historicalDaysBack, anomalyDay).get()

        ).flatMap(e -> e).collect(toList());

        for (TlsAlert alert : tlsAlerts) {
            networkEvents.addAll(alert.getIndicators().stream().flatMap(e -> e.generateEvents().stream()).collect(toList()));
        }

        /** future time events **/
        networkEvents.addAll(new FutureEventsForMetrics(10).get().collect(toList()));
        /** Session split data **/
        networkEvents.addAll(new SessionSplitEnrichmentData().generateAll().collect(toList()));
        /** Special cases alerts scenarios **/
        networkEvents.addAll(new AlertsSpecialCases(historicalDaysBack, anomalyDay).generateAll().collect(toList()));

        return networkEvents;
    }

    @Test
    public void dataReachedTheDestination() {
        generatorResultCount.forEach((schema, count) ->
                Assert.assertTrue(count > 0, "No events were sent from " + schema));
    }

}
