package presidio.ade.smart;

import com.google.common.collect.Lists;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.ml.model.SMARTMaxValuesModel;
import fortscale.ml.model.SMARTValuesPriorModel;
import fortscale.ml.model.SmartWeightsModel;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStoreConfig;
import fortscale.smart.record.conf.ClusterConf;
import fortscale.smart.record.conf.SmartRecordConf;
import fortscale.smart.record.conf.SmartRecordConfService;
import fortscale.utils.store.record.StoreMetadataProperties;
import fortscale.utils.test.category.ModuleTestCategory;
import fortscale.utils.time.TimeRange;
import fortscale.utils.time.TimeService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.aggregated.SmartAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.domain.store.aggr.AggregatedDataStore;
import presidio.ade.smart.config.SmartApplicationConfigurationTest;
import presidio.ade.test.utils.generators.AdeAggregationRecordHourlyGenerator;
import presidio.ade.test.utils.generators.ScoredFeatureAggregationRecordHourlyGenerator;
import presidio.ade.test.utils.tests.BaseAppTest;
import presidio.data.generators.common.*;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Category(ModuleTestCategory.class)
@ContextConfiguration
public class SmartApplicationTest extends BaseAppTest {
    private static final String EXECUTION_COMMAND = "process --smart_record_conf_name %s --start_date %s --end_date %s";
    private static final double avgFeatureValueForLowAnomaliesUser = 0.5;
    private static final int numOfFeaturesInGroups = 5;

    private IMapGenerator aggregatedFeatureToScoreGenerator;
    private IMapGenerator aggregatedFeatureToValueGenerator;

    @Autowired
    private AggregatedDataStore aggregatedDataStore;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
    @Autowired
    private ModelsCacheService modelsCacheService;
    @Autowired
    private SmartRecordConfService smartRecordConfService;

    @Override
    protected String getContextTestExecutionCommand() {
        Instant startDate = TimeService.floorTime(Instant.now().minus(Duration.ofDays(30)), Duration.ofDays(1));
        Instant endDate = startDate.plus(Duration.ofDays(2));
        return String.format(EXECUTION_COMMAND, "userId_hourly", startDate.toString(), endDate.toString());
    }

    @Before
    public void setUp() {
        mongoTemplate.getCollectionNames().forEach(collection -> mongoTemplate.dropCollection(collection));
        modelsCacheService.resetCache();
    }

    /**
     * 1. Test that each configured feature which has the same feature value, influence the score in the same manner if it has the same weight.
     * 2. Test that feature with higher weight has higher influence over feature with lower weight when both have the same featureValue.
     */
    @Test
    public void lowAnomaliesUserTest() throws GeneratorException {
        int daysBackFrom = 30;
        // Duration that covers all 42 features: 2 days 01:00 - 22:00.
        int durationOfProcess = 2;
        int daysBackTo = daysBackFrom - durationOfProcess;
        int startHourOfDay = 1;
        int endHourOfDay = 22;
        Instant startDate = TimeService.floorTime(Instant.now().minus(Duration.ofDays(daysBackFrom)), Duration.ofDays(1));
        String contextId = "user1";
        TreeMap<Double, List<String>> weightToFeaturesSortedMap = createFeaturesGroups();
        createWeightModel(startDate, weightToFeaturesSortedMap);
        createLowAnomaliesUserSmartValuesModel(startDate, "userId", contextId, weightToFeaturesSortedMap);
        createPriorModelForLowAnomaliesUser(startDate, weightToFeaturesSortedMap);
        List<String> contextIds = Collections.singletonList(contextId);
        createSingleHighValueAggregatedFeatureGenerators();
        TimeRange timeRange = generateAggregatedFeatureEventConf(daysBackFrom, daysBackTo, startHourOfDay, endHourOfDay, contextIds);
        String command = String.format(EXECUTION_COMMAND, "userId_hourly", timeRange.getStart().toString(), timeRange.getEnd().toString());
        executeAndAssertCommandSuccess(command);
        List<SmartRecord> smartRecords = mongoTemplate.findAll(SmartRecord.class, "smart_userId_hourly");
        AssertSmartRecords(smartRecords, weightToFeaturesSortedMap);
    }

