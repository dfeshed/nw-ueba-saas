package presidio.ade.modeling;

import com.google.common.collect.Lists;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.ml.model.SmartWeightsModel;
import fortscale.ml.model.store.ModelDAO;
import fortscale.smart.record.conf.ClusterConf;
import fortscale.smart.record.conf.SmartRecordConfService;
import fortscale.utils.data.Pair;
import fortscale.utils.shell.BootShim;
import fortscale.utils.shell.BootShimConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.store.record.StoreMetadataProperties;
import fortscale.utils.test.category.ModuleTestCategory;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.shell.core.CommandResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.domain.record.accumulator.AccumulatedSmartRecord;
import presidio.ade.domain.store.accumulator.smart.SmartAccumulationDataStore;
import presidio.ade.domain.store.accumulator.smart.SmartAccumulationDataStoreConfig;
import presidio.ade.modeling.config.ModelingServiceConfiguration;
import presidio.ade.test.utils.generators.AccumulatedSmartsDailyGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.StringRegexCyclicValuesGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.generators.common.time.TimeGenerator;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;

import java.time.Instant;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static fortscale.ml.model.builder.smart_weights.WeightsModelBuilderAlgorithm.MIN_ALLOWED_WEIGHT_DEFAULT;

/**
 * @author Barak Schuster.
 * @author Lior Govrin.
 */
@Category(ModuleTestCategory.class)
@ContextConfiguration
@RunWith(SpringRunner.class)
public class ModelingServiceApplicationSmartModelsTest {

    @Autowired
    private BootShim bootShim;
    @Autowired
    protected MongoTemplate mongoTemplate;
    @Autowired
    private SmartAccumulationDataStore smartAccumulationDataStore;
    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
    @Autowired
    private SmartRecordConfService smartRecordConfService;
    @Value("${presidio.ade.modeling.smart.records.base.configuration.path}")
    private String smartRecordsBaseConfigurationPath;
    @Value("${presidio.ade.modeling.feature.aggregation.records.group.name}")
private String groupName;
    @MockBean
    MetricCollectingService metricCollectingService;
    @MockBean
    MetricsExporter metricsExporter;

    private static final String FEATURE_AGGREGATION_RECORDS_LINE_FORMAT = "process --group_name smart-record-models --session_id test-run --end_date %s";

    @Before
    public void setup() {
        mongoTemplate.getCollectionNames().forEach(collection -> mongoTemplate.dropCollection(collection));
    }

    /**
     * Simple daily weightModel test.
     * Features divided to groups, where each group belongs to another smart and has descending score.
     * <p>
     * Expected result:
     * Descending avg weight between groups.
     */
    @Test
    public void simpleWeightModelWithDescendingScoreTest() throws GeneratorException {
        int numOfGroups = 6;
        double score = 100.0;
        int scoreInterval = 10;
        int probability = 100;
        int probabilityInterval = 0;
        int numOfSmarts = 1;
        int daysBackFrom = 2;
        int daysBackTo = 1;
        LinkedHashMap<List<AggregatedFeatureEventConf>, Pair<Double, Integer>> featuresGroupToScoreAndProbabilityMap = createFeaturesGroup(numOfGroups, score, scoreInterval, probability, probabilityInterval);

        List<String> ContextIdPatternList = createContextIdPatternList(featuresGroupToScoreAndProbabilityMap.size());

        int contextIdPatternIndex = 0;
        for (Map.Entry<List<AggregatedFeatureEventConf>, Pair<Double, Integer>> featuresGroupToScore : featuresGroupToScoreAndProbabilityMap.entrySet()) {
            String generatorContextIdPattern = ContextIdPatternList.get(contextIdPatternIndex);
            LinkedHashMap<List<AggregatedFeatureEventConf>, Pair<Double, Integer>> singleFeaturesGroupToScoreMap = new LinkedHashMap<>();
            singleFeaturesGroupToScoreMap.put(featuresGroupToScore.getKey(), featuresGroupToScore.getValue());
            GenerateAccumulatedSmarts(singleFeaturesGroupToScoreMap, numOfSmarts, generatorContextIdPattern, daysBackFrom, daysBackTo);
            contextIdPatternIndex++;
        }

        Instant end = Instant.now();
        CommandResult commandResult = bootShim.getShell().executeCommand(String.format(FEATURE_AGGREGATION_RECORDS_LINE_FORMAT, end));
        Assert.assertTrue(commandResult.isSuccess());
        AssertDescendingWeightAvgBetweenGroups(featuresGroupToScoreAndProbabilityMap, end);
    }


