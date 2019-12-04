package com.rsa.netwitness.presidio.automation.test.integration;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.ImmutableList;
import com.rsa.netwitness.presidio.automation.data.processing.DataProcessingHelper;
import com.rsa.netwitness.presidio.automation.file.configurations.WorkflowsDefaultJson;
import com.rsa.netwitness.presidio.automation.jdbc.AirflowTasksPostgres;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.assertj.core.api.SoftAssertions;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.*;
import static org.apache.commons.lang3.tuple.ImmutablePair.of;


public class AirflowTasksStartTimeTest extends AbstractTestNGSpringContextTests {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(AirflowTasksStartTimeTest.class);

    private final TemporalAmount OFFSET = Duration.of(1, HOURS);

    private Instant dataPreparationFinishTime;
    private Instant executionStart;

    private int featureAggregationModelsMinDays, smartRecordsModelsMinDays, enrichedRecordsModelsMinDays;
    private AirflowTasksPostgres airflowTasksPostgres = new AirflowTasksPostgres();

    private ImmutablePair<String, String> taskForStartTimeReference = of("TLS_indicator_ueba_flow", "adapter_TLS");


    private List<ImmutablePair<String, String>> featureAggregationTasks = ImmutableList.<ImmutablePair<String, String>>builder()
            .add(of("TLS_indicator_ueba_flow", "hourly_TLS_feature_aggregations"))
            .add(of("TLS_indicator_ueba_flow", "hourly_TLS_score_aggregations"))

            .add(of("TLS_model_ueba_flow", "TLS_aggr_model"))

            .add(of("ja3_hourly_ueba_flow", "smart_model_trigger"))
            .add(of("ja3_hourly_ueba_flow", "ja3_hourly"))
            .add(of("ja3_hourly_ueba_flow", "hourly_output_processor"))

            .add(of("ja3_hourly_model_ueba_flow", "ja3_hourly_smart_model_accumulation"))

            .build();

    private List<ImmutablePair<String, String>> smartTaks;
    private List<ImmutablePair<String, String>> enrichTaks;





    @BeforeClass
    public void setup() {
        dataPreparationFinishTime = DataProcessingHelper.INSTANCE.getDataPreparationFinishTime().orElseThrow().truncatedTo(HOURS);

        featureAggregationModelsMinDays = WorkflowsDefaultJson.getInstance().getFeatureAggregationRecordsConf().minDataTimeRangeForBuildingModelsInDays;
        smartRecordsModelsMinDays = WorkflowsDefaultJson.getInstance().getSmartRecordsConf().minDataTimeRangeForBuildingModelsInDays;
        enrichedRecordsModelsMinDays = WorkflowsDefaultJson.getInstance().getEnrichedRecordsConf().minDataTimeRangeForBuildingModelsInDays;

        executionStart = airflowTasksPostgres.getFirstSucceededExecutionDate(taskForStartTimeReference.left, taskForStartTimeReference.right).orElseThrow();

    }


    @Test
    public void feature_aggregation_tasks_start_time_match_configuration() {
        SoftAssertions softly = new SoftAssertions();
        final Instant expectedStartTime = executionStart.plus(featureAggregationModelsMinDays, DAYS);
        LOGGER.info("Feature aggregation reference start time =" + expectedStartTime);

        for (ImmutablePair<String, String> task : featureAggregationTasks) {
            String details = "\ndag_id=".concat(task.left).concat(", task_id=" + task.right);
            Optional<Instant> firstSucceededExecutionDate = airflowTasksPostgres.getFirstSucceededExecutionDate(task.left, task.right);

            softly.assertThat(firstSucceededExecutionDate).as("Unable to get first succeeded execution date for " + details).isPresent();

            firstSucceededExecutionDate.ifPresent(date -> softly.assertThat(date)
                            .as("First succeeded execution date is out of bounds." + details)
                            .isBetween(expectedStartTime.minus(OFFSET), expectedStartTime.plus(OFFSET)));
        }

        softly.assertAll();
    }
}