    /**
     * Test over 3 users for period of 6 days.
     */
    @Test
    public void lowAnomaliesUsersTest() throws GeneratorException {
        int daysBackFrom = 30;
        // Duration that covers all 42 features 3 times: 2 days 01:00 - 22:00.
        int durationOfProcess = 6;
        int daysBackTo = daysBackFrom - durationOfProcess;
        int startHourOfDay = 1;
        int endHourOfDay = 22;
        Instant startDate = TimeService.floorTime(Instant.now().minus(Duration.ofDays(daysBackFrom)), Duration.ofDays(1));
        Instant endDate = startDate.plus(Duration.ofDays(durationOfProcess));
        List<String> contextIds = new ArrayList<>();
        contextIds.add("user1");
        contextIds.add("user2");
        contextIds.add("user3");
        TreeMap<Double, List<String>> weightToFeaturesSortedMap = createFeaturesGroups();
        createSingleHighValueAggregatedFeatureGenerators();
        TimeRange timeRange = generateAggregatedFeatureEventConf(daysBackFrom, daysBackTo, startHourOfDay, endHourOfDay, contextIds);
        // Generate models for users:
        createModelsForLowAnomaliesUsers(startDate, endDate, contextIds, weightToFeaturesSortedMap);
        String command = String.format(EXECUTION_COMMAND, "userId_hourly", timeRange.getStart().toString(), timeRange.getEnd().toString());
        executeAndAssertCommandSuccess(command);
        List<SmartRecord> smartRecords = mongoTemplate.findAll(SmartRecord.class, "smart_userId_hourly");
        contextIds.forEach(contextId -> {
            List<SmartRecord> contextIdSmartRecords = smartRecords.stream().filter(smart -> smart.getContextId().equals("userId#" + contextId)).collect(Collectors.toList());
            AssertSmartRecords(contextIdSmartRecords, weightToFeaturesSortedMap);
        });
        assertSmartsBetweenContexts(smartRecords, contextIds.size(), startDate, endDate, durationOfProcess, startHourOfDay, endHourOfDay, weightToFeaturesSortedMap);
    }

    /**
     * Test that different sets of features, where each feature has different weight in set and same weights between sets, influence in the same manner.
     * Create smarts, where each smart contains set of features with featureValue greater than 0 and different weights.
     * Each hour generator generate next set of features.
     * Expected get same smart score.
     */
    @Test
    public void setOfFeaturesWithDiffWeightTest() throws GeneratorException {
        int daysBackFrom = 30;
        // Duration that covers all 42 features 3 times: 2 days 01:00 - 22:00.
        int durationOfProcess = 2;
        int daysBackTo = daysBackFrom - durationOfProcess;
        int startHourOfDay = 1;
        int endHourOfDay = 4;
        Instant startDate = TimeService.floorTime(Instant.now().minus(Duration.ofDays(daysBackFrom)), Duration.ofDays(1));
        Instant endDate = startDate.plus(Duration.ofDays(durationOfProcess));
        List<String> contextIds = new ArrayList<>();
        contextIds.add("user1");
        contextIds.add("user2");
        TreeMap<Double, List<String>> weightToFeaturesSortedMap = createFeaturesGroups();
        setOfFeaturesWithDiffWeightsAggrFeatureGenerators(weightToFeaturesSortedMap, 20.0);
        TimeRange timeRange = generateAggregatedFeatureEventConf(daysBackFrom, daysBackTo, startHourOfDay, endHourOfDay, contextIds);
        // Generate models for users:
        createModelsForLowAnomaliesUsers(startDate, endDate, contextIds, weightToFeaturesSortedMap);
        String command = String.format(EXECUTION_COMMAND, "userId_hourly", timeRange.getStart().toString(), timeRange.getEnd().toString());
        executeAndAssertCommandSuccess(command);
        List<SmartRecord> smartRecords = mongoTemplate.findAll(SmartRecord.class, "smart_userId_hourly");
        Assert.assertEquals(smartRecords.size(), contextIds.size() * (endHourOfDay - startHourOfDay) * durationOfProcess);
        Assert.assertEquals(smartRecords.size(), contextIds.size() * (endHourOfDay - startHourOfDay) * durationOfProcess);
        Map<Integer, List<SmartRecord>> smartAggregationRecordsSizeToSmartRecords = smartRecords.stream().collect(Collectors.groupingBy(smartRecord -> (int)(smartRecord.getSmartValue()*1000)));
        smartAggregationRecordsSizeToSmartRecords.values().forEach(smartRecordList -> {
            Double expectedScore = smartRecordList.get(0).getScore();
            int expectedSmartAggregationRecordsSize = smartRecordList.get(0).getSmartAggregationRecords().size();
            Assert.assertTrue(expectedScore > 0);
            Assert.assertTrue(smartRecordList.stream().allMatch(smart -> smart.getScore().equals(expectedScore) && smart.getSmartAggregationRecords().size() == expectedSmartAggregationRecordsSize));
        });
    }

    /**
     * Test smarts, where no Weight model exist.
     * Expected: smartScore = 0, smartValue = 0.
     */
    @Test
    public void NoWeightModelTest() throws GeneratorException {
        int daysBackFrom = 30;
        // Duration that covers all 42 features 3 times: 2 days 01:00 - 22:00.
        int durationOfProcess = 2;
        int daysBackTo = daysBackFrom - durationOfProcess;
        int startHourOfDay = 1;
        int endHourOfDay = 4;
        Instant startDate = TimeService.floorTime(Instant.now().minus(Duration.ofDays(daysBackFrom)), Duration.ofDays(1));
        String contextId = "user1";
        List<String> contextIds = Collections.singletonList(contextId);
        TreeMap<Double, List<String>> weightToFeaturesSortedMap = createFeaturesGroups();
        setOfFeaturesWithDiffWeightsAggrFeatureGenerators(weightToFeaturesSortedMap, 20.0);
        TimeRange timeRange = generateAggregatedFeatureEventConf(daysBackFrom, daysBackTo, startHourOfDay, endHourOfDay, contextIds);
        // Generate models for users:
        createLowAnomaliesUserSmartValuesModel(startDate, "userId", contextId, weightToFeaturesSortedMap);
        createPriorModelForLowAnomaliesUser(startDate, weightToFeaturesSortedMap);
        String command = String.format(EXECUTION_COMMAND, "userId_hourly", timeRange.getStart().toString(), timeRange.getEnd().toString());
        executeAndAssertCommandSuccess(command);
        List<SmartRecord> smartRecords = mongoTemplate.findAll(SmartRecord.class, "smart_userId_hourly");
        Double expectedScore = 0.0;
        Double expectedSmartValue = 0.0;
        Assert.assertTrue(smartRecords.stream().allMatch(smart -> smart.getScore().equals(expectedScore) && smart.getSmartValue() == expectedSmartValue));
        Assert.assertEquals(smartRecords.size(), contextIds.size() * (endHourOfDay - startHourOfDay) * durationOfProcess);
    }

