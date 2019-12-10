package com.rsa.netwitness.presidio.automation.test.integration;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.ImmutableList;
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

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.apache.commons.lang3.tuple.ImmutablePair.of;


public class AirflowTasksStartTimeTest extends AbstractTestNGSpringContextTests {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(AirflowTasksStartTimeTest.class);

    /** https://wiki.na.rsa.net/pages/viewpage.action?pageId=139268800 **/

    private final TemporalAmount OFFSET = Duration.of(1, HOURS);
    private Instant executionStart;
    private int featureAggregationModelsMinDays, smartRecordsModelsMinDays, enrichedRecordsModelsMinDays;
    private AirflowTasksPostgres airflowTasksPostgres = new AirflowTasksPostgres();


    private ImmutablePair<String, String> taskForStartTimeReference = of("TLS_indicator_ueba_flow", "adapter_TLS");

    private List<ImmutablePair<String, String>> startAfterFeatureAggModelsMinDataTimeTasks = ImmutableList.<ImmutablePair<String, String>>builder()
            .add(of("ACTIVE_DIRECTORY_model_ueba_flow", "ACTIVE_DIRECTORY_aggr_model"))
            .add(of("AUTHENTICATION_model_ueba_flow", "AUTHENTICATION_aggr_model"))
            .add(of("FILE_model_ueba_flow", "FILE_aggr_model"))
            .add(of("PROCESS_model_ueba_flow", "PROCESS_aggr_model"))
            .add(of("REGISTRY_model_ueba_flow", "REGISTRY_aggr_model"))
            .add(of("TLS_model_ueba_flow", "TLS_aggr_model"))
            .build();


    private List<ImmutablePair<String, String>> startAfterEnrichedRecordsModelsMinDataTimeTasks = ImmutableList.<ImmutablePair<String, String>>builder()
            .add(of("ACTIVE_DIRECTORY_model_ueba_flow", "ACTIVE_DIRECTORY_raw_model_task"))
            .add(of("AUTHENTICATION_model_ueba_flow", "AUTHENTICATION_raw_model_task"))
            .add(of("FILE_model_ueba_flow", "FILE_raw_model_task"))
            .add(of("PROCESS_model_ueba_flow", "PROCESS_raw_model_task"))
            .add(of("REGISTRY_model_ueba_flow", "REGISTRY_raw_model_task"))
            .add(of("TLS_model_ueba_flow", "TLS_raw_model_task"))
            .build();

    private List<ImmutablePair<String, String>> startAfterMaxFeatureAggAndEnrichedRecordsTasks = ImmutableList.<ImmutablePair<String, String>>builder()
            .add(of("ACTIVE_DIRECTORY_indicator_ueba_flow", "hourly_ACTIVE_DIRECTORY_feature_aggregations"))
            .add(of("ACTIVE_DIRECTORY_indicator_ueba_flow", "hourly_ACTIVE_DIRECTORY_score_aggregations"))

            .add(of("AUTHENTICATION_indicator_ueba_flow", "hourly_AUTHENTICATION_feature_aggregations"))
            .add(of("AUTHENTICATION_indicator_ueba_flow", "hourly_AUTHENTICATION_score_aggregations"))

            .add(of("FILE_indicator_ueba_flow", "hourly_FILE_feature_aggregations"))
            .add(of("FILE_indicator_ueba_flow", "hourly_FILE_score_aggregations"))

            .add(of("PROCESS_indicator_ueba_flow", "hourly_PROCESS_feature_aggregations"))
            .add(of("PROCESS_indicator_ueba_flow", "hourly_PROCESS_score_aggregations"))

            .add(of("REGISTRY_indicator_ueba_flow", "hourly_REGISTRY_feature_aggregations"))
            .add(of("REGISTRY_indicator_ueba_flow", "hourly_REGISTRY_score_aggregations"))

            .add(of("TLS_indicator_ueba_flow", "hourly_TLS_feature_aggregations"))
            .add(of("TLS_indicator_ueba_flow", "hourly_TLS_score_aggregations"))

            .add(of("userId_hourly_model_ueba_flow", "userId_hourly_smart_model_accumulation"))
            .add(of("ja3_hourly_model_ueba_flow", "ja3_hourly_smart_model_accumulation"))
            .add(of("sslSubject_hourly_model_ueba_flow", "sslSubject_hourly_smart_model_accumulation"))

            .add(of("userId_hourly_ueba_flow", "userId_hourly"))
            .add(of("userId_hourly_ueba_flow", "hourly_output_processor"))
            .add(of("sslSubject_hourly_ueba_flow", "sslSubject_hourly"))
            .add(of("sslSubject_hourly_ueba_flow", "hourly_output_processor"))
            .add(of("ja3_hourly_ueba_flow", "ja3_hourly"))
            .add(of("ja3_hourly_ueba_flow", "hourly_output_processor"))
            .build();