    /**
     * Features divided to groups with descending score between groups.
     * Each feature has same probability to belong to smart.
     * <p>
     * Expected result:
     * Descending avg weight between groups.
     */
    @Test
    public void weightModelWithDescendingScoreAndSameProbabilityTest() throws GeneratorException {
        int numOfGroups = 6;
        int numOfSmarts = 50;
        double score = 100.0;
        int scoreInterval = 10;
        int probability = 10;
        int probabilityInterval = 0;
        int daysBackFrom = 30;
        int daysBackTo = 1;
        String generatorContextIdPattern = "userId\\#[a-g]{1}[1-9]{1}";

        LinkedHashMap<List<AggregatedFeatureEventConf>, Pair<Double, Integer>> featuresGroupToScoreAndProbabilityMap = createFeaturesGroup(numOfGroups, score, scoreInterval, probability, probabilityInterval);

        GenerateAccumulatedSmarts(featuresGroupToScoreAndProbabilityMap, numOfSmarts, generatorContextIdPattern, daysBackFrom, daysBackTo);

        Instant end = Instant.now();
        CommandResult commandResult = bootShim.getShell().executeCommand(String.format(FEATURE_AGGREGATION_RECORDS_LINE_FORMAT, end));
        Assert.assertTrue(commandResult.isSuccess());
        AssertDescendingWeightAvgBetweenGroups(featuresGroupToScoreAndProbabilityMap, end);
    }

    /**
     * Features divided to groups with same score.
     * Each feature has descending probability to belong to smart between groups.
     * <p>
     * Expected result:
     * Descending avg weight between groups.
     */
    @Test
    public void weightModelWithSameScoreAndDescendingProbabilityTest() throws GeneratorException {
        int numOfGroups = 7;
        int numOfSmarts = 50;
        double score = 60.0;
        int scoreInterval = 0;
        int probability = 25;
        int probabilityInterval = 2;
        int daysBackFrom = 30;
        int daysBackTo = 1;
        String generatorContextIdPattern = "userId\\#[a-g]{1}[1-9]{1}";

        LinkedHashMap<List<AggregatedFeatureEventConf>, Pair<Double, Integer>> featuresGroupToScoreAndProbabilityMap = createFeaturesGroup(numOfGroups, score, scoreInterval, probability, probabilityInterval);

        GenerateAccumulatedSmarts(featuresGroupToScoreAndProbabilityMap, numOfSmarts, generatorContextIdPattern, daysBackFrom, daysBackTo);

        Instant end = Instant.now();
        CommandResult commandResult = bootShim.getShell().executeCommand(String.format(FEATURE_AGGREGATION_RECORDS_LINE_FORMAT, end));
        Assert.assertTrue(commandResult.isSuccess());
        AssertDescendingWeightAvgBetweenGroups(featuresGroupToScoreAndProbabilityMap, end);
    }

    /**
     * Create 2 groups of features with different 4 features, where first group has high score and second group has low score.
     * 1 day - 10 smarts will be generated with first group.
     * 29 days - 10 smarts will generated with second group per day.
     * <p>
     * results:
     * weight of second group features should be lower than weight of first group.
     */
    @Test
    public void weightModelWithSameFeatureForSmartsIDayAndDescendingScoreTest() throws GeneratorException {
        int groupSize = 4;
        double firstScore = 100.0;
        double secondScore = 55.0;
        int probability = 100;
        int numOfSmarts = 10;

        List<AggregatedFeatureEventConf> features = getIncludedAggregatedFeatureEventConfs();
        List<List<AggregatedFeatureEventConf>> featureGroups = Lists.partition(features, groupSize);

        int contextIdPatternIndex = 0;
        List<String> ContextIdPatternList = createContextIdPatternList(featureGroups.size());
        Iterator<List<AggregatedFeatureEventConf>> iterator = featureGroups.iterator();

        if (iterator.hasNext()) {
            List<AggregatedFeatureEventConf> firstGroup = iterator.next();

            while (iterator.hasNext()) {
                mongoTemplate.getCollectionNames().forEach(collection -> mongoTemplate.dropCollection(collection));

                List<AggregatedFeatureEventConf> secondGroup = iterator.next();
                String generatorContextIdPattern = ContextIdPatternList.get(contextIdPatternIndex);

                List<String> firstFeaturesGroup = GenerateAccumulatedSmartsWithFeaturesGroup(firstGroup, firstScore, probability, numOfSmarts, generatorContextIdPattern, 2, 1);
                List<String> secondFeaturesGroup = GenerateAccumulatedSmartsWithFeaturesGroup(secondGroup, secondScore, probability, numOfSmarts, generatorContextIdPattern, 32, 2);

                firstGroup = secondGroup;
                contextIdPatternIndex++;

                Instant end = Instant.now();
                CommandResult commandResult = bootShim.getShell().executeCommand(String.format(FEATURE_AGGREGATION_RECORDS_LINE_FORMAT, end));
                Assert.assertTrue(commandResult.isSuccess());

                AssertWeightsOfTwoGroups(firstFeaturesGroup, secondFeaturesGroup, end);
            }
        }
    }

