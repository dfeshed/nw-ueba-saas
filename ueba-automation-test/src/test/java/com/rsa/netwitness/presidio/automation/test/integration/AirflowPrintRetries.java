package com.rsa.netwitness.presidio.automation.test.integration;

import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.config.store.NetwitnessEventStoreConfig;
import com.rsa.netwitness.presidio.automation.jdbc.AirflowTasksPostgres;
import com.rsa.netwitness.presidio.automation.jdbc.model.AirflowTaskInstanceTable;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rsa.netwitness.presidio.automation.jdbc.model.AirflowTaskInstanceTable.*;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static org.assertj.core.api.Assertions.assertThat;


@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, AdapterTestManagerConfig.class, NetwitnessEventStoreConfig.class})
public class AirflowPrintRetries extends AbstractTestNGSpringContextTests {

    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(AirflowPrintRetries.class.getName());

    @Autowired
    private AdapterTestManager adapterTestManager;

    private final int MIN_TRIES_TO_DISPLAY = 2;

    private Instant endTime = Instant.now();
    private Instant startTime = Instant.now().minus(1, DAYS);
    private List<AirflowTaskInstanceTable> airflowTasksWithRetries;

    @Parameters({"historical_days_back", "anomaly_day"})
    @BeforeClass
    public void setup(@Optional("14") int historicalDaysBack, @Optional("1") int anomalyDay) {

        LOGGER.info("\t***** " + getClass().getSimpleName() + " started with historicalDaysBack="
                + historicalDaysBack + " anomalyDay=" + anomalyDay);

        AirflowTasksPostgres airflowTasksPostgres = new AirflowTasksPostgres();

        airflowTasksWithRetries = airflowTasksPostgres.fetchRetries(startTime)
                .parallelStream()
                .filter(task -> task.tryNumber >= MIN_TRIES_TO_DISPLAY)
                .collect(Collectors.toList());
    }


    @Test
    public void print_retries() {
        LOGGER.warn("Started on " + Instant.now());
        LOGGER.warn("");

        if (airflowTasksWithRetries.isEmpty()) {
            LOGGER.warn("Not found tasks with reties amount more then " + MIN_TRIES_TO_DISPLAY);
        } else {
            Stream<AirflowTaskInstanceTable> sorted = airflowTasksWithRetries.stream()
                    .sorted(comparing(e -> e.executionDate, reverseOrder()));

            LOGGER.warn("*************************************************");
            LOGGER.warn("****** List of tasks with tries number > " + MIN_TRIES_TO_DISPLAY + " ******");
            LOGGER.warn("*************************************************");

            LOGGER.warn(String.format("%-22s%-65s%-11s%-6s", EXECUTION_DATE, TASK_ID, TRY_NUMBER, MAX_TRIES));
            sorted.forEachOrdered(line -> LOGGER.warn(String.format("%-22s%-70s%-6s%-6s",
                    line.executionDate, line.taskId, line.tryNumber, line.maxTries)));
            LOGGER.warn("*************************************************");
        }
        assertThat(true).isTrue();
    }
}


