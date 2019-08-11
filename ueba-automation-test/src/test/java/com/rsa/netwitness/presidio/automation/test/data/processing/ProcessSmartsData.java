package com.rsa.netwitness.presidio.automation.test.data.processing;

import com.rsa.netwitness.presidio.automation.utils.ade.AdeDataProcessingHelper;
import com.rsa.netwitness.presidio.automation.utils.common.ASCIIArtGenerator;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.rsa.netwitness.presidio.automation.common.helpers.DateTimeHelperUtils.truncateAndMinusDays;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.collections.Lists.newArrayList;


public class ProcessSmartsData extends AbstractTestNGSpringContextTests {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ProcessSmartsData.class.getName());
    private static ASCIIArtGenerator ART_GEN = new ASCIIArtGenerator();

    private AdeDataProcessingHelper adeTestManagerPar = new AdeDataProcessingHelper();
    private List<String> SCHEMAS_TO_PROCESS = newArrayList("file", "active_directory", "authentication", "process", "registry", "tls");
    private List<String> ENTITIES_TO_PROCESS = newArrayList("userId_hourly", "sslSubject_hourly", "ja3_hourly");


    @Parameters({"historical_days_back", "anomaly_day_back"})
    @BeforeClass
    public void prepare(@Optional("10") int historicalDaysBack, @Optional("1") int anomalyDay) throws InterruptedException {
        ART_GEN.printTextArt(getClass().getSimpleName());
        LOGGER.info("\t***** " + getClass().getSimpleName() + " started with historicalDaysBack=" + historicalDaysBack + " anomalyDay=" + anomalyDay);

        List<List<? extends Callable<Integer>>> parallelTasksToExecute = Stream.of(

                processScoreAggr(truncateAndMinusDays(historicalDaysBack), truncateAndMinusDays(anomalyDay + 3), "hourly"),
                processSmart(truncateAndMinusDays(historicalDaysBack), truncateAndMinusDays(anomalyDay + 3)),
                processAccumulateSmart(truncateAndMinusDays(historicalDaysBack), truncateAndMinusDays(anomalyDay + 3)),
                processModeling("smart-record-models", "test-run", truncateAndMinusDays(anomalyDay + 3)),

                processScoreAggr(truncateAndMinusDays(anomalyDay + 3), truncateAndMinusDays(anomalyDay + 2), "hourly"),
                processSmart(truncateAndMinusDays(anomalyDay + 3), truncateAndMinusDays(anomalyDay + 2)),

                processModelFeatureBuckets(truncateAndMinusDays(historicalDaysBack), truncateAndMinusDays(anomalyDay), "hourly"),
                processModeling("enriched-record-models", "test-run", truncateAndMinusDays(anomalyDay)),

                processScoreAggr(truncateAndMinusDays(anomalyDay), truncateAndMinusDays(anomalyDay - 1), "hourly"),
                processAccumulateAggr(truncateAndMinusDays(historicalDaysBack), truncateAndMinusDays(anomalyDay)),
                processModeling("feature-aggregation-record-models", "test-run", truncateAndMinusDays(anomalyDay)),

                processFeatureAggr(truncateAndMinusDays(anomalyDay), truncateAndMinusDays(anomalyDay - 1), "hourly"),
                processAccumulateSmart(truncateAndMinusDays(anomalyDay + 3), truncateAndMinusDays(anomalyDay)),
                processModeling("smart-record-models", "test-run", truncateAndMinusDays(anomalyDay)),
                processSmart(truncateAndMinusDays(anomalyDay), truncateAndMinusDays(anomalyDay - 1))

        ).sequential().collect(toList());


        for (List<? extends Callable<Integer>> parallelTasks : parallelTasksToExecute) {
            LOGGER.info("********** Next cycle **********");
            ExecutorService executor = Executors.newWorkStealingPool();
            List<Future<Integer>> futures = executor.invokeAll(parallelTasks, 70, TimeUnit.MINUTES);
            List<Integer> results = futures.parallelStream().map(toIntResult).collect(toList());
            assertThat(results).containsOnly(0);
        }
    }

    private List<AdeDataProcessingHelper.ProcessScoreAggr> processScoreAggr(Instant start, Instant end, String timeFrame) {
        return SCHEMAS_TO_PROCESS.stream()
                .map(schema -> adeTestManagerPar.processScoreAggr(start, end, timeFrame, schema))
                .collect(toList());
    }

    private List<AdeDataProcessingHelper.ProcessModelFeatureBuckets> processModelFeatureBuckets(Instant start, Instant end, String timeFrame) {
        return SCHEMAS_TO_PROCESS.stream()
                .map(schema -> adeTestManagerPar.processModelFeatureBuckets(start, end, timeFrame, schema))
                .collect(toList());
    }

    private List<AdeDataProcessingHelper.ProcessSmart> processSmart(Instant start, Instant end) {
        return ENTITIES_TO_PROCESS.stream()
                .map(entity -> adeTestManagerPar.processSmart(start, end, entity))
                .collect(toList());
    }

    private List<AdeDataProcessingHelper.ProcessAccumulateSmart> processAccumulateSmart(Instant start, Instant end) {
        return ENTITIES_TO_PROCESS.stream()
                .map(entity -> adeTestManagerPar.processAccumulateSmart(start, end, entity))
                .collect(toList());
    }

    private List<AdeDataProcessingHelper.ProcessModeling> processModeling(String group_name, String session_id, Instant end) {
        return Lists.newArrayList(adeTestManagerPar.processModeling(group_name, session_id, end));
    }

    private List<AdeDataProcessingHelper.ProcessAccumulateAggr> processAccumulateAggr(Instant start, Instant end) {
        return SCHEMAS_TO_PROCESS.stream()
                .map(schema -> adeTestManagerPar.processAccumulateAggr(start, end, schema))
                .collect(toList());
    }

    private List<AdeDataProcessingHelper.ProcessFeatureAggr> processFeatureAggr(Instant start, Instant end, String timeFrame) {
        return SCHEMAS_TO_PROCESS.stream()
                .map(schema -> adeTestManagerPar.processFeatureAggr(start, end, timeFrame, schema))
                .collect(toList());
    }


    private Function<Future<Integer>, Integer> toIntResult = future -> {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return -100;
    };


    @Test
    public void dummy() {
        Assert.assertTrue(true);
    }

    @Test
    public void EndPointIndicatorsCreationTest() {
        /** This test is to verify that Smarts processing result covers all expected features:
         *
         *  * smart_user_id_hourly collection contains high score documents
         *  * all indicators covered
         *
         * **/

        /*
            Parameters for test: threshold for alert creation
            ...?
         */
    }


}