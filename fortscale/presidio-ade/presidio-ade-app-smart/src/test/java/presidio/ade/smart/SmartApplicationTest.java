package presidio.ade.smart;

import com.google.common.collect.Lists;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.ml.model.*;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStoreConfig;
import fortscale.smart.record.conf.ClusterConf;
import fortscale.utils.airflow.service.DagExecutionStatus;
import fortscale.utils.logging.Logger;
import fortscale.utils.test.category.ModuleTestCategory;
import fortscale.utils.time.TimeRange;
import fortscale.utils.time.TimeService;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.domain.store.aggr.AggregatedDataStore;
import presidio.ade.smart.config.SmartApplicationConfigurationTest;
import presidio.ade.test.utils.generators.AdeAggregationRecordHourlyGenerator;
import presidio.ade.test.utils.generators.ScoredFeatureAggregationRecordHourlyGenerator;
import presidio.ade.test.utils.tests.BaseAppTest;
import presidio.data.generators.common.*;
import presidio.data.generators.common.time.TimeGenerator;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by YaronDL on 9/4/2017.
 */

@Category(ModuleTestCategory.class)
@ContextConfiguration
public class SmartApplicationTest extends BaseAppTest {

    private static final Logger logger = Logger.getLogger(SmartApplicationTest.class);

    private int aggregatedFeatureConfCount;
    private IMapGenerator aggregatedFeatureToScoreGenerator;
    private IMapGenerator aggregatedFeatureToValueGenerator;
    private static final double avgFeatureValueForLowAnomaliesUser = 0.5;
    public static final String EXECUTION_COMMAND = "process --smart_record_conf_name %s --start_date %s --end_date %s";
    @Autowired
    private AggregatedDataStore aggregatedDataStore;
    @Autowired
    protected MongoTemplate mongoTemplate;
    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

    @Override
    protected String getContextTestExecutionCommand() {
        Instant startDate = TimeService.floorTime(Instant.now().minus(Duration.ofDays(30)), Duration.ofDays(1));
        Instant endDate = startDate.plus(Duration.ofDays(2));
        return String.format(EXECUTION_COMMAND, "userId_hourly", startDate.toString(), endDate.toString());
    }

    @Before
    public void setUp() {
        mongoTemplate.getCollectionNames().forEach(collection -> mongoTemplate.dropCollection(collection));
    }


    /**
     * 1. Test that each configured feature which has the same feature value, influence the score in the same manner if it has the same weight.
     * 2. Test that feature with higher weight has higher influence over feature with lower weight when both have the same featureValue.
     *
     * @throws GeneratorException
     */
    @Test
    public void lowAnomaliesUserTest() throws GeneratorException {
        int daysBackFrom = 30;
        //duration that covers all 42 features: 2 days 01:00 - 22:00
        int durationOfProcess = 2;
        int daysBackTo = daysBackFrom - durationOfProcess;
        int startHourOfDay = 1;
        int endHourOfDay = 22;
        Instant startDate = TimeService.floorTime(Instant.now().minus(Duration.ofDays(daysBackFrom)), Duration.ofDays(1));
        Instant endDate = startDate.plus(Duration.ofDays(durationOfProcess));
        String contextId = "user1";

        TreeMap<Double, List<String>> weightToFeaturesSortedMap = createFeaturesGroups();

        createWeightModel(startDate, weightToFeaturesSortedMap);
        createLowAnomaliesUserSmartValuesModel(startDate, "userId#" + contextId, weightToFeaturesSortedMap);
        createPriorModelForLowAnomaliesUser(startDate, weightToFeaturesSortedMap);

        List<String> contextIds = Collections.singletonList(contextId);

        TimeRange timeRange = generateAggregatedFeatureEventConf(daysBackFrom, daysBackTo, startHourOfDay, endHourOfDay, contextIds);
        String command = String.format(EXECUTION_COMMAND, "userId_hourly", timeRange.getStart().toString(), timeRange.getEnd().toString());
        executeAndAssertCommandSuccess(command);

        List<SmartRecord> smartRecords = mongoTemplate.findAll(SmartRecord.class, "smart_userId_hourly");

        AssertSmartRecords(smartRecords, weightToFeaturesSortedMap);
    }