    /**
     * Test smarts, where no SmartValues model exist.
     * Expected: smartScore = 0, smartValue > 0.
     */
    @Test
    public void NoSmartValuesModelsExistButPriorDoesExistTest() throws GeneratorException {
        int daysBackFrom = 30;
        // Duration that covers all 42 features 3 times: 2 days 01:00 - 22:00.
        int durationOfProcess = 2;
        int daysBackTo = daysBackFrom - durationOfProcess;
        int startHourOfDay = 1;
        int endHourOfDay = 4;
        Instant startDate = TimeService.floorTime(Instant.now().minus(Duration.ofDays(daysBackFrom)), Duration.ofDays(1));
        String contextId = "user1";
        List<String> contextIds = Collections.singletonList(contextId);
        TreeMap<Double, List<String>> weightToFeaturesSortedMap = createFeaturesGroups();
        setOfFeaturesWithDiffWeightsAggrFeatureGenerators(weightToFeaturesSortedMap, 20.0);
        TimeRange timeRange = generateAggregatedFeatureEventConf(daysBackFrom, daysBackTo, startHourOfDay, endHourOfDay, contextIds);
        // Generate models for users:
        createWeightModel(startDate, weightToFeaturesSortedMap);
        createPriorModelForLowAnomaliesUser(startDate, weightToFeaturesSortedMap);
        String command = String.format(EXECUTION_COMMAND, "userId_hourly", timeRange.getStart().toString(), timeRange.getEnd().toString());
        executeAndAssertCommandSuccess(command);
        List<SmartRecord> smartRecords = mongoTemplate.findAll(SmartRecord.class, "smart_userId_hourly");
        Assert.assertTrue(smartRecords.stream().allMatch(smart -> smart.getScore() > 0.0 && smart.getSmartValue() > 0.0));
        Assert.assertEquals(smartRecords.size(), contextIds.size() * (endHourOfDay - startHourOfDay) * durationOfProcess);
    }

    /**
     * Test smarts, where no Prior model exist.
     * Expected: smartScore = 0, smartValue > 0.
     */
    @Test
    public void NoPriorModeTest() throws GeneratorException {
        int daysBackFrom = 30;
        // Duration that covers all 42 features 3 times: 2 days 01:00 - 22:00.
        int durationOfProcess = 2;
        int daysBackTo = daysBackFrom - durationOfProcess;
        int startHourOfDay = 1;
        int endHourOfDay = 4;
        Instant startDate = TimeService.floorTime(Instant.now().minus(Duration.ofDays(daysBackFrom)), Duration.ofDays(1));
        String contextId = "user1";
        List<String> contextIds = Collections.singletonList(contextId);
        TreeMap<Double, List<String>> weightToFeaturesSortedMap = createFeaturesGroups();
        setOfFeaturesWithDiffWeightsAggrFeatureGenerators(weightToFeaturesSortedMap, 20.0);
        TimeRange timeRange = generateAggregatedFeatureEventConf(daysBackFrom, daysBackTo, startHourOfDay, endHourOfDay, contextIds);
        // Generate models for users:
        createWeightModel(startDate, weightToFeaturesSortedMap);
        createLowAnomaliesUserSmartValuesModel(startDate, "userId", contextId, weightToFeaturesSortedMap);
        String command = String.format(EXECUTION_COMMAND, "userId_hourly", timeRange.getStart().toString(), timeRange.getEnd().toString());
        executeAndAssertCommandSuccess(command);
        List<SmartRecord> smartRecords = mongoTemplate.findAll(SmartRecord.class, "smart_userId_hourly");
        Double expectedScore = 0.0;
        Assert.assertTrue(smartRecords.stream().allMatch(smart -> smart.getScore().equals(expectedScore) && smart.getSmartValue() > 0.0));
        Assert.assertEquals(smartRecords.size(), contextIds.size() * (endHourOfDay - startHourOfDay) * durationOfProcess);
    }

