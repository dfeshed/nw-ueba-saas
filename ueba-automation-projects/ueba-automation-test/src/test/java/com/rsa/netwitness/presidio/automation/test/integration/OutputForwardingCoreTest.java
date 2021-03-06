package com.rsa.netwitness.presidio.automation.test.integration;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.ImmutableList;
import com.rsa.netwitness.presidio.automation.config.EnvironmentProperties;
import com.rsa.netwitness.presidio.automation.domain.output.AlertsStoredRecord;
import com.rsa.netwitness.presidio.automation.mongo.RespondServerAlertCollectionHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.RestHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.PresidioUrl;
import com.rsa.netwitness.presidio.automation.ssh.client.SshResponse;
import com.rsa.netwitness.presidio.automation.ssh.helper.SshHelper;
import org.assertj.core.api.Fail;
import org.assertj.core.api.SoftAssertions;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.rsa.netwitness.presidio.automation.utils.output.OutputTestsUtils.skipTest;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


public class OutputForwardingCoreTest extends AbstractTestNGSpringContextTests {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(OutputForwardingCoreTest.class);

    private final int TIME_OFFSET = 3;
    private String esapServer;
    private RestHelper restHelper = new RestHelper();
    private SshHelper sshHelper = new SshHelper();
    private RespondServerAlertCollectionHelper respondServerAlertCollectionHelper;

    private final PresidioUrl allAlertsUrl = restHelper.alerts().url().withMaxSizeAndExpendedParameters();
    private List<AlertsStoredRecord> allAlerts;
    List<AlertsStoredRecord.Indicator> allIndicators;
    private Map<String, List<AlertsStoredRecord.Indicator>> indicatorsInTimeRangeByEntityType;
    private Map<String, List<String>> indicatorIdsByType;
    private RespondServerAlertCollectionHelper alertCollection = new RespondServerAlertCollectionHelper();

    private Instant endTime;
    private Instant startTime;
    private Predicate<? super AlertsStoredRecord> byTime = e -> e.getStartDate().plusSeconds(1).isAfter(startTime) && e.getEndDate().minusSeconds(1).isBefore(endTime);


