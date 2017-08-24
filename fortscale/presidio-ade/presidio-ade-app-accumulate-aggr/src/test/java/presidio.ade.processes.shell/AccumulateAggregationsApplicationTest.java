package presidio.ade.processes.shell;

import fortscale.common.general.Schema;
import fortscale.utils.time.TimeService;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.test.utils.generators.EnrichedSuccessfulFileOpenedGeneratorConfig;
import presidio.ade.test.utils.tests.EnrichedFileSourceBaseAppTest;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

@ContextConfiguration
public class AccumulateAggregationsApplicationTest extends EnrichedFileSourceBaseAppTest {
    private static final int DAYS_BACK_FROM = 3;
    private static final int DAYS_BACK_TO = 1;

    private static final Schema ADE_EVENT_TYPE = Schema.FILE;
    private static final Duration DURATION = Duration.ofDays(1);
    private static final Instant START_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(DAYS_BACK_FROM)), DURATION);
    private static final Instant END_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(DAYS_BACK_TO)), DURATION);


    public static final String EXECUTION_COMMAND = String.format("run --schema %s --start_date %s --end_date %s --fixed_duration_strategy %s --feature_bucket_strategy %s", ADE_EVENT_TYPE, START_DATE.toString(), END_DATE.toString(), 86400, 3600);

    @Autowired
    private EnrichedDataStore enrichedDataStore;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Import({EnrichedSourceSpringConfig.class, AccumulateAggregationsConfigurationTest.class, AccumulateServiceCommands.class, EnrichedSuccessfulFileOpenedGeneratorConfig.class})
    @Configuration
    protected static class AccumulateAggregationsTestConfig {

    }

    @Override
    protected String getContextTestExecutionCommand() {
        return EXECUTION_COMMAND;
    }

    @Override
    protected String getSanityTestExecutionCommand() {
        return EXECUTION_COMMAND;
    }

    /**
     * Generate 2 events per hour along 24 hours for 2 days.
     * Operation type of all the events is "open"
     * <p>
     * Expected result:
     * 2 accumulatedRecords - record per day.
     * 24 aggregatedFeatureValues for each record.
     * value of each aggregatedFeatureValues is 2 (2 opens files in each hour)
     */
    @Override
    protected void assertSanityTest() {

        String openFileCollectionName = "accm_numberOfSuccessfulFileActionsUserIdFileHourly";
        String failedOpenFileCollectionName = "accm_numberOfFailedFileActionsUserIdFileHourly";

        List<AccumulatedAggregationFeatureRecord> accumulatedRecords = mongoTemplate.findAll(AccumulatedAggregationFeatureRecord.class, openFileCollectionName);

        //expected results
        int expectedAccumulatedRecordsSize = DAYS_BACK_FROM - DAYS_BACK_TO;
        int expectedAggregatedFeatureValuesSize = 24;
        double expectedAggregatedFeatureValue = 2;

        Assert.assertTrue(accumulatedRecords.size() == expectedAccumulatedRecordsSize);
        assertRecords(accumulatedRecords, expectedAggregatedFeatureValuesSize, expectedAggregatedFeatureValue);

        List<AccumulatedAggregationFeatureRecord> fileOpenFailedAccumulatedRecords = mongoTemplate.findAll(AccumulatedAggregationFeatureRecord.class, failedOpenFileCollectionName);
        //expected results
        expectedAggregatedFeatureValuesSize = 24;
        expectedAggregatedFeatureValue = 0;
        Assert.assertTrue(fileOpenFailedAccumulatedRecords.size() == expectedAccumulatedRecordsSize);
        assertRecords(fileOpenFailedAccumulatedRecords, expectedAggregatedFeatureValuesSize, expectedAggregatedFeatureValue);
    }

    /**
     * Assert accumulation records result
     *
     * @param accumulatedRecords                  list of accumulated records
     * @param expectedAggregatedFeatureValuesSize num of aggregated features
     * @param expectedAggregatedFeatureValue      value for each feature aggregation
     */
    private void assertRecords(List<AccumulatedAggregationFeatureRecord> accumulatedRecords, int expectedAggregatedFeatureValuesSize, double expectedAggregatedFeatureValue) {
        long days = 0;
        for (AccumulatedAggregationFeatureRecord accumulatedRecord : accumulatedRecords) {
            Duration duration = Duration.ofDays(days);
            Instant start = START_DATE.plusSeconds((duration.getSeconds()));
            Instant end = start.plusSeconds((DURATION.getSeconds()));
            days++;

            Collection<Double> aggregatedFeatureValues = accumulatedRecord.getAggregatedFeatureValuesAsList();
            Assert.assertTrue(aggregatedFeatureValues.size() == expectedAggregatedFeatureValuesSize);

            Assert.assertTrue(accumulatedRecord.getStartInstant().equals(start));
            Assert.assertTrue(accumulatedRecord.getEndInstant().equals(end));

            for (Double aggregatedFeatureValue : aggregatedFeatureValues) {
                Assert.assertTrue(aggregatedFeatureValue == expectedAggregatedFeatureValue);
            }
        }
    }

}