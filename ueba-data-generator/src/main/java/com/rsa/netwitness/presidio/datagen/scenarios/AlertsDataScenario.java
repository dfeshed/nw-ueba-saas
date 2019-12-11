package com.rsa.netwitness.presidio.datagen.scenarios;

import ch.qos.logback.classic.Logger;
import com.rsa.netwitness.presidio.automation.common.scenarios.alerts.AlertsScenario;
import com.rsa.netwitness.presidio.automation.data.preparation.tls.model.TlsAlert;
import com.rsa.netwitness.presidio.automation.data.preparation.tls.scenarios.*;
import com.rsa.netwitness.presidio.automation.utils.common.Lazy;
import fortscale.common.general.Schema;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.network.TlsEvent;
import presidio.data.generators.common.GeneratorException;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class AlertsDataScenario extends DataPreparationBase {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(AlertsDataScenario.class);

    @Override
    protected List<? extends Event> generate() {
        LOGGER.info("Going to generate events for Alert scenarios");

        return Stream.of(
                getSortedAuthenticationEvents(),
                getSortedActiveDirectoryEvents(),
                getSortedFileEvents(),
                getSortedProcessEvents(),
                getSortedRegistryEvents(),
                getTlsEvents()
        ).flatMap(i -> i).collect(toList());
    }

    @Test
    public void dataReachedTheDestination() {
        generatorResultCount.forEach((schema, count) ->
                Assert.assertTrue(count > 0, "No events were sent from " + schema));
    }


    private Stream<? extends Event> getSortedAuthenticationEvents() {
        if (schemas.contains(Schema.AUTHENTICATION.toString())) {
            return alertsScenario.getOrCompute(supplier).getSortedAuthenticationEvents().stream();
        } else {
            return Stream.empty();
        }
    }

    private Stream<? extends Event> getSortedActiveDirectoryEvents() {
        if (schemas.contains(Schema.ACTIVE_DIRECTORY.toString())) {
            return alertsScenario.getOrCompute(supplier).getSortedActiveDirectoryEvents().stream();
        } else {
            return Stream.empty();
        }
    }

    private Stream<? extends Event> getSortedFileEvents() {
        if (schemas.contains(Schema.FILE.toString())) {
            return alertsScenario.getOrCompute(supplier).getSortedFileEvents().stream();
        } else {
            return Stream.empty();
        }
    }

    private Stream<? extends Event> getSortedProcessEvents() {
        if (schemas.contains(Schema.PROCESS.toString())) {
            return alertsScenario.getOrCompute(supplier).getSortedProcessEvents().stream();
        } else {
            return Stream.empty();
        }
    }

    private Stream<? extends Event> getSortedRegistryEvents() {
        if (schemas.contains(Schema.REGISTRY.toString())) {
            return alertsScenario.getOrCompute(supplier).getSortedRegistryEvents().stream();
        } else {
            return Stream.empty();
        }
    }


    private Stream<? extends Event> getTlsEvents() {
        if (schemas.contains(Schema.TLS.toString())) {
            List<TlsAlert> tlsAlerts = Stream.of(
                    new Ja3UncommonAlerts(historicalDaysBack, anomalyDay).get(),
                    new Ja3HighBytesSentAlerts(historicalDaysBack, anomalyDay).get(),
                    new SslSubjectUncommonAlerts(historicalDaysBack, anomalyDay).get(),
                    new SslSubjectHighBytesSentAlerts(historicalDaysBack, anomalyDay).get()
            ).flatMap(e -> e).collect(toList());

            Stream<TlsEvent> alertsScenarios = tlsAlerts.parallelStream()
                    .flatMap(e -> e.getIndicators().stream())
                    .flatMap(e -> e.generateEvents().stream());

            Stream<TlsEvent> sessionSplit = new SessionSplitEnrichmentData().generateAll();

            return Stream.of(alertsScenarios, sessionSplit).flatMap(e -> e);

        } else {
            return Stream.empty();
        }
    }


    private Lazy<AlertsScenario> alertsScenario = new Lazy<>();

    private Supplier<AlertsScenario> supplier = () -> {
        try {
            return new AlertsScenario(historicalDaysBack, anomalyDay);
        } catch (GeneratorException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Cannot init alerts scenarios");
    };

}