    /**
     * Test smarts time range.
     * Generate Fs and Ps on 1-6 days.
     * Run smart app on 3-4 days period.
     */
    @Test
    public void smartExpectedTimeTest() throws GeneratorException {
        int daysBackFrom = 30;
        int durationOfProcess = 6;
        int daysBackTo = daysBackFrom - durationOfProcess;
        int startHourOfDay = 1;
        int endHourOfDay = 7;
        Instant startDate = TimeService.floorTime(Instant.now().minus(Duration.ofDays(daysBackFrom)), Duration.ofDays(1));
        Instant endDate = startDate.plus(Duration.ofDays(durationOfProcess));
        List<String> contextIds = new ArrayList<>();
        contextIds.add("user1");
        TreeMap<Double, List<String>> weightToFeaturesSortedMap = createFeaturesGroups();
        setOfFeaturesWithDiffWeightsAggrFeatureGenerators(weightToFeaturesSortedMap, 20.0);
        TimeRange timeRange = generateAggregatedFeatureEventConf(daysBackFrom, daysBackTo, startHourOfDay, endHourOfDay, contextIds);
        // Generate models for users:
        createModelsForLowAnomaliesUsers(startDate, endDate, contextIds, weightToFeaturesSortedMap);
        // Execute command on smaller time range.
        int numOfDaysToReduce = 2;
        Instant start = timeRange.getStart().plus(Duration.ofDays(numOfDaysToReduce));
        Instant end = timeRange.getEnd().minus(Duration.ofDays(numOfDaysToReduce));
        String command = String.format(EXECUTION_COMMAND, "userId_hourly", start, end);
        executeAndAssertCommandSuccess(command);
        List<SmartRecord> smartRecords = mongoTemplate.findAll(SmartRecord.class, "smart_userId_hourly");
        Assert.assertEquals(smartRecords.size(), contextIds.size() * (endHourOfDay - startHourOfDay) * (durationOfProcess - (2 * numOfDaysToReduce)));
        Instant smartRecordStart = smartRecords.stream().min(Comparator.comparing(SmartRecord::getStartInstant)).get().getStartInstant();
        Instant smartRecordEnd = smartRecords.stream().max(Comparator.comparing(SmartRecord::getEndInstant)).get().getStartInstant();
        Assert.assertEquals(start, smartRecordStart);
        Assert.assertEquals(end, smartRecordEnd.plus(Duration.ofHours(1)));
        Map<Integer, List<SmartRecord>> smartAggregationRecordsSizeToSmartRecords = smartRecords.stream().collect(Collectors.groupingBy(smartRecord -> (int)(smartRecord.getSmartValue()*1000)));
        smartAggregationRecordsSizeToSmartRecords.values().forEach(smartRecordList -> {
            Double expectedScore = smartRecordList.get(0).getScore();
            int numOfSmartAggregationRecords = smartRecordList.get(0).getSmartAggregationRecords().size();
            Assert.assertTrue(expectedScore > 0);
            Assert.assertTrue(smartRecordList.stream().allMatch(smart -> smart.getScore().equals(expectedScore) && smart.getSmartAggregationRecords().size() == numOfSmartAggregationRecords));
        });
    }

    /**
     * Test smarts threshold.
     * Generate Fs and Ps for 2 users, where score of user1 higher than threshold and score of user2 lower than threshold.
     * Expect: get smarts for 2 users, where smart of user1 has all relevant aggregatedFeatures and smart of user2 has not aggregatedFeatures.
     */
    @Test
    public void smartExpectedThresholdTest() throws GeneratorException {
        int daysBackFrom = 30;
        int durationOfProcess = 2;
        int daysBackTo = daysBackFrom - durationOfProcess;
        int startHourOfDay = 1;
        int endHourOfDay = 3;
        Instant startDate = TimeService.floorTime(Instant.now().minus(Duration.ofDays(daysBackFrom)), Duration.ofDays(1));
        Instant endDate = startDate.plus(Duration.ofDays(durationOfProcess));
        TreeMap<Double, List<String>> weightToFeaturesSortedMap = createFeaturesGroups();
        List<String> passThresholdContexts = new ArrayList<>();
        passThresholdContexts.add("user1");
        setOfFeaturesWithDiffWeightsAggrFeatureGenerators(weightToFeaturesSortedMap, 11);
        TimeRange timeRange = generateAggregatedFeatureEventConf(daysBackFrom, daysBackTo, startHourOfDay, endHourOfDay, passThresholdContexts);
        List<String> doesNotPassThresholdContexts = new ArrayList<>();
        doesNotPassThresholdContexts.add("user2");
        setOfFeaturesWithDiffWeightsAggrFeatureGenerators(weightToFeaturesSortedMap, 10.0);
        generateAggregatedFeatureEventConf(daysBackFrom, daysBackTo, startHourOfDay, endHourOfDay, doesNotPassThresholdContexts);
        // Generate models for users:
        createModelsForLowAnomaliesUsers(startDate, endDate, passThresholdContexts, weightToFeaturesSortedMap);
        createModelsForLowAnomaliesUsers(startDate, endDate, doesNotPassThresholdContexts, weightToFeaturesSortedMap);
        String command = String.format(EXECUTION_COMMAND, "userId_hourly", timeRange.getStart(), timeRange.getEnd());
        executeAndAssertCommandSuccess(command);
        List<SmartRecord> smartRecords = mongoTemplate.findAll(SmartRecord.class, "smart_userId_hourly");

        for (SmartRecord smartRecord : smartRecords) {
            if (smartRecord.getContextId().equals("userId#user1")) {
                Assert.assertTrue(smartRecord.getScore() > 0);
                Assert.assertTrue(smartRecord.getSmartValue() > 0);
            } else if (smartRecord.getContextId().equals("userId#user2")) {
                Assert.assertEquals(0, smartRecord.getScore(), 0);
                Assert.assertEquals(0, smartRecord.getSmartValue(), 0);
            }
        }

        Assert.assertEquals(smartRecords.stream().filter(smart -> smart.getContextId().equals("userId#user1")).count(), passThresholdContexts.size() * (endHourOfDay - startHourOfDay) * durationOfProcess);
        Assert.assertEquals(smartRecords.stream().filter(smart -> smart.getContextId().equals("userId#user2")).count(), doesNotPassThresholdContexts.size() * (endHourOfDay - startHourOfDay) * durationOfProcess);
    }

