package presidio.ade.processes.shell;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.shell.BootShim;
import fortscale.utils.shell.BootShimConfig;
import fortscale.utils.test.category.ModuleTestCategory;
import fortscale.utils.time.TimeRange;
import fortscale.utils.time.TimeService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.shell.core.CommandResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.domain.record.accumulator.AccumulatedSmartRecord;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.domain.store.accumulator.smart.SmartAccumulationDataReader;
import presidio.ade.domain.store.smart.SmartDataStore;
import presidio.data.generators.common.GeneratorException;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by maria_dorohin on 8/24/17.
 */
@RunWith(SpringRunner.class)
@Category(ModuleTestCategory.class)
@ContextConfiguration
public class AccumulateSmartApplicationTest {

    @Autowired
    private BootShim bootShim;
    @Autowired
    private SmartDataStore smartDataStore;
    @Autowired
    private SmartAccumulationDataReader smartAccumulationDataReader;


    private static final String CONFIGURATION_NAME = "userId_hourly";
    private static final String CONTEXT_ID = "userId#testUser";
    private static final int NUM_OF_SMARTS_PER_HOUR = 5;


    private static final int DAYS_BACK_FROM = 5;
    private static final int DAYS_BACK_TO = 1;
    private static final Duration DURATION = Duration.ofDays(1);
    private static final Instant START_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(DAYS_BACK_FROM)), DURATION);
    private static final Instant END_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(DAYS_BACK_TO)), DURATION);

    public static final String EXECUTION_COMMAND = String.format("run --smart_record_conf_name %s --start_date %s --end_date %s --fixed_duration_strategy %s ", CONFIGURATION_NAME, START_DATE.toString(), END_DATE.toString(), 86400);

    @Import({AccumulateSmartApplicationConfigurationTest.class, AccumulateSmartServiceCommands.class, BootShimConfig.class
    })
    @Configuration
    protected static class AccumulateAggregationsTestConfig {

    }

    /**
     * Generate 2 events per hour along 24 hours for 2 days.
     * Operation type of all the events is "open"
     * execute sanity test command
     */
    //TODO: use smart generator after it will added
    @Test
    public void sanityTest() throws GeneratorException {
        generateAndPersistSanityData();
        CommandResult commandResult = bootShim.getShell().executeCommand(getSanityTestExecutionCommand());
        Assert.assertTrue(commandResult.isSuccess());
        assertSanityTest();
    }

    /**
     * Generates smarts
     */
    protected void generateAndPersistSanityData() {
        List<SmartRecord> smartRecords = new ArrayList<>();

        Instant start = START_DATE;

        for (int days = 1; days < DAYS_BACK_FROM - DAYS_BACK_TO; days++) {
            Duration smartDuration = Duration.ofHours(1);
            Instant end = start.plus(smartDuration);
            for (int smartIndex = 0; smartIndex < NUM_OF_SMARTS_PER_HOUR; smartIndex++) {
                TimeRange timeRange = new TimeRange(start, end);

                String featureName = "testFeatureName";
                double smartValue = 0.5;
                double smartScore = 0;
                List<FeatureScore> featureScores = Collections.emptyList();
                //The following if condition is to check the following 2 scenarios:
                // - days that one of the smart don't contains aggregation records
                // - a day that all of its smarts don't contain any aggregation record.
                List<AdeAggregationRecord> aggregationRecords = Collections.emptyList();
                if(days != 1 && smartIndex != days){
                    aggregationRecords = createAggregationRecord(start, end, featureName, smartScore + smartIndex * 10);
                }

                SmartRecord smartRecord = new SmartRecord(
                        timeRange, CONTEXT_ID, featureName, FixedDurationStrategy.HOURLY,
                        smartValue, smartScore, featureScores, aggregationRecords, null);
                smartRecords.add(smartRecord);
                start = end;
                end = end.plus(smartDuration);
            }
            start = TimeService.floorTime(start.plus(DURATION), DURATION);
        }
        smartDataStore.storeSmartRecords(CONFIGURATION_NAME, smartRecords);
    }

    /**
     * create AdeAggregationRecord
     * @param start start instant
     * @param end end instant
     * @param featureName feature name
     * @param featureValue value
     * @return List<AdeAggregationRecord>
     */
    private List<AdeAggregationRecord> createAggregationRecord(Instant start, Instant end, String featureName, double featureValue) {
        List<AdeAggregationRecord> adeAggregationRecords = new ArrayList<>();
        AdeAggregationRecord adeAggregationRecord = new AdeAggregationRecord(start, end, featureName, featureValue, "testConfName", Collections.singletonMap("userId", CONTEXT_ID), AggregatedFeatureType.SCORE_AGGREGATION);
        adeAggregationRecords.add(adeAggregationRecord);
        return adeAggregationRecords;
    }

    /**
     * Assert sanity test
     */
    protected void assertSanityTest() {
        List<AccumulatedSmartRecord> accumulatedSmartRecords = smartAccumulationDataReader.findAccumulatedEventsByContextIdAndStartTimeRange(CONFIGURATION_NAME, CONTEXT_ID, START_DATE, END_DATE);

        //assert num of accumulations
        Assert.assertTrue(accumulatedSmartRecords.size() == DAYS_BACK_FROM - DAYS_BACK_TO - 1);

        Instant start = START_DATE;
        //asserts for 2 days.
        for (AccumulatedSmartRecord accumulatedSmartRecord : accumulatedSmartRecords) {
            Instant end = start.plus(DURATION);
            Map<String, Map<Integer, Double>> aggregatedFeatureEventsValues = accumulatedSmartRecord.getAggregatedFeatureEventsValuesMap();

            if(accumulatedSmartRecord.getStartInstant().equals(START_DATE)){
                Assert.assertEquals(0, aggregatedFeatureEventsValues.size());
            } else {
                Assert.assertEquals(1, aggregatedFeatureEventsValues.size());
                aggregatedFeatureEventsValues.forEach((k, value) -> {
                    value.forEach((hour, score) -> {
                        //assert that score with zero did not store in map
                        Assert.assertFalse(score.equals(0));
                    });
                    //assert num of aggr (without aggr with score zero)
                    Assert.assertTrue(value.size() == NUM_OF_SMARTS_PER_HOUR - 2);
                });
            }

            //assert that score with zero store in activity time list.
            Assert.assertTrue(accumulatedSmartRecord.getActivityTime().size() == NUM_OF_SMARTS_PER_HOUR);
            start = end;
        }
    }


    protected String getSanityTestExecutionCommand() {
        return EXECUTION_COMMAND;
    }

}
