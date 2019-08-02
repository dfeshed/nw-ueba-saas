package com.rsa.netwitness.presidio.automation.test.data.preparation;

import com.rsa.netwitness.presidio.automation.common.scenarios.alerts.AlertsScenario;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import presidio.data.domain.event.Event;
import presidio.data.generators.common.GeneratorException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AlertsScenarioDataPreparation extends DataPreparationBase {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(AlertsScenarioDataPreparation.class.getName());

    @Override
    protected List<? extends Event> generate() throws GeneratorException {
        LOGGER.info("Going to generate events for Alert scenarios");

        AlertsScenario alertsScenario = new AlertsScenario(historicalDaysBack, anomalyDay);

        return Stream.of(
                alertsScenario.getSortedAuthenticationEvents().stream(),
                alertsScenario.getSortedActiveDirectoryEvents().stream(),
                alertsScenario.getSortedFileEvents().stream(),
                alertsScenario.getSortedProcessEvents().stream(),
                alertsScenario.getSortedRegistryEvents().stream()

        ).flatMap(i -> i).collect(Collectors.toList());
    }

    @Test
    public void dataReachedTheDestination() {
        generatorResultCount.forEach((schema, count) ->
                Assert.assertTrue(count > 0, "No events were sent from " + schema));
    }



}
