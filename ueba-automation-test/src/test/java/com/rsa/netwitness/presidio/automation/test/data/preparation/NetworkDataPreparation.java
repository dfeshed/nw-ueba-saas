package com.rsa.netwitness.presidio.automation.test.data.preparation;

import ch.qos.logback.classic.Logger;
import com.rsa.netwitness.presidio.automation.data.preparation.tls.model.TlsAlert;
import com.rsa.netwitness.presidio.automation.data.preparation.tls.scenarios.*;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.network.TlsEvent;

import java.util.stream.Stream;


public class NetworkDataPreparation extends DataPreparationBase {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(NetworkDataPreparation.class);

    @Override
    public Stream<? extends Event> generate() {
        /** TLS alerts generators **/
        Stream<TlsAlert> tlsAlerts = Stream.of(
                new Ja3UncommonAlerts(historicalDaysBack, anomalyDay).get(),
                new Ja3HighBytesSentAlerts(historicalDaysBack, anomalyDay).get(),
                new SslSubjectUncommonAlerts(historicalDaysBack, anomalyDay).get(),
                new SslSubjectHighBytesSentAlerts(historicalDaysBack, anomalyDay).get()
        ).flatMap(e -> e);


        Stream<TlsEvent> networkEvents = Stream.of(
                /** alerts **/
                tlsAlerts.parallel().map(alert -> alert.getIndicators().stream().map(ind -> ind.generateEvents().stream()).flatMap(e -> e)).flatMap(e -> e),

                /** future time events **/
                new FutureEventsForMetrics(10).get(),
                /** Session split data **/
                new SessionSplitEnrichmentData().generateAll(),
                /** Special cases alerts scenarios **/
                new AlertsSpecialCases(historicalDaysBack, anomalyDay).generateAll()
        ).flatMap(e -> e);


        return networkEvents;
    }

    @Test
    public void dataReachedTheDestination() {
        generatorResultCount.forEach((schema, count) ->
                Assert.assertTrue(count > 0, "No events were sent from " + schema));
    }

}
