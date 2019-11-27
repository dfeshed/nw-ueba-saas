package com.rsa.netwitness.presidio.automation.test.integration;

import com.rsa.netwitness.presidio.automation.config.EnvironmentProperties;
import com.rsa.netwitness.presidio.automation.domain.output.AlertsStoredRecord;
import com.rsa.netwitness.presidio.automation.mongo.RespondServerAlertCollectionHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.RestHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.PresidioUrl;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static com.rsa.netwitness.presidio.automation.utils.output.OutputTestsUtils.skipTest;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;


public class OutputForwardingE2eTest extends AbstractTestNGSpringContextTests {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(OutputForwardingE2eTest.class.getName());

    private String esapServer;
    private RestHelper restHelper = new RestHelper();
    private PresidioUrl allAlertsUrl = restHelper.alerts().url().withMaxSizeAndExpendedParameters();
    private List<AlertsStoredRecord> allAlerts;
    private List<AlertsStoredRecord.Indicator> allIndicators;

    private Instant startTime;
    private Instant endTime;

    @BeforeClass
    public void setup() {
        esapServer = EnvironmentProperties.ENVIRONMENT_PROPERTIES.esaAnalyticsServerIp();
        skipAllTestsIfMissingEsapServer();

        allAlerts = restHelper.alerts().request().getAlerts(allAlertsUrl);

        allIndicators = allAlerts.parallelStream()
                .flatMap(alert -> alert.getIndicatorsList().stream())
                .collect(Collectors.toList());

        startTime = allIndicators.parallelStream().map(AlertsStoredRecord.Indicator::getStartDate).min(Instant::compareTo).orElseThrow();
        endTime = allIndicators.parallelStream().map(AlertsStoredRecord.Indicator::getEndDate).max(Instant::compareTo).orElseThrow();
    }


    @Test
    public void all_indicators_from_rest_response_exiting_on_respond_server() {
        Instant receivedTimeLimit = Instant.now().minus(2, DAYS);

        RespondServerAlertCollectionHelper alertCollection = new RespondServerAlertCollectionHelper();
        List<RespondServerAlertCollectionHelper.RespondServerAlert> respondServerAlerts = alertCollection.getRespondServerAlerts(startTime, endTime, receivedTimeLimit);
        assertThat(respondServerAlerts).as("No alerts found on respond server from startDate=" + startTime + " to endDate=" + endTime).isNotEmpty();
        List<String> actualIndicatorIds = respondServerAlerts.parallelStream().map(alert -> alert.uebaIndicatorId).collect(Collectors.toList());

        List<String> expectedIndicatorIds = allIndicators.parallelStream().map(AlertsStoredRecord.Indicator::getId).collect(Collectors.toList());

        assertThat(actualIndicatorIds)
                .as(allAlertsUrl + "\nIndicator IDs mismatch between REST and respond server result.\nFrom startDate=" + startTime + " to endDate=" + endTime)
                .containsAll(expectedIndicatorIds);
    }

    private void skipAllTestsIfMissingEsapServer() {
        if (esapServer == null || esapServer.isBlank()) {
            skipTest("Skipping tests because environment doesn't have Analytics Server.");
        }
    }


}