    /**
     * Test over 3 users for period of 6 days.
     *
     * @throws GeneratorException
     */
    @Test
    public void lowAnomaliesUsersTest() throws GeneratorException {
        int daysBackFrom = 30;
        //duration that covers all 42 features 3 times: 2 days 01:00 - 22:00
        int durationOfProcess = 6;
        int durationOfProcessThatCoversAllFeatures = 2;
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

        TimeRange timeRange = generateAggregatedFeatureEventConf(daysBackFrom, daysBackTo, startHourOfDay, endHourOfDay, contextIds);

        //Generate models for users:
        createModelsForLowAnomaliesUsers(startDate, endDate, contextIds, weightToFeaturesSortedMap);

        String command = String.format(EXECUTION_COMMAND, "userId_hourly", timeRange.getStart().toString(), timeRange.getEnd().toString());
        executeAndAssertCommandSuccess(command);

        List<SmartRecord> smartRecords = mongoTemplate.findAll(SmartRecord.class, "smart_userId_hourly");

        contextIds.forEach(contextId -> {
            List<SmartRecord> contextIdSmartRecords = smartRecords.stream().filter(smart -> smart.getContextId().equals("userId#" + contextId)).collect(Collectors.toList());
            Map<Double, Double> contextIdWeightToScore = AssertSmartRecords(contextIdSmartRecords, weightToFeaturesSortedMap);
        });

        assertSmartsBetweenContexts(smartRecords, contextIds.size(), startDate, endDate, durationOfProcess, startHourOfDay, endHourOfDay, weightToFeaturesSortedMap);
    }

    /**
     * Create models for normal users with low anomalies.
     * Create new model each 2 days due to maxDiffBetweenCachedModelAndEvent.
     *
     * @param startDate                 start date
     * @param endDate                   end date
     * @param contextIds                create SmartValuesModel for given context ids.
     * @param weightToFeaturesSortedMap weight to features sorted map.
     */
    private void createModelsForLowAnomaliesUsers(Instant startDate, Instant endDate, List<String> contextIds, TreeMap<Double, List<String>> weightToFeaturesSortedMap) {
        Instant start = startDate;
        while (start.isBefore(endDate)) {
            createWeightModel(start, weightToFeaturesSortedMap);
            createPriorModelForLowAnomaliesUser(start, weightToFeaturesSortedMap);
            for (int i = 0; i < contextIds.size(); i++) {
                createLowAnomaliesUserSmartValuesModel(start, "userId#" + contextIds.get(i), weightToFeaturesSortedMap);
            }
            start = start.plus(Duration.ofDays(2));
        }
    }