    /**
     * Create models for normal users with low anomalies.
     * Create new model each 2 days due to maxDiffBetweenCachedModelAndEvent.
     *
     * @param startDate                 start date.
     * @param endDate                   end date.
     * @param contextIds                create SmartValuesModel for given context ids.
     * @param weightToFeaturesSortedMap weight to features sorted map.
     */
    private void createModelsForLowAnomaliesUsers(Instant startDate, Instant endDate, List<String> contextIds, TreeMap<Double, List<String>> weightToFeaturesSortedMap) {
        Instant start = startDate;

        while (start.isBefore(endDate)) {
            createWeightModel(start, weightToFeaturesSortedMap);
            createPriorModelForLowAnomaliesUser(start, weightToFeaturesSortedMap);

            for (String contextId : contextIds) {
                createLowAnomaliesUserSmartValuesModel(start, "userId", contextId, weightToFeaturesSortedMap);
            }

            start = start.plus(Duration.ofDays(2));
        }
    }

    /**
     * Compare smarts between contextIds.
     *
     * @param smartRecords              smart records of all contexts.
     * @param numOfUsers                num of users.
     * @param endDate                   end date of process - for time split.
     * @param numOfDays                 num of days.
     * @param weightToFeaturesSortedMap weight to features sorted map.
     */
    private void assertSmartsBetweenContexts(List<SmartRecord> smartRecords, int numOfUsers, Instant startDate, Instant endDate, int numOfDays, int startHourOfDay, int endHourOfDay, TreeMap<Double, List<String>> weightToFeaturesSortedMap) {
        // Split time to partitions.
        Instant start = startDate;
        List<Instant> instants = new LinkedList<>();

        while (start.isBefore(endDate)) {
            Instant currentEnd = start.plus(Duration.ofHours(1));
            instants.add(currentEnd);
            start = currentEnd;
        }

        // Num of smarts per user, which have not same weight.
        int expectedNumOfGeneratedHours = numOfDays * (endHourOfDay - startHourOfDay);
        int numOfGeneratedHours = 0;

        // Compare smarts of all contexts per hour.
        for (Instant end : instants) {
            for (Map.Entry<Double, List<String>> weightToFeature : weightToFeaturesSortedMap.entrySet()) {
                List<String> features = weightToFeature.getValue();
                List<SmartRecord> filteredSmarts = smartRecords.stream().filter(smartRecord -> {
                    List<SmartAggregationRecord> smartAggregationRecords = smartRecord.getSmartAggregationRecords();
                    return smartAggregationRecords.stream()
                            .map(SmartAggregationRecord::getAggregationRecord)
                            .anyMatch(aggregationRecord -> features.contains(aggregationRecord.getFeatureName()))
                            && smartRecord.getEndInstant().compareTo(end) == 0;
                }).collect(Collectors.toList());

                if (!filteredSmarts.isEmpty()) {
                    Assert.assertEquals(filteredSmarts.size(), numOfUsers);
                    SmartRecord expectedSmart = filteredSmarts.get(0);
                    Assert.assertTrue(filteredSmarts.stream().allMatch(smart -> smart.getScore().equals(expectedSmart.getScore())));
                    Assert.assertTrue(filteredSmarts.stream().allMatch(smart -> smart.getSmartValue() == expectedSmart.getSmartValue()));
                    Assert.assertTrue(filteredSmarts.stream().allMatch(smart -> smart.getStartInstant().equals(expectedSmart.getStartInstant())));
                    Assert.assertTrue(filteredSmarts.stream().allMatch(smart -> smart.getEndInstant().equals(expectedSmart.getEndInstant())));
                    Assert.assertTrue(filteredSmarts.stream().allMatch(smart -> smart.getFixedDurationStrategy().compareTo(expectedSmart.getFixedDurationStrategy()) == 0));
                    Assert.assertTrue(filteredSmarts.stream().allMatch(smart -> smart.getFeatureName().equals(expectedSmart.getFeatureName())));
                    numOfGeneratedHours++;
                }
            }
        }

        Assert.assertEquals(expectedNumOfGeneratedHours, numOfGeneratedHours);
    }