    @BeforeClass
    public void setup() {
        esapServer = EnvironmentProperties.ENVIRONMENT_PROPERTIES.esaAnalyticsServerIp();
        skipAllTestsIfMissingEsapServer();

        respondServerAlertCollectionHelper = new RespondServerAlertCollectionHelper();
        allAlerts = restHelper.alerts().request().getAlerts(allAlertsUrl);
        assertThat(allAlerts).as(allAlertsUrl + "\nEmpty alerts response").isNotNull().isNotEmpty();

        allIndicators = allAlerts.parallelStream().flatMap(alert -> alert.getIndicatorsList().stream()).collect(toList());

        Instant firstStartDate = allAlerts.parallelStream().map(AlertsStoredRecord::getStartDate).min(Instant::compareTo).orElseThrow();
        Instant lastEndDate = allAlerts.parallelStream().map(AlertsStoredRecord::getEndDate).max(Instant::compareTo).orElseThrow();
        startTime = firstStartDate.plus(TIME_OFFSET, HOURS);
        endTime = lastEndDate.minus(TIME_OFFSET, HOURS);

        indicatorsInTimeRangeByEntityType = allAlerts.parallelStream()
                .filter(byTime)
                .flatMap(alert -> alert.getIndicatorsList().stream())
                .collect(Collectors.groupingBy(AlertsStoredRecord.Indicator::getAlertEntityType));

        LOGGER.info("REST response indicators count from " + startTime + " to " + endTime);
        indicatorsInTimeRangeByEntityType.forEach((k,v) -> System.out.println(k + ": " + v.size()));

        indicatorIdsByType = indicatorsInTimeRangeByEntityType.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> e.getValue().parallelStream().map(ind -> ind.getId()).distinct().collect(toList()))
                );

        respondServerAlertCollectionHelper.deleteAllAlertCollectionsIncludingBackup();
    }


    @Test
    public void ja3_forwarded_indicators_count_equals_to_rest_result() {
        test("ja3");
    }

    @Test
    public void user_id_forwarded_indicators_count_equals_to_rest_result() {
        test("userId");
    }

    @Test
    public void ssl_subject_forwarded_indicators_count_equals_to_rest_result() {
        test("sslSubject");
    }


    private final int MAX_RETRIES = 5;
    private final int WAIT_SEC = 3;

    private void test(String entityType) {
        SoftAssertions softly = new SoftAssertions();
        LOGGER.info(" --- Forwarding validation started for " + entityType);

        ImmutableList<String> expectedIndicatorIdsFromRest = ImmutableList.copyOf(indicatorIdsByType.get(entityType));
        assertThat(expectedIndicatorIdsFromRest).as("Empty Respond Server result for " + entityType).isNotEmpty();

        String cmd = getForwarderCmd(entityType, startTime, endTime);
        SshResponse response = sshHelper.uebaHostExec().run(cmd);
        testScriptFinishedSuccessfully(cmd, response);

        int cmdActual = actualIndicatorsFromCmdResponse(response);
        assertThat(cmdActual).as("REST count doesn't match log response.").isEqualTo(expectedIndicatorIdsFromRest.size());

        List<RespondServerAlertCollectionHelper.RespondServerAlert> respondServerAlerts = getRespondServerAlerts(cmdActual, entityType);

        ImmutableList<String> actualIndicatorIdsFromRespondServer = ImmutableList.copyOf(respondServerAlerts.parallelStream().map(alert -> alert.uebaIndicatorId).collect(toList()));
        assertThat(actualIndicatorIdsFromRespondServer).as("No alerts found on respond server from startDate=" + startTime + " to endDate=" + endTime).isNotEmpty();

        List<String> missingFromRespondServer = new ArrayList<>(expectedIndicatorIdsFromRest);
        List<String> missingFromRest = new ArrayList<>(actualIndicatorIdsFromRespondServer);
        missingFromRespondServer.removeAll(actualIndicatorIdsFromRespondServer);
        missingFromRest.removeAll(expectedIndicatorIdsFromRest);

        LOGGER.info(" --- Forwarding validation before test " + entityType);

        Function<List<String>, String> printDetails = missingIds ->
                allIndicators.parallelStream().filter(e -> missingIds.stream().anyMatch(missingId -> missingId.equals(e.getId())))
                        .map(e -> e.getId().concat(" ").concat(e.getAlertEntityType()).concat(" ").concat(e.getStartDate().toString()).concat(" ").concat(e.getEndDate().toString()))
                        .collect(joining("\n"));

        softly.assertThat(missingFromRespondServer)
                .overridingErrorMessage(allAlertsUrl + "\n[" + entityType + "]: REST response Indicators missing from Respond Server: \n" + printDetails.apply(missingFromRespondServer))
                .isEmpty();

        softly.assertThat(missingFromRest)
                .as(allAlertsUrl + "\n[" + entityType + "]: Respond server Indicators missing from from REST." + "\n" + printDetails.apply(missingFromRest))
                .isEmpty();


        respondServerAlertCollectionHelper.backupAlertCollection(entityType);
        softly.assertAll();
        LOGGER.info(" --- Forwarding validation after test " + entityType);
    }

    private List<RespondServerAlertCollectionHelper.RespondServerAlert> getRespondServerAlerts(int cmdActual, String entityType) {
        List<RespondServerAlertCollectionHelper.RespondServerAlert> respondServerAlerts = new ArrayList<>();

        for (int i = 0; (i < MAX_RETRIES) && (respondServerAlerts.size() < cmdActual); i++) {
            try {
                TimeUnit.SECONDS.sleep(WAIT_SEC);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            respondServerAlerts = alertCollection.getRespondServerAlertsForLastWeek(startTime, endTime);
            LOGGER.info("<<<" + entityType + ">>> " + respondServerAlerts.size() + " alerts found on the respond server.");
        }
        return respondServerAlerts;
    }


    private int actualIndicatorsFromCmdResponse(SshResponse response) {
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

    private void testScriptFinishedSuccessfully(String cmd, SshResponse response) {
        String errorMessage = "Script execution failure.\n[" + cmd + "]\n" + String.join("\n", response.output);

        Predicate<SshResponse> logContainsError = res ->
                res.output.stream().anyMatch(e -> e.contains(" ERROR "));

        if (response.exitCode != 0 || logContainsError.test(response)) {
            LOGGER.error("[presidio-output-forwarder.jar]: error exit code return.");
            LOGGER.error("EXIT CODE=" + response.exitCode);
            LOGGER.error("CMD: [" + cmd + "]");
            System.out.println("\n\t********************************************\n");
            response.output.forEach(System.out::println);
            System.out.println("\n\t********************************************\n");
            Fail.fail(errorMessage);
        }
    }


    private void skipAllTestsIfMissingEsapServer() {
        if (esapServer == null || esapServer.isBlank()) {
            skipTest("Skipping tests because environment doesn't have Analytics Server.");
        }
    }


}