    /**
     * Compare smarts between contextIds.
     *
     * @param smartRecords              smart records of all contexts
     * @param numOfUsers                num of users
     * @param endDate                   end date of process - for time split
     * @param numOfDays                 num of days
     * @param weightToFeaturesSortedMap weight to features sorted map.
     */
    private void assertSmartsBetweenContexts(List<SmartRecord> smartRecords, int numOfUsers, Instant startDate, Instant endDate, int numOfDays, int startHourOfDay, int endHourOfDay, TreeMap<Double, List<String>> weightToFeaturesSortedMap) {

        //split time to partitions
        Instant start = startDate;
        List<Instant> instants = new LinkedList<>();
        while (start.isBefore(endDate)) {
            Instant currentEnd = start.plus(Duration.ofHours(1));
            instants.add(currentEnd);
            start = currentEnd;
        }

        //num of smarts per user, which have not same weight
        int expectedNumOfGeneratedHours = numOfDays * (endHourOfDay - startHourOfDay);
        int numOfGeneratedHours = 0;

        //compare smarts of all contexts per hour
        for (Instant end : instants) {
            for (Map.Entry<Double, List<String>> weightToFeature : weightToFeaturesSortedMap.entrySet()) {
                List<String> features = weightToFeature.getValue();
                List<SmartRecord> filteredSmarts = smartRecords.stream().filter(s -> {
                    List<AdeAggregationRecord> adeAggregationRecords = s.getAggregationRecords();
                    return adeAggregationRecords.stream().anyMatch(a -> features.contains(a.getFeatureName())) && s.getEndInstant().compareTo(end) == 0;
                }).map(s -> s).collect(Collectors.toList());

                if (!filteredSmarts.isEmpty()) {

                    Assert.assertTrue(filteredSmarts.size() == numOfUsers);

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

        Assert.assertTrue(expectedNumOfGeneratedHours == numOfGeneratedHours);

    }


    /**
     * Assert that smarts of features with higher weight have higher smart score that of feature with lower weight.
     * Assert that smarts of features with same weight have same smartScore and smartValue that of feature with lower weight.
     *
     * @param smartRecords              smart records
     * @param weightToFeaturesSortedMap weight to features sorted map.
     * @return map of weight to smart score
     */
    private Map<Double, Double> AssertSmartRecords(List<SmartRecord> smartRecords, TreeMap<Double, List<String>> weightToFeaturesSortedMap) {
        Map<Double, Double> weightToScore = new HashMap<>();

        Double weightScore = 0.0;
        Double weightSmartValue = 0.0;

        for (Map.Entry<Double, List<String>> weightToFeature : weightToFeaturesSortedMap.entrySet()) {
            Double weight = weightToFeature.getKey();
            for (String featureName : weightToFeature.getValue()) {

                List<SmartRecord> filteredSmartsByFeature = smartRecords.stream().filter(s -> {
                    List<AdeAggregationRecord> adeAggregationRecords = s.getAggregationRecords();
                    Assert.assertTrue(adeAggregationRecords.size() == 1);
                    return adeAggregationRecords.get(0).getFeatureName().equals(featureName);
                }).map(s -> s).collect(Collectors.toList());

                for (SmartRecord smart : filteredSmartsByFeature) {
                    if (weightToScore.containsKey(weight)) {
                        Assert.assertTrue(weightScore.equals(smart.getScore()));
                        Assert.assertTrue(weightSmartValue.equals(smart.getSmartValue()));
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
     * Generate aggregated Features (F + P)
     *
     * @param daysBackTo days until TimeGenerator will generate
     * @param contextIds generate aggregated Features for given contextIds
     * @return time range of AggregatedFeature records
     * @throws GeneratorException
     */
    private TimeRange generateAggregatedFeatureEventConf(int daysBackFrom, int daysBackTo, int startHourOfDay, int endHourOfDay, List<String> contextIds) throws GeneratorException {

        createAggregatedFeatureGenerators();

        IStringListGenerator contextIdGenerator = new FixedListGenerator(contextIds);
        TimeGenerator startInstantGenerator = new TimeGenerator(LocalTime.of(startHourOfDay, 0), LocalTime.of(endHourOfDay, 0), 60, daysBackFrom, daysBackTo);

        //Generate scored F:
        ScoredFeatureAggregationRecordHourlyGenerator scoredAggregationGenerator =
                new ScoredFeatureAggregationRecordHourlyGenerator(aggregatedFeatureToScoreGenerator, contextIdGenerator, startInstantGenerator);
        List<AdeAggregationRecord> adeScoredAggregationRecords = scoredAggregationGenerator.generate();

        //Generate P:
        startInstantGenerator = new TimeGenerator(LocalTime.of(startHourOfDay, 0), LocalTime.of(endHourOfDay, 0), 60, daysBackFrom, daysBackTo);
        AdeAggregationRecordHourlyGenerator adeAggregationGenerator =
                new AdeAggregationRecordHourlyGenerator(aggregatedFeatureToValueGenerator, startInstantGenerator, contextIdGenerator);
        List<AdeAggregationRecord> adeAggregationRecords = adeAggregationGenerator.generate();

        aggregatedDataStore.store(adeAggregationRecords, AggregatedFeatureType.SCORE_AGGREGATION);
        aggregatedDataStore.store(adeScoredAggregationRecords, AggregatedFeatureType.FEATURE_AGGREGATION);

        Instant start = adeAggregationRecords.stream().min(Comparator.comparing(AdeAggregationRecord::getStartInstant)).get().getStartInstant();
        Instant end = adeAggregationRecords.stream().max(Comparator.comparing(AdeAggregationRecord::getEndInstant)).get().getStartInstant();

        return new TimeRange(start, end.plus(Duration.ofHours(1)));
    }


    /**
     * Create WeightModel.
     * Use weightToFeaturesSortedMap map to fill clusterConfs.
     *
     * @param end                       end instant of model
     * @param weightToFeaturesSortedMap weight to features sorted map.
     */
    private void createWeightModel(Instant end, TreeMap<Double, List<String>> weightToFeaturesSortedMap) {
        List<ClusterConf> clusterConfs = new ArrayList<>();
        weightToFeaturesSortedMap.forEach((weight, featureList) -> {
            featureList.forEach(featureName -> {
                List<String> singletonFeature = Collections.singletonList(featureName);
                ClusterConf clusterConf = new ClusterConf(singletonFeature, weight);
                clusterConfs.add(clusterConf);
            });
        });
        SmartWeightsModel smartWeightsModel = new SmartWeightsModel().setClusterConfs(clusterConfs);
        ModelDAO modelDao = new ModelDAO("test-session-id", null, smartWeightsModel, end.minus(Duration.ofDays(90)), end);
        mongoTemplate.insert(modelDao, "model_smart.global.weights.userId.hourly");
    }

    /**
     * Create model for user with low anomalies.
     * <p>
     * User usually has not anomaly behaviour.
     * In case the user has anomaly behaviour, he gets low positive smartValues.
     * Normal user behavior is: numOfZeroValues=300, numOfPositiveValues=60, avgFeatureValues=50
     *
     * @param end                       end instant of the model.
     * @param weightToFeaturesSortedMap weight to features sorted map.
     */
    private void createLowAnomaliesUserSmartValuesModel(Instant end, String contextId, TreeMap<Double, List<String>> weightToFeaturesSortedMap) {
        long numOfZeroValues = 300L;
        long numOfPositiveValues = 60L;
        Double minWeight = Collections.min(weightToFeaturesSortedMap.keySet());
        double avgSmartValue = avgFeatureValueForLowAnomaliesUser * minWeight;
        double sumOfValues = avgSmartValue * numOfPositiveValues;
        SMARTValuesModel smartValuesModel = new SMARTValuesModel();
        smartValuesModel.init(numOfZeroValues, numOfPositiveValues, sumOfValues);
        ModelDAO modelDao = new ModelDAO("test-session-id", contextId, smartValuesModel, end.minus(Duration.ofDays(90)), end);
        mongoTemplate.insert(modelDao, "model_smart.userId.hourly");
    }


    /**
     * Create SmartValuesPriorModel for user with low anomalies.
     * <p>
     * The model represents user with avg FeatureValue 50.
     *
     * @param end                       end instant of model
     * @param weightToFeaturesSortedMap weight to features sorted map.
     */
    private void createPriorModelForLowAnomaliesUser(Instant end, TreeMap<Double, List<String>> weightToFeaturesSortedMap) {
        Double minWeight = Collections.min(weightToFeaturesSortedMap.keySet());
        double prior = avgFeatureValueForLowAnomaliesUser * minWeight;
        SMARTValuesPriorModel smartValuesPriorModel = new SMARTValuesPriorModel();
        smartValuesPriorModel.init(prior);
        ModelDAO modelDao = new ModelDAO("test-session-id", null, smartValuesPriorModel, end.minus(Duration.ofDays(90)), end);
        mongoTemplate.insert(modelDao, "model_smart.global.prior.userId.hourly");
    }

    /**
     * Build weightToFeatures sorted map.
     * Split features into groups, where each group has same weight.
     */
    private TreeMap<Double, List<String>> createFeaturesGroups() {
        List<AggregatedFeatureEventConf> aggregatedFeatureEventConfList = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList();
        aggregatedFeatureConfCount = aggregatedFeatureEventConfList.size();
        Double weight = 0.1;
        Double decreasedValueOfWeight = 0.005;
        //features divided to #6 groups
        int numOfGroups = 6;

        TreeMap<Double, List<String>> weightToFeaturesSortedMap = new TreeMap<>();

        List<List<AggregatedFeatureEventConf>> partitions = Lists.partition(aggregatedFeatureEventConfList, numOfGroups);

        List<List<String>> featuresGroups = partitions.stream().map(list -> {
            return list.stream().map(p -> p.getName()).collect(Collectors.toList());
        }).collect(Collectors.toList());

        for (List<String> featuresGroup : featuresGroups) {
            weightToFeaturesSortedMap.put(weight, featuresGroup);
            weight = weight - decreasedValueOfWeight;
        }

        return weightToFeaturesSortedMap;
    }

    /**
     * Create CyclicMapGenerator for F and P.
     * CyclicMapGenerator contains list of maps, where map consist of features and score/value.
     * Only 1 feature has score/value = 100 in each map.
     */
    private void createAggregatedFeatureGenerators() {
        List<AggregatedFeatureEventConf> aggregatedFeatureEventConfList = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList();

        Map<AggregatedFeatureEventConf, Double> aggregatedFeatureEventConfToValue = aggregatedFeatureEventConfList.stream().collect(Collectors.toMap(aggregatedFeature -> aggregatedFeature, aggregatedFeature -> 0.0));

        Map<AggregatedFeatureEventConf, Double> aggregatedFeatureEventConfToScoreMap = aggregatedFeatureEventConfToValue.entrySet().stream()
                .filter(valueToAggr -> valueToAggr.getKey().getType().equals("F")).collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue));

        Map<AggregatedFeatureEventConf, Double> aggregatedFeatureEventConfToValueMap = aggregatedFeatureEventConfToValue.entrySet().stream()
                .filter(valueToAggr -> valueToAggr.getKey().getType().equals("P")).collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue));

        //F config to feature score
        List<Map<AggregatedFeatureEventConf, Double>> aggregatedFeatureEventConfToScoreList = new ArrayList<>();
        //P config to feature value
        List<Map<AggregatedFeatureEventConf, Double>> aggregatedFeatureEventConfToValueList = new ArrayList<>();

        //create list of maps <AggregatedFeatureEventConf, score/value>, where each map contains one feature with score/value 100.
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


    @Configuration
    @Import({SmartApplicationConfigurationTest.class, BaseAppTest.springConfig.class, ModelStoreConfig.class})
    protected static class springConfigModelingServiceApplication {

    }
}
