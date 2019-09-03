package com.rsa.netwitness.presidio.automation.test.integration;

import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.config.store.NetwitnessEventStoreConfig;
import com.rsa.netwitness.presidio.automation.jdbc.AirflowDbHelper;
import com.rsa.netwitness.presidio.automation.jdbc.model.AirflowTaskFailTable;
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

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;


@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, AdapterTestManagerConfig.class, NetwitnessEventStoreConfig.class})
public class AirflowFailedDagsTest extends AbstractTestNGSpringContextTests {

    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(AirflowFailedDagsTest.class.getName());

    @Autowired
    private AdapterTestManager adapterTestManager;

    private Instant endTime = Instant.now();
    private Instant startTime = Instant.now().minus(1, DAYS);


    @Parameters({"historical_days_back", "anomaly_day"})
    @BeforeClass
    public void setup(@Optional("14") int historicalDaysBack, @Optional("1") int anomalyDay) {

        LOGGER.info("\t***** " + getClass().getSimpleName() + " started with historicalDaysBack="
                + historicalDaysBack + " anomalyDay=" + anomalyDay);
    }


    @Test
    public void airflow_failed_dags_test() {
        AirflowDbHelper airflowDbHelper = new AirflowDbHelper();
        List<AirflowTaskFailTable> airflowTaskFailTables = airflowDbHelper.fetchFailedTasks(startTime);

        assertThat(airflowTaskFailTables)
                .overridingErrorMessage("Found Airflow failed DAGs.\n" +
                        airflowTaskFailTables.stream().map(AirflowTaskFailTable::toString).collect(joining("\n")))
                .isEmpty();
    }
}