    /**
     * Assert that smarts of features with higher weight have higher smart score that of feature with lower weight.
     * Assert that smarts of features with same weight have same smartScore and smartValue that of feature with lower weight.
     *
     * @param smartRecords              smart records.
     * @param weightToFeaturesSortedMap weight to features sorted map.
     * @return map of weight to smart score.
     */
    private Map<Double, Double> AssertSmartRecords(List<SmartRecord> smartRecords, TreeMap<Double, List<String>> weightToFeaturesSortedMap) {
        Map<Double, Double> weightToScore = new HashMap<>();
        Double weightScore = 0.0;
        Double weightSmartValue = 0.0;

        for (Map.Entry<Double, List<String>> weightToFeature : weightToFeaturesSortedMap.entrySet()) {
            Double weight = weightToFeature.getKey();

            for (String featureName : weightToFeature.getValue()) {
                List<SmartRecord> filteredSmartsByFeature = smartRecords.stream().filter(smartRecord -> {
                    List<SmartAggregationRecord> smartAggregationRecords = smartRecord.getSmartAggregationRecords();
                    Assert.assertEquals(1, smartAggregationRecords.size());
                    return smartAggregationRecords.get(0).getAggregationRecord().getFeatureName().equals(featureName);
                }).collect(Collectors.toList());

                for (SmartRecord smart : filteredSmartsByFeature) {
                    if (weightToScore.containsKey(weight)) {
                        Assert.assertEquals(weightScore, smart.getScore());
                        Assert.assertEquals(weightSmartValue, smart.getSmartValue(), 0);
                    } else {
                        Assert.assertTrue(smart.getScore() > 0);
                        Assert.assertTrue(smart.getScore() > weightScore);
                        weightToScore.put(weight, smart.getScore());
                        weightScore = smart.getScore();
                        weightSmartValue = smart.getSmartValue();
                    }
                }
            }
        }

        return weightToScore;
    }

    /**
     * Generate aggregated Features (F + P).
     *
     * @param daysBackTo days until TimeGenerator will generate.
     * @param contextIds generate aggregated Features for given contextIds.
     * @return time range of AggregatedFeature records.
     */
    private TimeRange generateAggregatedFeatureEventConf(int daysBackFrom, int daysBackTo, int startHourOfDay, int endHourOfDay, List<String> contextIds) throws GeneratorException {
        IStringListGenerator contextIdGenerator = new FixedListGenerator(contextIds);
        ITimeGenerator startInstantGenerator = new MinutesIncrementTimeGenerator(LocalTime.of(startHourOfDay, 0), LocalTime.of(endHourOfDay, 0), 60, daysBackFrom, daysBackTo);
        // Generate scored F:
        ScoredFeatureAggregationRecordHourlyGenerator scoredAggregationGenerator = new ScoredFeatureAggregationRecordHourlyGenerator(aggregatedFeatureToScoreGenerator, contextIdGenerator, startInstantGenerator);
        List<AdeAggregationRecord> adeScoredAggregationRecords = scoredAggregationGenerator.generate();
        // Generate P:
        startInstantGenerator = new MinutesIncrementTimeGenerator(LocalTime.of(startHourOfDay, 0), LocalTime.of(endHourOfDay, 0), 60, daysBackFrom, daysBackTo);
        AdeAggregationRecordHourlyGenerator adeAggregationGenerator = new AdeAggregationRecordHourlyGenerator(aggregatedFeatureToValueGenerator, startInstantGenerator, contextIdGenerator);
        List<AdeAggregationRecord> adeAggregationRecords = adeAggregationGenerator.generate();
        aggregatedDataStore.store(adeAggregationRecords, AggregatedFeatureType.SCORE_AGGREGATION, new StoreMetadataProperties());
        aggregatedDataStore.store(adeScoredAggregationRecords, AggregatedFeatureType.FEATURE_AGGREGATION, new StoreMetadataProperties());
        Instant start = adeAggregationRecords.stream().min(Comparator.comparing(AdeAggregationRecord::getStartInstant)).get().getStartInstant();
        Instant end = adeAggregationRecords.stream().max(Comparator.comparing(AdeAggregationRecord::getEndInstant)).get().getStartInstant();
        return new TimeRange(start, end.plus(Duration.ofHours(1)));
    }

    /**
     * Create WeightModel.
     * Use weightToFeaturesSortedMap map to fill clusterConfs.
     *
     * @param end                       end instant of model.
     * @param weightToFeaturesSortedMap weight to features sorted map.
     */
    private void createWeightModel(Instant end, TreeMap<Double, List<String>> weightToFeaturesSortedMap) {
        List<ClusterConf> clusterConfs = new ArrayList<>();
        weightToFeaturesSortedMap.forEach((weight, featureList) -> featureList.forEach(featureName -> {
            List<String> singletonFeature = Collections.singletonList(featureName);
            ClusterConf clusterConf = new ClusterConf(singletonFeature, weight);
            clusterConfs.add(clusterConf);
        }));
        SmartWeightsModel smartWeightsModel = new SmartWeightsModel().setClusterConfs(clusterConfs);
        ModelDAO modelDao = new ModelDAO("test-session-id", null, smartWeightsModel, end.minus(Duration.ofDays(90)), end, null);
        mongoTemplate.insert(modelDao, "model_smart.global.weights.userId.hourly");
    }

