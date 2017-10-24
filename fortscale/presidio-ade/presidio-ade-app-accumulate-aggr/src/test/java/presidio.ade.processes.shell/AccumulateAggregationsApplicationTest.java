package presidio.ade.processes.shell;


import fortscale.common.general.Schema;
import fortscale.utils.time.TimeService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;
import presidio.ade.test.utils.tests.EnrichedDataBaseAppTest;
import presidio.data.ade.AdeFileOperationGeneratorTemplateFactory;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.fileop.IFileOperationGenerator;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@ContextConfiguration
public class AccumulateAggregationsApplicationTest extends EnrichedDataBaseAppTest {
//    private static final int DAYS_BACK_FROM = 3;
//    private static final int DAYS_BACK_TO = 1;
//    private static final Schema ADE_EVENT_TYPE = Schema.FILE;
    private static final Duration ACCUMUATION_DURATION = Duration.ofDays(1);
//    private static final Instant START_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(DAYS_BACK_FROM)), ACCUMUATION_DURATION);
//    private static final Instant END_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(DAYS_BACK_TO)), ACCUMUATION_DURATION);

    public static final String EXECUTION_COMMAND_FORMAT = "run --schema %s --start_date %s --end_date %s --fixed_duration_strategy %s --feature_bucket_strategy %s";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Import({ EnrichedDataBaseAppTest.EnrichedDataBaseAppSpringConfig.class, AccumulateAggregationsConfigurationTest.class, AccumulateServiceCommands.class})
    @Configuration
    protected static class AccumulateAggregationsTestConfig {
    }

    @Before
    public void setup() {
        prepareTest();
    }

    @Override
    protected String getContextTestExecutionCommand() {
        int daysBackFrom = 3;
        int daysBackTo = 1;
        Instant startInstant = TimeService.floorTime(Instant.now().minus(Duration.ofDays(daysBackFrom)), ACCUMUATION_DURATION);
        Instant endInstant = TimeService.floorTime(Instant.now().minus(Duration.ofDays(daysBackTo)), ACCUMUATION_DURATION);
        Schema adeEventType = Schema.FILE;
        int fixDurationStrategy = 86400;
        int featureBucketStrategy = 3600;
        return String.format(EXECUTION_COMMAND_FORMAT, adeEventType, startInstant.toString(), endInstant.toString(), fixDurationStrategy, featureBucketStrategy);
    }

    /**
     * Generate 6 successful file open events per hour along 23 hours for 2 days in the enriched data.
     * Then running the module and then testing the results.
     * <p>
     * Expected result:
     * 2 accumulatedRecords - record per day.
     * 23 aggregatedFeatureValues for each record.
     * value of each aggregatedFeatureValues is 6 (6 opens files in each hour)
     */
    @Test
    public void sanityTest() throws GeneratorException {
        int startHourOfDay = 0;
        int endHourOfDay = 23;
        int daysBackFrom = 3;
        int daysBackTo = 1;
        Instant startInstant = TimeService.floorTime(Instant.now().minus(Duration.ofDays(daysBackFrom)), ACCUMUATION_DURATION);
        Instant endInstant = TimeService.floorTime(Instant.now().minus(Duration.ofDays(daysBackTo)), ACCUMUATION_DURATION);
        Schema adeEventType = Schema.FILE;
        int fixDurationStrategy = 86400;
        int featureBucketStrategy = 3600;
        String executionCommand = String.format(EXECUTION_COMMAND_FORMAT, adeEventType, startInstant.toString(), endInstant.toString(), fixDurationStrategy, featureBucketStrategy);

        IFileOperationGenerator fileOperationActionGenerator = new AdeFileOperationGeneratorTemplateFactory().createOpenFileOperationsGenerator();
        generateAndPersistFileEventData(Collections.singletonList(fileOperationActionGenerator), startHourOfDay, endHourOfDay, daysBackFrom, daysBackTo, "testUser");
        executeAndAssertCommandSuccess(executionCommand);

        String openFileCollectionName = "accm_numberOfSuccessfulFileActionsUserIdFileHourly";
        String failedOpenFileCollectionName = "accm_numberOfFailedFileActionsUserIdFileHourly";
        List<AccumulatedAggregationFeatureRecord> accumulatedRecords = mongoTemplate.findAll(AccumulatedAggregationFeatureRecord.class, openFileCollectionName);

        //expected results
        int expectedAccumulatedRecordsSize = daysBackFrom - daysBackTo;
        int expectedAggregatedFeatureValuesSize = endHourOfDay - startHourOfDay;
        double expectedAggregatedFeatureValue = 6;

        Assert.assertTrue(accumulatedRecords.size() == expectedAccumulatedRecordsSize);
        assertRecords(startInstant, accumulatedRecords, expectedAggregatedFeatureValuesSize, expectedAggregatedFeatureValue);

        List<AccumulatedAggregationFeatureRecord> fileOpenFailedAccumulatedRecords = mongoTemplate.findAll(AccumulatedAggregationFeatureRecord.class, failedOpenFileCollectionName);
        //expected results
        expectedAggregatedFeatureValuesSize = 23;
        expectedAggregatedFeatureValue = 0;
        Assert.assertTrue(fileOpenFailedAccumulatedRecords.size() == expectedAccumulatedRecordsSize);
        assertRecords(startInstant, fileOpenFailedAccumulatedRecords, expectedAggregatedFeatureValuesSize, expectedAggregatedFeatureValue);
    }

    /**
     * Assert accumulation records result
     *
     * @param accumulatedRecords                  list of accumulated records
     * @param expectedAggregatedFeatureValuesSize num of aggregated features
     * @param expectedAggregatedFeatureValue      value for each feature aggregation
     */
    private void assertRecords(Instant startInstant, List<AccumulatedAggregationFeatureRecord> accumulatedRecords, int expectedAggregatedFeatureValuesSize, double expectedAggregatedFeatureValue) {
        long days = 0;
        for (AccumulatedAggregationFeatureRecord accumulatedRecord : accumulatedRecords) {
            Instant start = startInstant.plusSeconds((Duration.ofDays(days).getSeconds()));
            Instant end = start.plusSeconds((ACCUMUATION_DURATION.getSeconds()));
            days++;

            Collection<Double> aggregatedFeatureValues = accumulatedRecord.getAggregatedFeatureValuesAsList();
            Assert.assertEquals(expectedAggregatedFeatureValuesSize, aggregatedFeatureValues.size());

            Assert.assertEquals(start, accumulatedRecord.getStartInstant());
            Assert.assertEquals(end, accumulatedRecord.getEndInstant());

            for (Double aggregatedFeatureValue : aggregatedFeatureValues) {
                Assert.assertEquals(expectedAggregatedFeatureValue, aggregatedFeatureValue, 0.0);
            }
        }
    }
}