    /**
     * @param group                     group of features
     * @param score                     score of features
     * @param probability               probability
     * @param numOfSmarts               num of smarts
     * @param generatorContextIdPattern generatorContextIdPattern
     * @param daysBackFrom              daysBackFrom
     * @param daysBackTo                daysBackTo
     */
    public List<String> GenerateAccumulatedSmartsWithFeaturesGroup(List<AggregatedFeatureEventConf> group, Double score, int probability, int numOfSmarts, String generatorContextIdPattern, int daysBackFrom, int daysBackTo) throws GeneratorException {
        LinkedHashMap<List<AggregatedFeatureEventConf>, Pair<Double, Integer>> featuresGroupToScoreAndProbabilityMap = new LinkedHashMap<>();
        featuresGroupToScoreAndProbabilityMap.put(group, new Pair<>(score, probability));
        GenerateAccumulatedSmarts(featuresGroupToScoreAndProbabilityMap, numOfSmarts, generatorContextIdPattern, daysBackFrom, daysBackTo);
        return group.stream().map(AggregatedFeatureEventConf::getName).collect(Collectors.toList());
    }


    /**
     * Assert descending weight avg between groups
     */
    public void AssertDescendingWeightAvgBetweenGroups(LinkedHashMap<List<AggregatedFeatureEventConf>, Pair<Double, Integer>> featuresGroupToScoreAndProbabilityMap, Instant end) {
        Query query = new Query()
                .addCriteria(Criteria.where(ModelDAO.END_TIME_FIELD).is(end));
        List<ModelDAO> weightModel = mongoTemplate.find(query, ModelDAO.class, "model_smart.global.weights.userId.hourly");

        Double oneBeforeCurrentAvgWeight = 0.0;
        List<ClusterConf> clusterConfs = ((SmartWeightsModel) weightModel.get(0).getModel()).getClusterConfs();

        List<String> prevFeatures = null;
        for (List<AggregatedFeatureEventConf> aggregatedFeatureEventConf : featuresGroupToScoreAndProbabilityMap.keySet()) {
            List<String> features = aggregatedFeatureEventConf.stream().map(AggregatedFeatureEventConf::getName).collect(Collectors.toList());

            //suppose that each cluster contains 1 feature
            List<ClusterConf> filteredClusterConf = clusterConfs.stream().filter(clusterConf -> features.contains(clusterConf.getAggregationRecordNames().get(0))).collect(Collectors.toList());
            Double avgFeaturesWeight = filteredClusterConf.stream().mapToDouble(ClusterConf::getWeight).average().getAsDouble();
            if(prevFeatures != null && oneBeforeCurrentAvgWeight >= avgFeaturesWeight){
                StringBuilder builder = new StringBuilder();
                builder.append(String.format("oneBeforeCurrentAvgWeight: %f, avgFeaturesWeight:%f\n", oneBeforeCurrentAvgWeight, avgFeaturesWeight));
                builder.append("prevFeatures: " + prevFeatures+ "\n");
                builder.append("features: " + features + "\n");
                builder.append(String.format("featuresGroupToScoreAndProbabilityMap (class: %s):\n",featuresGroupToScoreAndProbabilityMap.getClass()));
                for(Map.Entry<List<AggregatedFeatureEventConf>, Pair<Double, Integer>> entry: featuresGroupToScoreAndProbabilityMap.entrySet()){
                    builder.append(String.format("list class: %s \n", entry.getKey().getClass()));
                    List<String> featuresTmp = entry.getKey().stream().map(AggregatedFeatureEventConf::getName).collect(Collectors.toList());
                    builder.append(String.format("features: %s\n", featuresTmp));
                    builder.append(String.format("score: %f prob:%d\n", entry.getValue().getKey(), entry.getValue().getValue()));
                }
                Assert.fail(builder.toString());
            }
            oneBeforeCurrentAvgWeight = avgFeaturesWeight;
            prevFeatures = features;
        }
    }