    /**
     * Create model for user with low anomalies.
     * User usually has not anomaly behaviour.
     * In case the user has anomaly behaviour, he gets low positive smartValues.
     * Normal user behavior is: numOfZeroValues=300, numOfPositiveValues=60, avgFeatureValues=50.
     *
     * @param end                       end instant of the model.
     * @param weightToFeaturesSortedMap weight to features sorted map.
     */
    private void createLowAnomaliesUserSmartValuesModel(Instant end, String contextFieldName, String contextValue, TreeMap<Double, List<String>> weightToFeaturesSortedMap) {
        int numOfZeroValues = 50;
        int numOfPositiveValues = 5;
        Double minWeight = Collections.min(weightToFeaturesSortedMap.keySet());
        double avgSmartValue = avgFeatureValueForLowAnomaliesUser * minWeight;
        Map<Long, Double> startInstantToMaxSmartValue = new HashMap<>();
        Duration duration = Duration.ofDays(1);
        Instant startInstant = end.minus(duration);

        for (int i = 0; i < numOfPositiveValues; i++) {
            startInstantToMaxSmartValue.put(startInstant.getEpochSecond(), avgSmartValue);
            startInstant = startInstant.minus(duration);
        }

        for (int i = 0; i < numOfZeroValues; i++) {
            startInstantToMaxSmartValue.put(startInstant.getEpochSecond(), 0.0);
            startInstant = startInstant.minus(duration);
        }

        SMARTMaxValuesModel smartMaxValuesModel = new SMARTMaxValuesModel();
        smartMaxValuesModel.init(startInstantToMaxSmartValue, startInstantToMaxSmartValue.size(), end);
        String contextId = contextFieldName +"#" + contextValue;
        ModelDAO modelDao = new ModelDAO("test-session-id", contextId, smartMaxValuesModel,
                end.minus(Duration.ofDays(90)), end,
                Collections.singletonMap(contextFieldName, contextValue));
        mongoTemplate.insert(modelDao, "model_smart.userId.hourly");
    }

    /**
     * Create SmartValuesPriorModel for user with low anomalies.
     * The model represents user with avg FeatureValue 50.
     *
     * @param end                       end instant of model.
     * @param weightToFeaturesSortedMap weight to features sorted map.
     */
    private void createPriorModelForLowAnomaliesUser(Instant end, TreeMap<Double, List<String>> weightToFeaturesSortedMap) {
        Double minWeight = Collections.min(weightToFeaturesSortedMap.keySet());
        double prior = avgFeatureValueForLowAnomaliesUser * minWeight;
        SMARTValuesPriorModel smartValuesPriorModel = new SMARTValuesPriorModel();
        smartValuesPriorModel.init(prior);
        smartValuesPriorModel.setWeightsModelEndTime(end);
        smartValuesPriorModel.setNumOfPartitions(30);
        ModelDAO modelDao = new ModelDAO("test-session-id", null, smartValuesPriorModel, end.minus(Duration.ofDays(90)), end, null);
        mongoTemplate.insert(modelDao, "model_smart.global.prior.userId.hourly");
    }

    /**
     * Build weightToFeatures sorted map.
     * Split features into groups, where each group has same weight.
     */
    private TreeMap<Double, List<String>> createFeaturesGroups() {
        List<AggregatedFeatureEventConf> aggregatedFeatureEventConfList = getIncludedAggregatedFeatureEventConfs();
        Double weight = 0.1;
        Double decreasedValueOfWeight = 0.005;
        TreeMap<Double, List<String>> weightToFeaturesSortedMap = new TreeMap<>();
        List<List<AggregatedFeatureEventConf>> partitions = Lists.partition(aggregatedFeatureEventConfList, numOfFeaturesInGroups);
        List<List<String>> featuresGroups = partitions.stream()
                .map(list -> list.stream().map(AggregatedFeatureEventConf::getName).collect(Collectors.toList()))
                .collect(Collectors.toList());

        for (List<String> featuresGroup : featuresGroups) {
            weightToFeaturesSortedMap.put(weight, featuresGroup);
            weight = weight - decreasedValueOfWeight;
        }

        return weightToFeaturesSortedMap;
    }

