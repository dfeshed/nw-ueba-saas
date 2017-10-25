package presidio.ade.processes.shell;


import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ContextConfiguration
public class AccumulateAggregationsApplicationTest extends EnrichedDataBaseAppTest {
    private static final int FIX_DURATION_STRATEGY = 86400;
    private static final int FEATURE_BUCKET_STRATEGY = 3600;
    private static final Duration ACCUMUATION_DURATION = Duration.ofDays(1);


    public static final String EXECUTION_COMMAND_FORMAT = "run --schema %s --start_date %s --end_date %s --fixed_duration_strategy %s --feature_bucket_strategy %s";

    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

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

    private Instant calculateRunningInstantParameter(Instant now, int daysBack){
        return TimeService.floorTime(now.minus(Duration.ofDays(daysBack)), ACCUMUATION_DURATION);
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
        Instant now = Instant.now();
        Instant startInstant = calculateRunningInstantParameter(now, daysBackFrom);
        Instant endInstant = calculateRunningInstantParameter(now, daysBackTo);
        Schema adeEventType = Schema.FILE;

        String executionCommand = String.format(EXECUTION_COMMAND_FORMAT, adeEventType, startInstant.toString(), endInstant.toString(), FIX_DURATION_STRATEGY, FEATURE_BUCKET_STRATEGY);

        IFileOperationGenerator fileOperationActionGenerator = new AdeFileOperationGeneratorTemplateFactory().createOpenFileOperationsGenerator();
        generateAndPersistFileEventData(Collections.singletonList(fileOperationActionGenerator), startHourOfDay, endHourOfDay, daysBackFrom, daysBackTo, "testUser", false);
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
     *
     */
    @Test
    public void each_aggr_file_feature_has_accumulation() throws GeneratorException {
        int startHourOfDay = 8;
        int endHourOfDay = 16;
        int daysBackFrom = 4;
        int daysBackTo = 2;
        Instant now = Instant.now();
        Instant startInstant = calculateRunningInstantParameter(now, daysBackFrom);
        Instant endInstant = calculateRunningInstantParameter(now, daysBackTo);
        Schema adeEventType = Schema.FILE;

        //generating also data for days that before the startInstant and after endInstant in order to see that they are not counted.
        generateAndPersistFileEventDataThatOutputAllAggrFeatures(startHourOfDay, endHourOfDay, daysBackFrom+1, daysBackTo-1);

        String executionCommand = String.format(EXECUTION_COMMAND_FORMAT, adeEventType, startInstant.toString(), endInstant.toString(), FIX_DURATION_STRATEGY, FEATURE_BUCKET_STRATEGY);
        executeAndAssertCommandSuccess(executionCommand);

        List<String> accumulationCollectionNames = mongoTemplate.getCollectionNames().stream().filter(s -> s.startsWith("accm")).collect(Collectors.toList());
        List<String> fileHourlyConfNames = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList().stream().map(aggregatedFeatureEventConf -> aggregatedFeatureEventConf.getName()).filter(s -> s.endsWith("FileHourly")).collect(Collectors.toList());

        //making sure that all conf names have accumulation collection
        Assert.assertEquals(fileHourlyConfNames.size(), accumulationCollectionNames.size());

        //making sure that all collection names are built of prefix 'accm_' and the conf name.
        for (String confName: fileHourlyConfNames){
            boolean isCollectionExist = accumulationCollectionNames.contains("accm_" + confName);
            Assert.assertTrue(confName + " does not have collection", isCollectionExist);
        }
    }

    public void generateAndPersistFileEventDataThatOutputAllAggrFeatures(int startHourOfDay, int endHourOfDay, int daysBackFrom, int daysBackTo) throws GeneratorException {
        //non admin users
        String contextIdPattern = "testUser[0-9]{3}"; //1000 different context ids: making sure that each file event generator will have different context id.
        generateAndPersistFileEventData(getAllFileOperationGenerator(), startHourOfDay, endHourOfDay, daysBackFrom, daysBackTo, contextIdPattern, false);

        //admin users
        contextIdPattern = "testAdminUser[0-9]{3}"; //1000 different context ids: making sure that each file event generator will have different context id.
        generateAndPersistFileEventData(getAllFileOperationGenerator(), startHourOfDay, endHourOfDay, daysBackFrom, daysBackTo, contextIdPattern, true);
    }

    /**
     * Get IFileOperationGenerator that cover all the features
     *
     * @return list of fileOperationGenerators
     * @throws GeneratorException
     */
    private List<IFileOperationGenerator> getAllFileOperationGenerator() throws GeneratorException {
        List<IFileOperationGenerator> fileOperationGenerators = new ArrayList<>();
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createLocalSharePermissionsChangeOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createFailedLocalSharePermissionsChangeOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createFailedOpenFileOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createOpenFileOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createFolderOpenFileOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createDeleteFileOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createRenameFileOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createFailedRenameFileOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createMoveFromSharedFileOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createMoveToSharedFileOperationsGenerator());

        return fileOperationGenerators;
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
