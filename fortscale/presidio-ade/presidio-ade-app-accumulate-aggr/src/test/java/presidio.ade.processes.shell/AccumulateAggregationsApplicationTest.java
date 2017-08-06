package presidio.ade.processes.shell;

import fortscale.common.general.Schema;
import fortscale.utils.time.TimeService;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.shell.core.CommandResult;
import org.springframework.test.context.ContextConfiguration;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.test.utils.tests.EnricheSourceBaseAppTest;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@ContextConfiguration
public class AccumulateAggregationsApplicationTest extends EnricheSourceBaseAppTest {
    private static final int DAYS_BACK_FROM = 3;
    private static final int DAYS_BACK_TO = 1;

    private static final Schema ADE_EVENT_TYPE = Schema.DLPFILE;
    private static final Duration DURATION = Duration.ofDays(1);
    private static final Instant START_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(DAYS_BACK_FROM)), DURATION);
    private static final Instant END_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(DAYS_BACK_TO)), DURATION);

    public static final String EXECUTION_COMMAND = String.format("run  --schema %s --start_date %s --end_date %s --fixed_duration_strategy %s --feature_bucket_strategy %s", ADE_EVENT_TYPE, START_DATE.toString(), END_DATE.toString(), 86400, 3600);

    @Autowired
    private EnrichedDataStore enrichedDataStore;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Import({AccumulateAggregationsConfigurationTest.class,EnricheSourceBaseAppTest.EnricheSourceSpringConfig.class })
    @Configuration
    protected static class AccumulateAggregationsTestConfig {

    }

    @Override
    protected String getContextTestExecutionCommand() {
        return EXECUTION_COMMAND;
    }

    /**
     * Generate 2 events per hour along 24 hours for 2 days.
     * Operation type of all the events is "open"
     * <p>
     * Expected result:
     * 2 accumulatedRecords - record per each day.
     * 24 aggregatedFeatureValues for each record.
     * value of each aggregatedFeatureValues is 2 (2 opens files in each hour)
     */
    @Override
    protected void assertSanityTest() {
        CommandResult commandResult = bootShim.getShell().executeCommand(EXECUTION_COMMAND);
        Assert.assertTrue(commandResult.isSuccess());

        String openFileCollectionName = "accm_number_of_opened_files_normalized_username_hourly_dlpfile";

        List<AccumulatedAggregationFeatureRecord> accumulatedRecords = mongoTemplate.findAll(AccumulatedAggregationFeatureRecord.class, openFileCollectionName);

        //expected results
        int expectedAccumulatedRecordsSize = DAYS_BACK_FROM - DAYS_BACK_TO;
        int expectedAggregatedFeatureValuesSize = 24;
        double expectedAggregatedFeatureValue = 2;

        Assert.assertTrue(accumulatedRecords.size() == expectedAccumulatedRecordsSize);

        long days = 0;
        for (AccumulatedAggregationFeatureRecord accumulatedRecord : accumulatedRecords) {
            Duration duration = Duration.ofDays(days);
            Instant start = START_DATE.plusSeconds((duration.getSeconds()));
            Instant end = start.plusSeconds((DURATION.getSeconds()));
            days++;

            List<Double> aggregatedFeatureValues = accumulatedRecord.getAggregatedFeatureValues();
            Assert.assertTrue(aggregatedFeatureValues.size() == expectedAggregatedFeatureValuesSize);

            Assert.assertTrue(accumulatedRecord.getStartInstant().equals(start));
            Assert.assertTrue(accumulatedRecord.getEndInstant().equals(end));

            for (Double aggregatedFeatureValue : aggregatedFeatureValues) {
                Assert.assertTrue(aggregatedFeatureValue == expectedAggregatedFeatureValue);
            }
        }
    }

}