    private List<ImmutablePair<String, String>> startAfterMaxFeatureAggAndEnrichedRecPlusSmartRecTasks = ImmutableList.<ImmutablePair<String, String>>builder()
            .add(of("userId_hourly_model_ueba_flow", "userId_hourly_smart_model"))
            .add(of("sslSubject_hourly_model_ueba_flow", "sslSubject_hourly_smart_model"))
            .add(of("ja3_hourly_model_ueba_flow", "ja3_hourly_smart_model"))

            .add(of("userId_hourly_ueba_flow", "output_forwarding_task"))
            .add(of("sslSubject_hourly_ueba_flow", "output_forwarding_task"))
            .add(of("ja3_hourly_ueba_flow", "output_forwarding_task"))

            .add(of("userId_hourly_ueba_flow", "userId_hourly_score_processor"))
            .add(of("sslSubject_hourly_ueba_flow", "sslSubject_hourly_score_processor"))
            .add(of("ja3_hourly_ueba_flow", "ja3_hourly_score_processor"))
            .build();


    @BeforeClass
    public void setup() {
        featureAggregationModelsMinDays = WorkflowsDefaultJson.getInstance().getFeatureAggregationRecordsConf().minDataTimeRangeForBuildingModelsInDays;
        smartRecordsModelsMinDays = WorkflowsDefaultJson.getInstance().getSmartRecordsConf().minDataTimeRangeForBuildingModelsInDays;
        enrichedRecordsModelsMinDays = WorkflowsDefaultJson.getInstance().getEnrichedRecordsConf().minDataTimeRangeForBuildingModelsInDays;

        executionStart = airflowTasksPostgres.getFirstSucceededExecutionDate(taskForStartTimeReference.left, taskForStartTimeReference.right).orElseThrow();
        LOGGER.info("Tasks execution start time is " + executionStart);
    }


    @Test
    public void task_start_date_is_after_feature_agg_models() {
        final Instant expectedStartTime = executionStart.plus(featureAggregationModelsMinDays, DAYS);
        LOGGER.info("Reference start time =" + expectedStartTime);
        validateFirstSucceededTaskStartTime(expectedStartTime, startAfterFeatureAggModelsMinDataTimeTasks).assertAll();
    }

    @Test
    public void task_start_date_is_after_enrich_rec_models() {
        final Instant expectedStartTime = executionStart.plus(enrichedRecordsModelsMinDays, DAYS);
        LOGGER.info("Reference start time =" + expectedStartTime);
        validateFirstSucceededTaskStartTime(expectedStartTime, startAfterEnrichedRecordsModelsMinDataTimeTasks).assertAll();
    }

    @Test
    public void task_start_date_is_after_max_of_feature_agg_and_enrich_rec() {
        final Instant expectedStartTime = executionStart.plus(Math.max(featureAggregationModelsMinDays, enrichedRecordsModelsMinDays), DAYS);
        LOGGER.info("Reference start time =" + expectedStartTime);
        validateFirstSucceededTaskStartTime(expectedStartTime, startAfterMaxFeatureAggAndEnrichedRecordsTasks).assertAll();
    }

    @Test
    public void task_start_date_is_after_max_of_feature_agg_and_enrich_rec_then_plus_smart_records() {
        final Instant expectedStartTime = executionStart.plus(Math.max(featureAggregationModelsMinDays, enrichedRecordsModelsMinDays), DAYS)
                .plus(smartRecordsModelsMinDays, DAYS);
        LOGGER.info("Reference start time =" + expectedStartTime);
        validateFirstSucceededTaskStartTime(expectedStartTime, startAfterMaxFeatureAggAndEnrichedRecPlusSmartRecTasks).assertAll();
    }


    private SoftAssertions validateFirstSucceededTaskStartTime(final Instant expectedStartTime, List<ImmutablePair<String, String>> enrichTasks) {
        SoftAssertions softly = new SoftAssertions();
        for (ImmutablePair<String, String> task : enrichTasks) {
            String details = "\ndag_id=".concat(task.left).concat(", task_id=" + task.right);
            Optional<Instant> firstSucceededExecutionDate = airflowTasksPostgres.getFirstSucceededExecutionDate(task.left, task.right);

            softly.assertThat(firstSucceededExecutionDate).as("Unable to get first succeeded execution date for " + details).isPresent();

            firstSucceededExecutionDate.ifPresent(date -> softly.assertThat(date)
                    .as("First succeeded execution date is out of bounds." + details)
                    .isBetween(expectedStartTime.minus(OFFSET), expectedStartTime.plus(OFFSET)));
        }
        return softly;
    }


}