    /**
     * Assert that weight of oneDayFeatures(features with high score for 1 day)
     * greater than weight of restDaysFeatures (features with low score for 29 days).     *
     * <p>
     * Assert that features in each group get same weight.
     */
    public void AssertWeightsOfTwoGroups(List<String> highScoreFeaturesForOneDay, List<String> lowScoreFeaturesForRestDays, Instant end) {
        Query query = new Query()
                .addCriteria(Criteria.where(ModelDAO.END_TIME_FIELD).is(end));
        List<ModelDAO> weightModel = mongoTemplate.find(query, ModelDAO.class, "model_smart.global.weights.userId.hourly");

        List<ClusterConf> clusterConfs = ((SmartWeightsModel) weightModel.get(0).getModel()).getClusterConfs();

        List<ClusterConf> firstClusterConfGroup = clusterConfs.stream().filter(clusterConf -> highScoreFeaturesForOneDay.contains(clusterConf.getAggregationRecordNames().get(0))).collect(Collectors.toList());
        List<ClusterConf> secondClusterConfGroup = clusterConfs.stream().filter(clusterConf -> lowScoreFeaturesForRestDays.contains(clusterConf.getAggregationRecordNames().get(0))).collect(Collectors.toList());

        Double firstGroupWeight = firstClusterConfGroup.get(0).getWeight();
        firstClusterConfGroup.forEach(conf -> Assert.assertEquals(conf.getWeight(), firstGroupWeight));

        Double secondGroupWeight = secondClusterConfGroup.get(0).getWeight();
        secondClusterConfGroup.forEach(conf -> Assert.assertEquals(conf.getWeight(), secondGroupWeight));

        Assert.assertTrue(firstGroupWeight > secondGroupWeight);
    }


    /**
     * Generate AccumulatedSmarts with context generator, time generator and featuresGroupToScore map.
     *
     * @param featuresGroupToScoreAndProbabilityMap List<List<featureConf>, pair<score, probability>>
     */
    public void GenerateAccumulatedSmarts(LinkedHashMap<List<AggregatedFeatureEventConf>, Pair<Double, Integer>> featuresGroupToScoreAndProbabilityMap, int numOfSmarts, String generatorContextIdPattern, int daysBackFrom, int daysBackTo) throws GeneratorException {
        IStringGenerator contextIdGenerator = new StringRegexCyclicValuesGenerator(generatorContextIdPattern);
        TimeGenerator startInstantGenerator = new MinutesIncrementTimeGenerator(LocalTime.of(0, 0), LocalTime.of(0, 0), 1440, daysBackFrom, daysBackTo);
        AccumulatedSmartsDailyGenerator accumulatedSmartsGenerator = new AccumulatedSmartsDailyGenerator(contextIdGenerator, startInstantGenerator, featuresGroupToScoreAndProbabilityMap, numOfSmarts);
        List<AccumulatedSmartRecord> accumulatedSmartRecords = accumulatedSmartsGenerator.generate();
        smartAccumulationDataStore.store(accumulatedSmartRecords, "userId_hourly", new StoreMetadataProperties());
    }

    /**
     * Create features group to score map
     *
     * @param numOfGroups   number of groups
     * @param score         initial score
     * @param scoreInterval interval for score descending
     * @return features group to score and probability map
     */
    public LinkedHashMap<List<AggregatedFeatureEventConf>, Pair<Double, Integer>> createFeaturesGroup(int numOfGroups, Double score, int scoreInterval, int probability, int probabilityInterval) {
        List<AggregatedFeatureEventConf> features = getIncludedAggregatedFeatureEventConfs();
        int groupSize = features.size()/numOfGroups;
        List<List<AggregatedFeatureEventConf>> featureGroups = Lists.partition(features, groupSize);
        LinkedHashMap<List<AggregatedFeatureEventConf>, Pair<Double, Integer>> featuresGroupToScoreAndProbabilityMap = new LinkedHashMap<>();

        for (List<AggregatedFeatureEventConf> featureGroup : featureGroups) {
            featuresGroupToScoreAndProbabilityMap.put(featureGroup, new Pair<>(score, probability));
            score = score - scoreInterval;
            probability = probability - probabilityInterval;
        }
        return featuresGroupToScoreAndProbabilityMap;
    }

