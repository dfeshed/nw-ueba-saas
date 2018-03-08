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
import org.springframework.test.context.ContextConfiguration;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;
import presidio.ade.test.utils.tests.EnrichedDataBaseAppTest;
import presidio.data.ade.AdeFileOperationGeneratorTemplateFactory;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.fileop.IFileOperationGenerator;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
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
        expectedAggregatedFeatureValuesSize =  endHourOfDay - startHourOfDay;
        expectedAggregatedFeatureValue = 0;
        Assert.assertTrue(fileOpenFailedAccumulatedRecords.size() == expectedAccumulatedRecordsSize);
        assertRecords(startInstant, fileOpenFailedAccumulatedRecords, expectedAggregatedFeatureValuesSize, expectedAggregatedFeatureValue);
    }

    private List<String> getAccumulationCollectionNames(){
        return mongoTemplate.getCollectionNames().stream().filter(s -> s.startsWith("accm")).collect(Collectors.toList());
    }

    private List<String> filterSchemaConfNames(String schemaConfNameSuffix){
        return aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList().stream().map(aggregatedFeatureEventConf -> aggregatedFeatureEventConf.getName()).filter(s -> s.endsWith(schemaConfNameSuffix)).collect(Collectors.toList());
    }

    /**
     *
     */
    @Test
    public void each_aggr_file_feature_has_accumulation_test() throws GeneratorException {
        int startHourOfDay = 8;
        int endHourOfDay = 16;
        int daysBackFrom = 4;
        int daysBackTo = 2;
        Instant now = Instant.now();
        Instant startInstant = calculateRunningInstantParameter(now, daysBackFrom);
        Instant endInstant = calculateRunningInstantParameter(now, daysBackTo);
        Schema adeEventType = Schema.FILE;

        //generating also data for days that before the startInstant and after endInstant in order to see that they are not counted.
        generateAndPersistFileEventDataThatOutputAllAggrFeatures(startHourOfDay, endHourOfDay, daysBackFrom + 1, daysBackTo - 1);

        String executionCommand = String.format(EXECUTION_COMMAND_FORMAT, adeEventType, startInstant.toString(), endInstant.toString(), FIX_DURATION_STRATEGY, FEATURE_BUCKET_STRATEGY);
        executeAndAssertCommandSuccess(executionCommand);

        //expected results
        int expectedAccumulatedRecordsSize = daysBackFrom - daysBackTo;
        int expectedAggregatedFeatureValuesSize = endHourOfDay - startHourOfDay;
        List<Double> expectedAggregatedFeatureValues = Arrays.asList(0d,6d);
        List<Integer> optionalExpectedNumOfDifferentUsersInCollection = Arrays.asList(2, 4, 16);
        assert_each_aggr_feature_has_accumulation("FileHourly", startInstant, expectedAccumulatedRecordsSize,
                expectedAggregatedFeatureValuesSize, expectedAggregatedFeatureValues, optionalExpectedNumOfDifferentUsersInCollection);
    }

    private void assert_each_aggr_feature_has_accumulation(String schemaConfNameSuffix, Instant startInstant,
                                                           int expectedAccumulatedRecordsSize, int expectedAggregatedFeatureValuesSize,
                                                           List<Double> expectedAggregatedFeatureValues, List<Integer> optionalExpectedNumOfDifferentUsersInCollection) {
        List<String> accumulationCollectionNames = getAccumulationCollectionNames();
        List<String> fileHourlyConfNames = filterSchemaConfNames(schemaConfNameSuffix);

        //making sure that all conf names have accumulation collection
        Assert.assertEquals(fileHourlyConfNames.size(), accumulationCollectionNames.size());

        //making sure that all collection names are built of prefix 'accm_' and the conf name.
        for (String confName: fileHourlyConfNames){
            boolean isCollectionExist = accumulationCollectionNames.contains("accm_" + confName);
            Assert.assertTrue(confName + " does not have collection", isCollectionExist);
        }


        for (String collectionName: accumulationCollectionNames){
            List<AccumulatedAggregationFeatureRecord> accumulatedRecords = mongoTemplate.findAll(AccumulatedAggregationFeatureRecord.class, collectionName);
            Map<String,List<AccumulatedAggregationFeatureRecord>> contextIdToAccumulatedRecords = new HashMap<>();
            for (AccumulatedAggregationFeatureRecord record: accumulatedRecords){
                List<AccumulatedAggregationFeatureRecord> contextRecordsList = contextIdToAccumulatedRecords.get(record.getContextId());
                if(contextRecordsList == null){
                    contextRecordsList = new ArrayList<>();
                    contextIdToAccumulatedRecords.put(record.getContextId(),contextRecordsList);
                }
                contextRecordsList.add(record);
            }

            if(!optionalExpectedNumOfDifferentUsersInCollection.contains(contextIdToAccumulatedRecords.size())){
                Assert.fail(String.format("number of different users in collection %s is %s (%s), which is not one of the expected values (%s)",
                        collectionName, contextIdToAccumulatedRecords.size(), contextIdToAccumulatedRecords.keySet(), optionalExpectedNumOfDifferentUsersInCollection));
            }

            for (List<AccumulatedAggregationFeatureRecord> userAccumulatedRecords: contextIdToAccumulatedRecords.values()) {
                Assert.assertTrue(userAccumulatedRecords.size() == expectedAccumulatedRecordsSize);
                assertRecords(startInstant, userAccumulatedRecords, expectedAggregatedFeatureValuesSize, expectedAggregatedFeatureValues);
            }
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
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createDownloadFileOperationsGenerator());

        return fileOperationGenerators;
    }

    /**
     * Assert accumulation records result
     *
     * @param accumulatedRecords                  list of accumulated records
     * @param expectedAggregatedFeatureValuesSize num of aggregated features
     * @param expectedAggregatedFeatureValue     expected feature value.
     */
    private void assertRecords(Instant startInstant, List<AccumulatedAggregationFeatureRecord> accumulatedRecords, int expectedAggregatedFeatureValuesSize, Double expectedAggregatedFeatureValue) {
        assertRecords(startInstant,accumulatedRecords,expectedAggregatedFeatureValuesSize,Collections.singletonList(expectedAggregatedFeatureValue));
    }
    /**
     * Assert accumulation records result
     *
     * @param accumulatedRecords                  list of accumulated records
     * @param expectedAggregatedFeatureValuesSize num of aggregated features
     * @param expectedAggregatedFeatureValues     list of optional expected values. Meaning that one of them should be equal to the actual value.
     */
    private void assertRecords(Instant startInstant, List<AccumulatedAggregationFeatureRecord> accumulatedRecords, int expectedAggregatedFeatureValuesSize, List<Double> expectedAggregatedFeatureValues) {
        long days = 0;
        Set<Double> actualFeatureValues = new HashSet<>();
        accumulatedRecords.forEach(record -> actualFeatureValues.addAll(record.getAggregatedFeatureValuesAsList()));
        Assert.assertEquals(String.format("Got few values (%s), while it is expected that each hour will have the same value", actualFeatureValues),
                1,actualFeatureValues.size());
        Double actualFeatureValue = actualFeatureValues.iterator().next();
        if(!expectedAggregatedFeatureValues.contains(actualFeatureValue)){
            Assert.fail(String.format("actual: %s, is not one of the optional expected value %s", actualFeatureValue, expectedAggregatedFeatureValues));
        }
        for (AccumulatedAggregationFeatureRecord accumulatedRecord : accumulatedRecords) {
            Instant start = startInstant.plusSeconds((Duration.ofDays(days).getSeconds()));
            Instant end = start.plusSeconds((ACCUMUATION_DURATION.getSeconds()));
            days++;

            Collection<Double> aggregatedFeatureValues = accumulatedRecord.getAggregatedFeatureValuesAsList();
            Assert.assertEquals(expectedAggregatedFeatureValuesSize, aggregatedFeatureValues.size());

            Assert.assertEquals(start, accumulatedRecord.getStartInstant());
            Assert.assertEquals(end, accumulatedRecord.getEndInstant());
        }
    }
}
