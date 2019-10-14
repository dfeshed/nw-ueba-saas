package com.rsa.netwitness.presidio.automation.test.data.preparation;

import com.rsa.netwitness.presidio.automation.common.scenarios.tls.FutureEventsForMetrics;
import com.rsa.netwitness.presidio.automation.common.scenarios.tls.SessionSplitEnrichmentData;
import com.rsa.netwitness.presidio.automation.common.scenarios.tls.UncommonValuesAlerts;
import com.rsa.netwitness.presidio.automation.common.scenarios.tls.UnusualTrafficVolumeAlerts;
import com.rsa.netwitness.presidio.automation.data.tls.TlsAlerts;
import com.rsa.netwitness.presidio.automation.data.tls.model.TlsAlert;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.network.TlsEvent;
import presidio.data.generators.common.GeneratorException;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class NetworkDataPreparation extends DataPreparationBase {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(NetworkDataPreparation.class.getName());

    @Override
    public List<? extends Event> generate() throws GeneratorException {
        // adapterTestManager.clearAllCollections();

        // todo:
        UncommonValuesAlerts uncommonValuesAlerts = new UncommonValuesAlerts(historicalDaysBack, anomalyDay);
        UnusualTrafficVolumeAlerts unusualTrafficVolumeAlerts = new UnusualTrafficVolumeAlerts(historicalDaysBack, anomalyDay);
        SessionSplitEnrichmentData sessionSplitEnrichmentData = new SessionSplitEnrichmentData();

        FutureEventsForMetrics futureEventsGen = new FutureEventsForMetrics(10);
        TlsAlerts tlsAlerts = new TlsAlerts(historicalDaysBack,anomalyDay);

        List<TlsEvent> networkEvents = new LinkedList<>();

        for (TlsAlert alert : tlsAlerts.get()) {
            networkEvents.addAll(alert.getIndicators().stream().flatMap(e -> e.generateEvents().stream()).collect(Collectors.toList()));
        }

        networkEvents.addAll(futureEventsGen.get().collect(Collectors.toList()));
        return networkEvents;
    }

    @Test
    public void dataReachedTheDestination() {
        generatorResultCount.forEach((schema, count) ->
                Assert.assertTrue(count > 0, "No events were sent from " + schema));
    }

}
