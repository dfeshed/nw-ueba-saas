package com.rsa.netwitness.presidio.automation.test.integration;

import com.rsa.netwitness.presidio.automation.context.EnvironmentProperties;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.config.store.NetwitnessEventStoreConfig;
import com.rsa.netwitness.presidio.automation.domain.output.AlertsStoredRecord;
import com.rsa.netwitness.presidio.automation.rest.helper.RestHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.ParametersUrlBuilder;
import com.rsa.netwitness.presidio.automation.utils.adapter.AdapterTestManager;
import com.rsa.netwitness.presidio.automation.utils.adapter.config.AdapterTestManagerConfig;
import com.rsa.netwitness.presidio.automation.utils.common.SSHManager;
import com.rsa.netwitness.presidio.automation.utils.common.SSHManagerSingleton;
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
import java.util.stream.Collectors;

import static com.rsa.netwitness.presidio.automation.utils.output.OutputTestsUtils.skipTest;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;


@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true",})
@SpringBootTest(classes = {MongoConfig.class, AdapterTestManagerConfig.class, NetwitnessEventStoreConfig.class})
public class OutputForwardingTest extends AbstractTestNGSpringContextTests {

    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(OutputForwardingTest.class.getName());

    @Autowired
    private AdapterTestManager adapterTestManager;

    private Instant endTime = Instant.now();
    private Instant startTime = Instant.now().minus(20, DAYS);
    private List<AlertsStoredRecord> alertsToForward = Lists.newArrayList();




    @Parameters({"historical_days_back", "anomaly_day"})
    @BeforeClass
    public void setup(@Optional("14") int historicalDaysBack, @Optional("1") int anomalyDay) {

        LOGGER.info("\t***** " + getClass().getSimpleName() + " started with historicalDaysBack="
                + historicalDaysBack + " anomalyDay=" + anomalyDay);

        if (EnvironmentProperties.ENVIRONMENT_PROPERTIES.esaAnalyticsServerIp().isEmpty()) {
            skipTest("Skipping tests because environment doesn't have Analytics Server.");
        } else {
            alertsToForward = fetchAlerts();
            LOGGER.info(alertsToForward.size() + " alerts fetched from sever.");
        }
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

    @Test
    public void ja3_forwarded_indicators_count_equals_to_rest_result() {
        String cmd = getForwarderCmd("ja3");
        SSHManager.Response response = SSHManagerSingleton.INSTANCE.getSshManager().runCmd(cmd, true);
        boolean scriptSucceeded = isScriptSucceeded(cmd, response);
        assertThat(scriptSucceeded).isTrue();


    }







    private List<AlertsStoredRecord> fetchAlerts() {
        RestHelper restHelper = new RestHelper();
        List<AlertsStoredRecord> allAlerts = Lists.newArrayList();
        ParametersUrlBuilder url = restHelper.alerts().url().withNoParameters();
        allAlerts = restHelper.alerts().request().getAlerts(url);

        return allAlerts.stream()
                .filter(alert -> alert.getStartDate().isAfter(startTime))
                .filter(alert -> alert.getStartDate().isBefore(endTime))
                .collect(Collectors.toList());
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