    /**
     * Create CyclicMapGenerator for F and P.
     * CyclicMapGenerator contains list of maps, where map consist of features and score/value.
     * Map contains all features, where only one has score/value = 100.
     */
    private void createSingleHighValueAggregatedFeatureGenerators() {
        List<AggregatedFeatureEventConf> aggregatedFeatureEventConfList = getIncludedAggregatedFeatureEventConfs();
        Map<AggregatedFeatureEventConf, Double> aggregatedFeatureEventConfToValue = aggregatedFeatureEventConfList.stream()
                .collect(Collectors.toMap(aggregatedFeature -> aggregatedFeature, aggregatedFeature -> 0.0));
        Map<AggregatedFeatureEventConf, Double> aggregatedFeatureEventConfToScoreMap = aggregatedFeatureEventConfToValue.entrySet().stream()
                .filter(valueToAggr -> valueToAggr.getKey().getType().equals("F"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<AggregatedFeatureEventConf, Double> aggregatedFeatureEventConfToValueMap = aggregatedFeatureEventConfToValue.entrySet().stream()
                .filter(valueToAggr -> valueToAggr.getKey().getType().equals("P"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        // F config to feature score.
        List<Map<AggregatedFeatureEventConf, Double>> aggregatedFeatureEventConfToScoreList = new ArrayList<>();
        // P config to feature value.
        List<Map<AggregatedFeatureEventConf, Double>> aggregatedFeatureEventConfToValueList = new ArrayList<>();

        // Create list of maps <AggregatedFeatureEventConf, score/value>, where each map contains one feature with score/value 100.
        for (AggregatedFeatureEventConf conf : aggregatedFeatureEventConfList) {
            Map<AggregatedFeatureEventConf, Double> aggregatedFeatureEventConfToScoreMapTmp = new HashMap<>(aggregatedFeatureEventConfToScoreMap);
            Map<AggregatedFeatureEventConf, Double> aggregatedFeatureEventConfToValueMapTmp = new HashMap<>(aggregatedFeatureEventConfToValueMap);

            if (conf.getType().equals("F")) {
                aggregatedFeatureEventConfToScoreMapTmp.put(conf, 100.0);
            } else {
                aggregatedFeatureEventConfToValueMapTmp.put(conf, 100.0);
            }

            aggregatedFeatureEventConfToScoreList.add(aggregatedFeatureEventConfToScoreMapTmp);
            aggregatedFeatureEventConfToValueList.add(aggregatedFeatureEventConfToValueMapTmp);
        }

        aggregatedFeatureToScoreGenerator = new CyclicMapGenerator<>(aggregatedFeatureEventConfToScoreList);
        aggregatedFeatureToValueGenerator = new CyclicMapGenerator<>(aggregatedFeatureEventConfToValueList);
    }

    /**
     * Create CyclicMapGenerator for F and P.
     * CyclicMapGenerator contains list of maps, where map consist of features and score/value.
     * Map contains set of features, where all features have different weight.
     */
    private void setOfFeaturesWithDiffWeightsAggrFeatureGenerators(TreeMap<Double, List<String>> weightToFeaturesSortedMap, double featureValue) {
        List<AggregatedFeatureEventConf> aggregatedFeatureEventConfList = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList();
        // Build list of features with different weights.
        List<List<String>> featuresWithDiffWeightList = new ArrayList<>();

        for (int i = 0; i < numOfFeaturesInGroups; i++) {
            List<String> featuresWithDiffWeight = new ArrayList<>();

            for (List<String> features : weightToFeaturesSortedMap.values()) {
                if (i < features.size()) {
                    featuresWithDiffWeight.add(features.get(i));
                }
            }

            featuresWithDiffWeightList.add(featuresWithDiffWeight);
        }

        // F config to feature score.
        List<Map<AggregatedFeatureEventConf, Double>> aggregatedFeatureEventConfToScoreList = new ArrayList<>();
        // P config to feature value.
        List<Map<AggregatedFeatureEventConf, Double>> aggregatedFeatureEventConfToValueList = new ArrayList<>();

        for (List<String> featureNames : featuresWithDiffWeightList) {
            Map<AggregatedFeatureEventConf, Double> aggregatedFeatureEventConfToValue = aggregatedFeatureEventConfList.stream()
                    .filter(aggregatedFeatureEventConf -> featureNames.contains(aggregatedFeatureEventConf.getName()))
                    .collect(Collectors.toMap(aggregatedFeature -> aggregatedFeature, aggregatedFeature -> featureValue));
            Map<AggregatedFeatureEventConf, Double> aggregatedFeatureEventConfToScoreMap = aggregatedFeatureEventConfToValue.entrySet().stream()
                    .filter(valueToAggr -> valueToAggr.getKey().getType().equals("F"))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            Map<AggregatedFeatureEventConf, Double> aggregatedFeatureEventConfToValueMap = aggregatedFeatureEventConfToValue.entrySet().stream()
                    .filter(valueToAggr -> valueToAggr.getKey().getType().equals("P"))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            aggregatedFeatureEventConfToScoreList.add(aggregatedFeatureEventConfToScoreMap);
            aggregatedFeatureEventConfToValueList.add(aggregatedFeatureEventConfToValueMap);
        }

        aggregatedFeatureToScoreGenerator = new CyclicMapGenerator<>(aggregatedFeatureEventConfToScoreList);
        aggregatedFeatureToValueGenerator = new CyclicMapGenerator<>(aggregatedFeatureEventConfToValueList);
    }

    private List<AggregatedFeatureEventConf> getIncludedAggregatedFeatureEventConfs() {
        SmartRecordConf smartRecordConf = smartRecordConfService.getSmartRecordConf("userId_hourly");
        return aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfs(smartRecordConf).stream()
                .filter(aggregatedFeatureEventConf -> aggregatedFeatureEventConf.getBucketConf().getContextFieldNames().size() == 1 && aggregatedFeatureEventConf.getBucketConf().getContextFieldNames().get(0).equals("userId"))
                .filter(conf -> !smartRecordConf.getExcludedAggregationRecords().contains(conf.getName()))
                .collect(Collectors.toList());
    }

    @Configuration
    @Import({SmartApplicationConfigurationTest.class, BaseAppTest.springConfig.class, ModelStoreConfig.class})
    protected static class springConfigModelingServiceApplication {
    }
}