    /**
     * Filter out aggregation records with weights less than the minimum threshold and excluded aggregation records.
     *
     * @return only included aggregated feature event confs.
     */
    private List<AggregatedFeatureEventConf> getIncludedAggregatedFeatureEventConfs() {
        Set<String> aggregationRecordNamesWithLowWeights = smartRecordConfService.getSmartRecordConf("userId_hourly").getClusterConfs().stream()
                .filter(clusterConf -> clusterConf.getWeight() < MIN_ALLOWED_WEIGHT_DEFAULT)
                .map(ClusterConf::getAggregationRecordNames)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        List<String> excludedAggregationRecords = smartRecordConfService.getSmartRecordConf("userId_hourly").getExcludedAggregationRecords();
        return aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList().stream()
                .filter(aggregatedFeatureEventConf -> aggregatedFeatureEventConf.getBucketConf().getContextFieldNames().size() == 1 && aggregatedFeatureEventConf.getBucketConf().getContextFieldNames().get(0).equals("userId"))
                .filter(aggregatedFeatureEventConf -> !aggregationRecordNamesWithLowWeights.contains(aggregatedFeatureEventConf.getName()))
                .filter(aggregatedFeatureEventConf -> !excludedAggregationRecords.contains(aggregatedFeatureEventConf.getName()))
                .collect(Collectors.toList());
    }


    /**
     * Create contextIdPattern list
     *
     * @param size list size
     * @return contextIdPattern list
     */
    public List<String> createContextIdPatternList(int size) {
        List<String> ContextIdPatternList = new ArrayList<>();
        for (int i = 65; i < 65 + size; i++) {
            ContextIdPatternList.add("userId\\#[" + (char) i + "]{1}[1-2]{1}");
        }
        return ContextIdPatternList;
    }


    @Configuration
    @Import({MongodbTestConfig.class, BootShimConfig.class, ModelingServiceConfiguration.class, SmartAccumulationDataStoreConfig.class})
    public static class ModelingServiceApplicationSmartModelsTestConfig {
        @Bean
        public static TestPropertiesPlaceholderConfigurer continuousModelingServiceConfigurationTestPropertiesPlaceholderConfigurer() {
            Properties properties = new Properties();
            // Feature bucket conf service
            properties.put("presidio.ade.modeling.feature.bucket.confs.base.path", "classpath*:config/asl/feature-buckets/**/*.json");
            // Feature aggregation event conf service
            properties.put("presidio.ade.modeling.feature.aggregation.event.confs.base.path", "classpath*:config/asl/aggregation-records/**/*.json");
            // Smart event conf service
            properties.put("presidio.ade.smart.record.base.configurations.path", "classpath*:config/asl/smart-records/*");
            // Model conf service
            properties.put("presidio.ade.modeling.enriched.records.group.name", "enriched-record-models");
            properties.put("presidio.ade.modeling.enriched.records.base.configuration.path", "classpath*:config/asl/models/enriched-records/");
            properties.put("presidio.ade.modeling.feature.aggregation.records.group.name", "feature-aggregation-record-models");
            properties.put("presidio.ade.modeling.feature.aggregation.records.base.configuration.path", "classpath*:config/asl/models/feature-aggregation-records/");
            properties.put("presidio.ade.modeling.smart.records.group.name", "smart-record-models");
            properties.put("presidio.ade.modeling.smart.records.base.configuration.path", "classpath*:config/asl/models/smart-records/");
            // Additional properties
            properties.put("fortscale.model.retriever.smart.oldestAllowedModelDurationDiff", "PT48H");
            properties.put("presidio.default.ttl.duration", "PT1000H");
            properties.put("presidio.default.cleanup.interval", "PT2000H");
            return new TestPropertiesPlaceholderConfigurer(properties);
        }
    }
}
