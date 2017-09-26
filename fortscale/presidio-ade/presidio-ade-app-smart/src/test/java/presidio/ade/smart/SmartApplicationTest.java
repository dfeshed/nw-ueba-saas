package presidio.ade.smart;

import com.google.common.collect.Lists;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.ml.model.*;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStore;
import fortscale.ml.model.store.ModelStoreConfig;
import fortscale.smart.record.conf.ClusterConf;
import fortscale.utils.test.category.ModuleTestCategory;
import fortscale.utils.time.TimeService;
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
    private Map<Double, List<String>> weightToFeatures;


    private static final int GENERATOR_START_HOUR_OF_DAY = 1;
    private static final int GENERATOR_END_HOUR_OF_DAY = 22;
    private static final int GENERATOR_DAYS_BACK_FROM = 30;

    private static final Instant START_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(GENERATOR_DAYS_BACK_FROM)), Duration.ofDays(1));
    private static final int DURATION_OF_PROCESS = 2;
    private static final Instant END_DATE = START_DATE.plus(Duration.ofDays(DURATION_OF_PROCESS));

    private static final Double START_WEIGHT = 0.1;
    private static final Double WEIGHT_DECREASED_VALUE = 0.005;
    private static final int NUM_OF_GROUPS = 6;

    @Autowired
    private AggregatedDataStore aggregatedDataStore;
    @Autowired
    protected MongoTemplate mongoTemplate;
    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

    public static final String EXECUTION_COMMAND = String.format("process --smart_record_conf_name %s --start_date %s --end_date %s", "userId_hourly", START_DATE.toString(), END_DATE.toString());

    @Override
    protected String getContextTestExecutionCommand() {
        return EXECUTION_COMMAND;
    }

    private IMapGenerator aggregatedFeatureToScoreGenerator;
    private IMapGenerator aggregatedFeatureToValueGenerator;


    @Before
    public void setUp() throws GeneratorException {
        createFeaturesGroups();
        createAggregatedFeatureGenerators();
    }

    @Test
    public void SmartTest() throws GeneratorException {
        Map<Double, Double> weightToScore = testNormalUser();
        testNormalUsers(weightToScore);
    }


    /**
     * Test that feature with higher weight has higher influence over feature with lower weight if both have the same featureValue.
     * @throws GeneratorException
     */
    public Map<Double, Double> testNormalUser() throws GeneratorException {

        String contextId = "user1";
        createWeightModel(START_DATE);
        createNormalUserSmartValuesModel(START_DATE,"userId#"+contextId);
        createSmartValuesPriorModel(START_DATE);

        int daysBackTo = GENERATOR_DAYS_BACK_FROM - DURATION_OF_PROCESS;
        List<String> contextIds = Collections.singletonList(contextId);
        generateAggregatedFeatureEventConf(daysBackTo, contextIds);

        executeAndAssertCommandSuccess(EXECUTION_COMMAND);
        List<SmartRecord> smartRecords = mongoTemplate.findAll(SmartRecord.class, "smart_userId_hourly");

        Assert.assertTrue(smartRecords.size() == 42);

        return  AssertSmartRecords(smartRecords);
    }


    public void testNormalUsers(Map<Double, Double> weightToScore) throws GeneratorException {
        mongoTemplate.getCollectionNames().forEach(collection -> mongoTemplate.dropCollection(collection));

        int duration = 3;
        int daysBackTo = GENERATOR_DAYS_BACK_FROM - duration;
        Instant start = START_DATE;
        Instant end =  START_DATE.plus(Duration.ofDays(duration));

        List<String> contextIds = new ArrayList<>();
        contextIds.add("user2");
        contextIds.add("user3");
        generateAggregatedFeatureEventConf(daysBackTo, contextIds);

        while(start.isBefore(end)){
            createWeightModel(start);
            createSmartValuesPriorModel(start);
            for(int i=0; i< contextIds.size();i++){
                createNormalUserSmartValuesModel(start, "userId#"+contextIds.get(i));
            }
            start = start.plus(Duration.ofDays(2));
        }

        String command = String.format("process --smart_record_conf_name %s --start_date %s --end_date %s", "userId_hourly", START_DATE.toString(), end);

        executeAndAssertCommandSuccess(command);
        List<SmartRecord> smartRecords = mongoTemplate.findAll(SmartRecord.class, "smart_userId_hourly");


        contextIds.forEach(contextId -> {
            List<SmartRecord> contextIdSmartRecords = smartRecords.stream().filter(smart ->  smart.getContextId().equals("userId#"+contextId)).collect(Collectors.toList());
            Map<Double, Double> contextIdWeightToScore = AssertSmartRecords(contextIdSmartRecords);
            Assert.assertTrue(contextIdWeightToScore.equals(weightToScore));
        });
    }



    //Assert that feature with higher weight has higher smart score than feature with lower weight
    // and features with same weight has same smart scores.
    private Map<Double, Double> AssertSmartRecords(List<SmartRecord> smartRecords){
        Map<Double, Double> weightToScore = new HashMap<>();
        weightToFeatures.forEach((weight, features) -> {
            features.forEach(featureName -> {
                List<Double> filteredSmartsScoreByFeature = smartRecords.stream().filter(s ->{
                    List<AdeAggregationRecord>  adeAggregationRecords = s.getAggregationRecords();
                    return adeAggregationRecords.stream().anyMatch(a-> a.getFeatureName().equals(featureName));
                }).map(s -> s.getScore()).collect(Collectors.toList());

                filteredSmartsScoreByFeature.forEach(smartScore -> {
                    if (weightToScore.containsKey(weight)) {
                        Double score = weightToScore.get(weight);
                        Assert.assertTrue(score.equals(smartScore));
                    } else {
                        weightToScore.put(weight, smartScore);
                        List<Double> lowerScores = weightToScore.entrySet().stream().filter((map) -> map.getKey() < weight).map(Map.Entry::getValue).collect(Collectors.toList());
                        Assert.assertTrue(lowerScores.stream().filter(score -> smartScore <= score).collect(Collectors.toList()).size() == 0);
                        List<Double> higherScores = weightToScore.entrySet().stream().filter((map) -> map.getKey() > weight).map(Map.Entry::getValue).collect(Collectors.toList());
                        Assert.assertTrue(higherScores.stream().filter(score -> smartScore >= score).collect(Collectors.toList()).size() == 0);
                    }
                });
            });
        });

        return weightToScore;
    }


    /**
     * Split features to groups, where each group has same weight.
     * Build weight to featuresNames map.
     */
    private void createFeaturesGroups() {
        List<AggregatedFeatureEventConf> aggregatedFeatureEventConfList = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList();
        Double weight = START_WEIGHT;
        weightToFeatures = new HashMap<>();

        List<List<AggregatedFeatureEventConf>> partitions = Lists.partition(aggregatedFeatureEventConfList, NUM_OF_GROUPS);

        List<List<String>> featuresGroups =  partitions.stream().map(list-> {
            return list.stream().map(p -> p.getName()).collect(Collectors.toList());
        }).collect(Collectors.toList());

        for (List<String> featuresGroup : featuresGroups) {
            weightToFeatures.put(weight, featuresGroup);
            weight = weight - WEIGHT_DECREASED_VALUE;
        }
    }



    /**
     * Create WeightModel.
     * Use weightToFeatures map to fill clusterConfs.
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
     * Normal user behavior is: numOfZeroValues=300, numOfPositiveValues=60, avgFeatureValue=50
     * @param end end instant of model
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
     * The model represwent user with avg FeatureValue 50.
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
     * Generate aggregated Features
     * @param daysBackTo
     * @param contextIds generate aggregated Features for context ids
     * @throws GeneratorException
     */
    private void generateAggregatedFeatureEventConf(int daysBackTo,  List<String> contextIds) throws GeneratorException {

        IStringListGenerator contextIdGenerator = new FixedListGenerator(contextIds);
        TimeGenerator startInstantGenerator = new TimeGenerator(LocalTime.of(GENERATOR_START_HOUR_OF_DAY, 0), LocalTime.of(GENERATOR_END_HOUR_OF_DAY, 0), 60, GENERATOR_DAYS_BACK_FROM, daysBackTo);

        //Generate F:
        ScoredFeatureAggregationRecordHourlyGenerator scoredGenerator =
                new ScoredFeatureAggregationRecordHourlyGenerator(aggregatedFeatureToScoreGenerator, contextIdGenerator, 0.0, new ArrayList<>(), startInstantGenerator);
        List<AdeAggregationRecord> adeScoredAggregationRecords = scoredGenerator.generate();

        //Generate P:
        startInstantGenerator = new TimeGenerator(LocalTime.of(GENERATOR_START_HOUR_OF_DAY, 0), LocalTime.of(GENERATOR_END_HOUR_OF_DAY, 0), 60, GENERATOR_DAYS_BACK_FROM, daysBackTo);
        AdeAggregationRecordHourlyGenerator generator =
                new AdeAggregationRecordHourlyGenerator(aggregatedFeatureToValueGenerator, startInstantGenerator, contextIdGenerator);
        List<AdeAggregationRecord> adeAggregationRecords = generator.generate();


        aggregatedDataStore.store(adeAggregationRecords, AggregatedFeatureType.SCORE_AGGREGATION);
        aggregatedDataStore.store(adeScoredAggregationRecords, AggregatedFeatureType.FEATURE_AGGREGATION);
    }

    /**
     * Create generators for F and P.
     */
    private void createAggregatedFeatureGenerators(){
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
