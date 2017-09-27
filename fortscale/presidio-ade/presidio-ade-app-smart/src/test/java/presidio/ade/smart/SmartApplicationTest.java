package presidio.ade.smart;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.ml.model.*;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStore;
import fortscale.ml.model.store.ModelStoreConfig;
import fortscale.smart.record.conf.ClusterConf;
import fortscale.utils.jonfig.Jonfig;
import fortscale.utils.logging.Logger;
import fortscale.utils.test.category.ModuleTestCategory;
import fortscale.utils.time.TimeRange;
import fortscale.utils.time.TimeService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections.map.SingletonMap;
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
    private TreeMap<Double, List<String>> weightToFeatures;
    private static final Logger logger = Logger.getLogger(SmartApplicationTest.class);

    private static final int GENERATOR_START_HOUR_OF_DAY = 1;
    private static final int GENERATOR_END_HOUR_OF_DAY = 22;
    private static final int GENERATOR_DAYS_BACK_FROM = 30;
    //duration that covers all 42 features: 2 days 01:00 - 22:00
    private static final int DURATION_OF_PROCESS = 2;
    private static final Instant START_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(GENERATOR_DAYS_BACK_FROM)), Duration.ofDays(1));
    private static final Instant END_DATE = START_DATE.plus(Duration.ofDays(DURATION_OF_PROCESS));

    private static final Double START_WEIGHT = 0.1;
    private static final Double DECREASED_VALUE_OF_WEIGHT = 0.005;
    //features divided to #6 groups
    private static final int NUM_OF_GROUPS = 6;

    public static final String EXECUTION_COMMAND = String.format("process --smart_record_conf_name %s --start_date %s --end_date %s", "userId_hourly", START_DATE.toString(), END_DATE.toString());

    private int aggregatedFeatureConfCount;
    private IMapGenerator aggregatedFeatureToScoreGenerator;
    private IMapGenerator aggregatedFeatureToValueGenerator;
    @Autowired
    private AggregatedDataStore aggregatedDataStore;
    @Autowired
    protected MongoTemplate mongoTemplate;
    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

    @Override
    protected String getContextTestExecutionCommand() {
        return EXECUTION_COMMAND;
    }


    @Before
    public void setUp() {
        mongoTemplate.getCollectionNames().forEach(collection -> mongoTemplate.dropCollection(collection));
        createFeaturesGroups();
        createAggregatedFeatureGenerators();
    }


    /**
     * Test normal user, who usually has not anomaly behavior.
     * In case the user has anomaly behaviour, he gets low positive smartValues.
     * <p>
     * Test that feature with higher weight has higher influence over feature with lower weight if both have the same featureValue.
     *
     * @throws GeneratorException
     */
    @Test
    public void testNormalUserTest() throws GeneratorException {
        String contextId = "user1";
        createWeightModel(START_DATE);
        createNormalUserSmartValuesModel(START_DATE, "userId#" + contextId);
        createSmartValuesPriorModel(START_DATE);

        int daysBackTo = GENERATOR_DAYS_BACK_FROM - DURATION_OF_PROCESS;
        List<String> contextIds = Collections.singletonList(contextId);

        generateAggregatedFeatureEventConf(daysBackTo, contextIds);

        executeAndAssertCommandSuccess(EXECUTION_COMMAND);

        List<SmartRecord> smartRecords = mongoTemplate.findAll(SmartRecord.class, "smart_userId_hourly");

        Assert.assertTrue(smartRecords.size() == aggregatedFeatureConfCount);

        AssertSmartRecords(smartRecords);
    }

    /**
     * Test over 3 users for period of 6 days.
     *
     * @throws GeneratorException
     */
    @Test
    public void testNormalUsersTest() throws GeneratorException {

        int numOfDays = 6;
        int daysBackTo = GENERATOR_DAYS_BACK_FROM - numOfDays;
        Instant end = START_DATE.plus(Duration.ofDays(numOfDays));

        List<String> contextIds = new ArrayList<>();
        contextIds.add("user1");
        contextIds.add("user2");
        contextIds.add("user3");

        generateAggregatedFeatureEventConf(daysBackTo, contextIds);

        //Generate models for users:
        Instant start = START_DATE;
        while (start.isBefore(end)) {
            createWeightModel(start);
            createSmartValuesPriorModel(start);
            for (int i = 0; i < contextIds.size(); i++) {
                createNormalUserSmartValuesModel(start, "userId#" + contextIds.get(i));
            }
            start = start.plus(Duration.ofDays(2));
        }

        String command = String.format("process --smart_record_conf_name %s --start_date %s --end_date %s", "userId_hourly", START_DATE.toString(), end);
        executeAndAssertCommandSuccess(command);

        List<SmartRecord> smartRecords = mongoTemplate.findAll(SmartRecord.class, "smart_userId_hourly");

        contextIds.forEach(contextId -> {
            List<SmartRecord> contextIdSmartRecords = smartRecords.stream().filter(smart -> smart.getContextId().equals("userId#" + contextId)).collect(Collectors.toList());
            Assert.assertTrue(contextIdSmartRecords.size() == aggregatedFeatureConfCount * numOfDays / DURATION_OF_PROCESS);
            Map<Double, Double> contextIdWeightToScore = AssertSmartRecords(contextIdSmartRecords);
        });

        assertSmartsBetweenContexts(smartRecords, contextIds.size(), end, numOfDays);
    }

    /**
     * Compare smarts between contextIds.
     *
     * @param smartRecords smart records of all contexts
     * @param numOfUsers   num of users
     * @param endDate      end date of process - for time split
     * @param numOfDays    num of days
     */
    private void assertSmartsBetweenContexts(List<SmartRecord> smartRecords, int numOfUsers, Instant endDate, int numOfDays) {

        //split time to partitions
        Instant startDate = START_DATE.plus(Duration.ofHours(GENERATOR_START_HOUR_OF_DAY));
        List<Instant> instants = new LinkedList<>();
        while (startDate.isBefore(endDate)) {
            Instant currentEnd = startDate.plus(Duration.ofHours(1));
            instants.add(currentEnd);
            startDate = currentEnd;
        }

        //num of smarts per user, which have not same weight
        int expectedNumOfGeneratedHours = numOfDays * (GENERATOR_END_HOUR_OF_DAY - GENERATOR_START_HOUR_OF_DAY);
        int numOfGeneratedHours = 0;

        //compare smarts of all contexts per hour
        for (Instant end : instants) {
            for (Map.Entry<Double, List<String>> weightToFeature : weightToFeatures.entrySet()) {
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
     * @param smartRecords smart records
     * @return map of weight to smart score
     */
    private Map<Double, Double> AssertSmartRecords(List<SmartRecord> smartRecords) {
        Map<Double, Double> weightToScore = new HashMap<>();

        Double weightScore = 0.0;
        Double weightSmartValue = 0.0;

        for (Map.Entry<Double, List<String>> weightToFeature : weightToFeatures.entrySet()) {
            Double weight = weightToFeature.getKey();
            for (String featureName : weightToFeature.getValue()) {

                List<SmartRecord> filteredSmartsScoreByFeature = smartRecords.stream().filter(s -> {
                    List<AdeAggregationRecord> adeAggregationRecords = s.getAggregationRecords();
                    return adeAggregationRecords.stream().anyMatch(a -> a.getFeatureName().equals(featureName));
                }).map(s -> s).collect(Collectors.toList());

                for (SmartRecord smart : filteredSmartsScoreByFeature) {
                    if (weightToScore.containsKey(weight)) {
                        Assert.assertTrue(weightScore.equals(smart.getScore()));
                        Assert.assertTrue(weightSmartValue.equals(smart.getSmartValue()));
                    } else {
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
     * @throws GeneratorException
     */
    private void generateAggregatedFeatureEventConf(int daysBackTo, List<String> contextIds) throws GeneratorException {

        IStringListGenerator contextIdGenerator = new FixedListGenerator(contextIds);
        TimeGenerator startInstantGenerator = new TimeGenerator(LocalTime.of(GENERATOR_START_HOUR_OF_DAY, 0), LocalTime.of(GENERATOR_END_HOUR_OF_DAY, 0), 60, GENERATOR_DAYS_BACK_FROM, daysBackTo);

        //Generate scored F:
        ScoredFeatureAggregationRecordHourlyGenerator scoredAggregationGenerator =
                new ScoredFeatureAggregationRecordHourlyGenerator(aggregatedFeatureToScoreGenerator, contextIdGenerator, 0.0, new ArrayList<>(), startInstantGenerator);
        List<AdeAggregationRecord> adeScoredAggregationRecords = scoredAggregationGenerator.generate();

        //Generate P:
        startInstantGenerator = new TimeGenerator(LocalTime.of(GENERATOR_START_HOUR_OF_DAY, 0), LocalTime.of(GENERATOR_END_HOUR_OF_DAY, 0), 60, GENERATOR_DAYS_BACK_FROM, daysBackTo);
        AdeAggregationRecordHourlyGenerator adeAggregationGenerator =
                new AdeAggregationRecordHourlyGenerator(aggregatedFeatureToValueGenerator, startInstantGenerator, contextIdGenerator);
        List<AdeAggregationRecord> adeAggregationRecords = adeAggregationGenerator.generate();

        aggregatedDataStore.store(adeAggregationRecords, AggregatedFeatureType.SCORE_AGGREGATION);
        aggregatedDataStore.store(adeScoredAggregationRecords, AggregatedFeatureType.FEATURE_AGGREGATION);
    }


    /**
     * Create WeightModel.
     * Use weightToFeatures map to fill clusterConfs.
     *
     * @param end end instant of model
     */
    private void createWeightModel(Instant end) {
        List<ClusterConf> clusterConfs = new ArrayList<>();
        weightToFeatures.forEach((weight, featureList) -> {
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
     * Create model for a normal user.
     * User usually has not anomaly behaviour.
     * In case the user has anomaly behaviour, he gets low positive smartValues.
     * Normal user behavior is: numOfZeroValues=300, numOfPositiveValues=60, avgFeatureValues=50
     *
     * @param end end instant of the model.
     */
    private void createNormalUserSmartValuesModel(Instant end, String contextId) {
        long numOfZeroValues = 300L;
        long numOfPositiveValues = 60L;
        double avgFeatureValue = 0.5;
        Double minWeight = Collections.min(weightToFeatures.keySet());
        double smartValue = avgFeatureValue * minWeight;
        double sumOfValues = smartValue * numOfPositiveValues;
        SMARTValuesModel smartValuesModel = new SMARTValuesModel();
        smartValuesModel.init(numOfZeroValues, numOfPositiveValues, sumOfValues);
        ModelDAO modelDao = new ModelDAO("test-session-id", contextId, smartValuesModel, end.minus(Duration.ofDays(90)), end);
        mongoTemplate.insert(modelDao, "model_smart.userId.hourly");
    }


    /**
     * Create SmartValuesPriorModel.
     * The model represents user with avg FeatureValue 50.
     *
     * @param end end instant of model
     */
    private void createSmartValuesPriorModel(Instant end) {
        double avgFeatureValue = 0.5;
        Double minWeight = Collections.min(weightToFeatures.keySet());
        double prior = avgFeatureValue * minWeight;
        SMARTValuesPriorModel smartValuesPriorModel = new SMARTValuesPriorModel();
        smartValuesPriorModel.init(prior);
        ModelDAO modelDao = new ModelDAO("test-session-id", null, smartValuesPriorModel, end.minus(Duration.ofDays(90)), end);
        mongoTemplate.insert(modelDao, "model_smart.global.prior.userId.hourly");
    }

    /**
     * Build weightToFeatures map.
     * Split features to groups, where each group has same weight.
     */
    private void createFeaturesGroups() {
        List<AggregatedFeatureEventConf> aggregatedFeatureEventConfList = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList();
        aggregatedFeatureConfCount = aggregatedFeatureEventConfList.size();
        Double weight = START_WEIGHT;
        weightToFeatures = new TreeMap<>();

        List<List<AggregatedFeatureEventConf>> partitions = Lists.partition(aggregatedFeatureEventConfList, NUM_OF_GROUPS);

        List<List<String>> featuresGroups = partitions.stream().map(list -> {
            return list.stream().map(p -> p.getName()).collect(Collectors.toList());
        }).collect(Collectors.toList());

        for (List<String> featuresGroup : featuresGroups) {
            weightToFeatures.put(weight, featuresGroup);
            weight = weight - DECREASED_VALUE_OF_WEIGHT;
        }
    }

    /**
     * Create CyclicFixedMapGenerator for F and P.
     * CyclicFixedMapGenerator contains list of maps, where map consist of features and score/value.
     * Only 1 feature has score/value = 100 in each map.
     */
    private void createAggregatedFeatureGenerators() {
        List<AggregatedFeatureEventConf> aggregatedFeatureEventConfList = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList();
        Map<AggregatedFeatureEventConf, Double> aggregatedFeatureEventConfToValue = aggregatedFeatureEventConfList.stream().collect(Collectors.toMap(aggregatedFeature -> aggregatedFeature, aggregatedFeature -> 0.0));

        //F config to feature score
        List<Map<AggregatedFeatureEventConf, Double>> aggregatedFeatureEventConfToScoreList = new ArrayList<>();
        //P config to feature value
        List<Map<AggregatedFeatureEventConf, Double>> aggregatedFeatureEventConfToValueList = new ArrayList<>();

        //create list of maps <AggregatedFeatureEventConf, score/value>, where each map contains one feature with score/value 100.
        for (AggregatedFeatureEventConf conf : aggregatedFeatureEventConfList) {
            aggregatedFeatureEventConfToValue.put(conf, 100.0);

            Map<AggregatedFeatureEventConf, Double> aggregatedFeatureEventConfToScoreMap = aggregatedFeatureEventConfToValue.entrySet().stream()
                    .filter(valueToAggr -> valueToAggr.getKey().getType().equals("F")).collect(Collectors.toMap(
                            Map.Entry::getKey, Map.Entry::getValue));

            Map<AggregatedFeatureEventConf, Double> aggregatedFeatureEventConfToValueMap = aggregatedFeatureEventConfToValue.entrySet().stream()
                    .filter(valueToAggr -> valueToAggr.getKey().getType().equals("P")).collect(Collectors.toMap(
                            Map.Entry::getKey, Map.Entry::getValue));

            aggregatedFeatureEventConfToScoreList.add(aggregatedFeatureEventConfToScoreMap);
            aggregatedFeatureEventConfToValueList.add(aggregatedFeatureEventConfToValueMap);

            aggregatedFeatureEventConfToValue.put(conf, 0.0);
        }

        aggregatedFeatureToScoreGenerator = new CyclicFixedMapGenerator<>(aggregatedFeatureEventConfToScoreList);
        aggregatedFeatureToValueGenerator = new CyclicFixedMapGenerator<>(aggregatedFeatureEventConfToValueList);
    }


    @Configuration
    @Import({SmartApplicationConfigurationTest.class, BaseAppTest.springConfig.class, ModelStoreConfig.class})
    protected static class springConfigModelingServiceApplication {

    }
}
