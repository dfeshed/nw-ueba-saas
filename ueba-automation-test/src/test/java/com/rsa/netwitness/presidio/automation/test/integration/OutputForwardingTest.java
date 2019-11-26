package com.rsa.netwitness.presidio.automation.test.integration;

import com.rsa.netwitness.presidio.automation.config.EnvironmentProperties;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.config.store.NetwitnessEventStoreConfig;
import com.rsa.netwitness.presidio.automation.domain.output.AlertsStoredRecord;
import com.rsa.netwitness.presidio.automation.rest.helper.RestHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.PresidioUrl;
import com.rsa.netwitness.presidio.automation.ssh.client.SshResponse;
import com.rsa.netwitness.presidio.automation.ssh.helper.SshHelper;
import com.rsa.netwitness.presidio.automation.test_managers.AdapterTestManager;
import com.rsa.netwitness.presidio.automation.utils.adapter.config.AdapterTestManagerConfig;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.rsa.netwitness.presidio.automation.utils.output.OutputTestsUtils.skipTest;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, AdapterTestManagerConfig.class, NetwitnessEventStoreConfig.class})
public class OutputForwardingTest extends AbstractTestNGSpringContextTests {

    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(OutputForwardingTest.class.getName());
    private String esapServer;
    private RestHelper restHelper = new RestHelper();
    private PresidioUrl allAlertsUrl = restHelper.alerts().url().withMaxSizeAndExpendedParameters();
    private List<AlertsStoredRecord> allAlerts;
    private Map<String, List<AlertsStoredRecord.Indicator>> indicatorsInTimeRangeByEntityType;
    private Map<String, List<String>> indicatorIdsByType;

    @Autowired
    private AdapterTestManager adapterTestManager;

    private Instant endTime = Instant.now();
    private Instant startTime = Instant.now().minus(20, DAYS);
    private Predicate<? super AlertsStoredRecord> byTime = e -> e.getStartDate().isAfter(startTime) && e.getEndDate().isBefore(endTime);

    private SshHelper sshHelper = new SshHelper();


    @Parameters({"historical_days_back", "anomaly_day"})
    @BeforeClass
    public void setup(@Optional("14") int historicalDaysBack, @Optional("1") int anomalyDay) {

        LOGGER.info("\t***** " + getClass().getSimpleName() + " started with historicalDaysBack="
                + historicalDaysBack + " anomalyDay=" + anomalyDay);

        esapServer = EnvironmentProperties.ENVIRONMENT_PROPERTIES.esaAnalyticsServerIp();
        skipAllTestsIfMissingEsapServer();

        allAlerts = restHelper.alerts().request().getAlerts(allAlertsUrl);
        assertThat(allAlerts).as(allAlertsUrl + "\nEmpty alerts response").isNotNull().isNotEmpty();

        indicatorsInTimeRangeByEntityType = allAlerts.parallelStream()
                .filter(byTime)
                .flatMap(alert -> alert.getIndicatorsList().stream())
                .collect(Collectors.groupingBy(AlertsStoredRecord.Indicator::getAlertEntityType));

        indicatorIdsByType = indicatorsInTimeRangeByEntityType.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> e.getValue().parallelStream().map(ind -> ind.getId()).distinct().collect(toList()))
                );

    }


    @Test
    public void ja3_forwarded_indicators_count_equals_to_rest_result() {
        String cmd = getForwarderCmd("ja3", startTime, endTime);
        SshResponse response = sshHelper.uebaHostExec().run(cmd);

        String errorMessage = "Script execution failure.\n[" + cmd + "]\n"
                + String.join("\n", response.output);

        boolean scriptSucceeded = isScriptSucceeded(cmd, response);
        assertThat(scriptSucceeded)
                .overridingErrorMessage(errorMessage)
                .isTrue();
        int actual = actualIndicatorsCount(response);
        assertThat(actual).isEqualTo(indicatorIdsByType.get("ja3").size());
    }


    @Test
    public void user_id_forwarded_indicators_count_equals_to_rest_result() {
        String cmd = getForwarderCmd("userId", startTime, endTime);
        SshResponse response = sshHelper.uebaHostExec().run(cmd);
        boolean scriptSucceeded = isScriptSucceeded(cmd, response);
        assertThat(scriptSucceeded).isTrue();
        int actual = actualIndicatorsCount(response);
        assertThat(actual).isEqualTo(indicatorIdsByType.get("userId").size());
    }

    @Test
    public void ssl_subject_forwarded_indicators_count_equals_to_rest_result() {
        String cmd = getForwarderCmd("sslSubject", startTime, endTime);
        SshResponse response = sshHelper.uebaHostExec().run(cmd);
        boolean scriptSucceeded = isScriptSucceeded(cmd, response);
        assertThat(scriptSucceeded).isTrue();
        int actual = actualIndicatorsCount(response);
        assertThat(actual).isEqualTo(indicatorIdsByType.get("sslSubject").size());
    }


    private int actualIndicatorsCount(SshResponse response) {
        /**    forwarder.Forwarder      : 2922 'INDICATOR' messages were forwarded successfully **/

        String successPatternSt = "'INDICATOR' messages were forwarded successfully";
        Predicate<String> successPattern = line -> line.contains(successPatternSt);
        String errorMessage = "Indicator success patters is not found in cmd output.\nPattern: ".concat(successPatternSt);
        String indicatorsSussesLine = response.output.stream()
                .filter(successPattern)
                .findAny()
                .orElseGet(() -> fail(errorMessage));

        LOGGER.info(indicatorsSussesLine);

        Pattern pattern = Pattern.compile("Forwarder\\s+:\\s+(\\d+)\\s+" + successPatternSt);
        Matcher matcher = pattern.matcher(indicatorsSussesLine);
        String group = matcher.find() ? matcher.group(1) : "-1";
        return Integer.valueOf(group);
    }


    private String getForwarderCmd(String entity, Instant fromTime, Instant toTime) {
        return "/usr/bin/java -Xms2048m -Xmx2048m -Duser.timezone=UTC" +
                " -cp /var/lib/netwitness/presidio/batch/presidio-output-forwarder.jar" +
                " -Dloader.main=presidio.output.forwarder.shell.OutputForwarderApplication" +
                " org.springframework.boot.loader.PropertiesLauncher run" +
                " --start_date " + fromTime.toString() +   //2019-08-15T11:00:00
                " --end_date  " + toTime.toString() +
                " --entity_type " + entity;
    }

    private boolean isScriptSucceeded(String cmd, SshResponse response) {
        Predicate<SshResponse> logContainsError = res ->
                res.output.stream().anyMatch(e -> e.contains(" ERROR "));

        if (response.exitCode != 0 || logContainsError.test(response)) {
            LOGGER.error("[presidio-output-forwarder.jar]: error exit code return.");
            LOGGER.error("EXIT CODE=" + response.exitCode);
            LOGGER.error("CMD: [" + cmd + "]");
            System.out.println("\n\t********************************************\n");
            response.output.forEach(System.out::println);
            System.out.println("\n\t********************************************\n");
            return false;
        } else {
            return true;
        }
    }


    private void skipAllTestsIfMissingEsapServer() {
        if (esapServer == null || esapServer.isBlank()) {
            skipTest("Skipping tests because environment doesn't have Analytics Server.");
        }
    }


}


