package com.rsa.netwitness.presidio.automation.test.integration;

import com.rsa.netwitness.presidio.automation.context.EnvironmentProperties;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.config.store.NetwitnessEventStoreConfig;
import com.rsa.netwitness.presidio.automation.domain.output.AlertsStoredRecord;
import com.rsa.netwitness.presidio.automation.rest.helper.RestHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.ParametersUrlBuilder;
import com.rsa.netwitness.presidio.automation.ssh.SSHManager;
import com.rsa.netwitness.presidio.automation.ssh.SSHManagerSingleton;
import com.rsa.netwitness.presidio.automation.utils.adapter.AdapterTestManager;
import com.rsa.netwitness.presidio.automation.utils.adapter.config.AdapterTestManagerConfig;
import org.assertj.core.util.Lists;
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
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rsa.netwitness.presidio.automation.utils.output.OutputTestsUtils.skipTest;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, AdapterTestManagerConfig.class, NetwitnessEventStoreConfig.class})
public class OutputForwardingTest extends AbstractTestNGSpringContextTests {

    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(OutputForwardingTest.class.getName());

    @Autowired
    private AdapterTestManager adapterTestManager;

    private Instant endTime = Instant.now();
    private Instant startTime = Instant.now().minus(20, DAYS);
    private List<String> alertsToForward = Lists.newArrayList();




    @Parameters({"historical_days_back", "anomaly_day"})
    @BeforeClass
    public void setup(@Optional("14") int historicalDaysBack, @Optional("1") int anomalyDay) {

        LOGGER.info("\t***** " + getClass().getSimpleName() + " started with historicalDaysBack="
                + historicalDaysBack + " anomalyDay=" + anomalyDay);

        boolean hasNoAnalyticsServer = EnvironmentProperties.ENVIRONMENT_PROPERTIES.esaAnalyticsServerIp().isEmpty();

        if (hasNoAnalyticsServer) {
            skipTest("Skipping tests because environment doesn't have Analytics Server.");
        } else {
            alertsToForward = alertsIndicatorsInTimeRange();
            LOGGER.info(alertsToForward.size() + " alerts fetched from sever.");
        }
    }


    @Test
    public void ja3_forwarded_indicators_count_equals_to_rest_result() {
        String cmd = getForwarderCmd("ja3");
        SSHManager.Response response = SSHManagerSingleton.INSTANCE.getSshManager().runCmd(cmd, true);
        boolean scriptSucceeded = isScriptSucceeded(cmd, response);
        assertThat(scriptSucceeded).isTrue();
        int actual = actualIndicatorsCount(response);

        assertThat(actual).isEqualTo(alertsToForward.size());
    }




    @Test
    public void user_id_forwarded_indicators_count_equals_to_rest_result() {
        String cmd = getForwarderCmd("userId");
        SSHManager.Response response = SSHManagerSingleton.INSTANCE.getSshManager().runCmd(cmd, true);
        boolean scriptSucceeded = isScriptSucceeded(cmd, response);
        assertThat(scriptSucceeded).isTrue();


    }

    @Test
    public void ssl_subject_forwarded_indicators_count_equals_to_rest_result() {
        String cmd = getForwarderCmd("sslSubject");
        SSHManager.Response response = SSHManagerSingleton.INSTANCE.getSshManager().runCmd(cmd, true);
        boolean scriptSucceeded = isScriptSucceeded(cmd, response);
        assertThat(scriptSucceeded).isTrue();


    }






    private int actualIndicatorsCount(SSHManager.Response response) {
        /**    forwarder.Forwarder      : 2922 'INDICATOR' messages were forwarded successfully **/

        String successPatternSt = "'INDICATOR' messages were forwarded successfully";
        Predicate<String> successPattern = line -> line.contains(successPatternSt);
        String errorMessage = "Indicator success patters is not found in cmd output.\nPattern: ".concat(successPatternSt);
        String indicatorsSussesLine = response.output.stream()
                .filter(successPattern)
                .findAny()
                .orElseGet(() -> fail(errorMessage));

        Pattern pattern = Pattern.compile("Forwarder\\s+:\\s+(\\d+)\\s+" + successPatternSt);
        Matcher matcher = pattern.matcher(indicatorsSussesLine);
        String group = matcher.find() ? matcher.group(1) : "-1";
        return Integer.valueOf(group);
    }


    private List<String> alertsIndicatorsInTimeRange() {
        RestHelper restHelper = new RestHelper();
        ParametersUrlBuilder url = restHelper.alerts().url().withMaxSizeAndExpendedParameters();
        List<AlertsStoredRecord> allAlerts = restHelper.alerts().request().getAlerts(url);

        Stream<String> distinctIndicatorsInTimeRange = allAlerts.stream()
                .filter(alert -> alert.getStartDate().isAfter(startTime))
                .filter(alert -> alert.getStartDate().isBefore(endTime))
                .flatMap(alert -> alert.getIndicatorsList().stream().map(AlertsStoredRecord.Indicator::getId))
                .distinct();

        return distinctIndicatorsInTimeRange.collect(Collectors.toList());
    }

    private String getForwarderCmd(String entity) {
        return "/usr/bin/java -Xms2048m -Xmx2048m -Duser.timezone=UTC" +
                " -cp /var/lib/netwitness/presidio/batch/presidio-output-forwarder.jar" +
                " -Dloader.main=presidio.output.forwarder.shell.OutputForwarderApplication" +
                " org.springframework.boot.loader.PropertiesLauncher run" +
                " --start_date " + startTime.toString() +   //2019-08-15T11:00:00
                " --end_date  " + endTime.toString() +
                " --entity_type " + entity;
    }

    private boolean isScriptSucceeded(String cmd, SSHManager.Response response) {
        Predicate<SSHManager.Response> logContainsError = res ->
                res.output.stream().anyMatch(e -> e.contains(" ERROR "))
                        || !res.error.isEmpty();

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


}


