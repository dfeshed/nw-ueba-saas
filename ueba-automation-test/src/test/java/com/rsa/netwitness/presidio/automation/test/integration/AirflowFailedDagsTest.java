package com.rsa.netwitness.presidio.automation.test.integration;

import ch.qos.logback.classic.Logger;
import com.rsa.netwitness.presidio.automation.data.processing.airflow.AirflowHelper;
import com.rsa.netwitness.presidio.automation.jdbc.AirflowTasksPostgres;
import com.rsa.netwitness.presidio.automation.jdbc.model.AirflowTaskFailTable;
import com.rsa.netwitness.presidio.automation.data.processing.DataProcessingHelper;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;


public class AirflowFailedDagsTest extends AbstractTestNGSpringContextTests {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(AirflowFailedDagsTest.class);

    private Instant endTime;
    private Instant startTime;

    @BeforeClass
    public void setup() {
        endTime = DataProcessingHelper.INSTANCE.getDataPreparationFinishTime().orElseThrow().truncatedTo(HOURS);
        startTime = endTime.minus(1, DAYS);
    }


    @Test
    public void airflow_failed_dags_for_the_last_day_test() {
        AirflowTasksPostgres airflowTasksPostgres = new AirflowTasksPostgres();
        List<AirflowTaskFailTable> airflowTaskFailTables = airflowTasksPostgres.fetchFailedTasks(startTime, endTime);
        LOGGER.warn("Execution time: " + Instant.now());
        LOGGER.warn("");

        if (airflowTaskFailTables.isEmpty()) {
            LOGGER.warn("No failures found from: " + startTime + ", to " + endTime);
        } else {
            airflowTaskFailTables.forEach(task -> AirflowHelper.INSTANCE.publishLogs(task.dagId, task.taskId, task.executionDate));
            LOGGER.warn("StartTime: " + startTime + ", EndTime " + endTime);
            assertThat(airflowTaskFailTables)
                    .as("Found Airflow failed DAGs.\n" + airflowTaskFailTables.stream()
                            .map(AirflowTaskFailTable::toString)
                            .peek(e -> LOGGER.warn(e))
                            .collect(joining("\n")))
                    .isEmpty();
        }
    }
}


