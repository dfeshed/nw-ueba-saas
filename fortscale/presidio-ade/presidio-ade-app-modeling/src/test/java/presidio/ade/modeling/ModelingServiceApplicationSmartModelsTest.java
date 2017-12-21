package presidio.ade.modeling;

import com.google.common.collect.Lists;
import fortscale.aggregation.configuration.AslConfigurationPaths;
import fortscale.aggregation.configuration.AslResourceFactory;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.ml.model.*;
import fortscale.ml.model.builder.smart_weights.WeightsModelBuilderConf;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStore;
import fortscale.smart.record.conf.ClusterConf;
import fortscale.smart.record.conf.SmartRecordConfService;
import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
import fortscale.utils.elasticsearch.config.EmbeddedElasticsearchInitialiser;
import fortscale.utils.shell.BootShim;
import fortscale.utils.shell.BootShimConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.category.ModuleTestCategory;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import javafx.util.Pair;
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
import presidio.monitoring.spring.PresidioMonitoringConfiguration;

import java.time.Instant;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by barak_schuster on 9/4/17.
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
    private ModelStore modelStore;
    @Autowired
    private SmartAccumulationDataStore smartAccumulationDataStore;
    @Autowired
    private ModelingService modelingService;
    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
    @Autowired
    private SmartRecordConfService smartRecordConfService;
    @Autowired
    private AslResourceFactory aslResourceFactory;
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
     *
     * @throws GeneratorException
     */
    @Test
    public void simpleWeightModelWithDescendingScoreTest() throws GeneratorException {
        int groupSize = 8;
        double score = 100.0;
        int scoreInterval = 10;
        int probability = 100;
        int probabilityInterval = 0;
        int numOfSmarts = 1;
        int daysBackFrom = 2;
        int daysBackTo = 1;
        LinkedHashMap<List<AggregatedFeatureEventConf>, Pair<Double, Integer>> featuresGroupToScoreAndProbabilityMap = createFeaturesGroup(groupSize, score, scoreInterval, probability, probabilityInterval);

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
     *
     * @throws GeneratorException
     */
    @Test
    public void weightModelWithDescendingScoreAndSameProbabilityTest() throws GeneratorException {
        int groupSize = 6;
        int numOfSmarts = 50;
        double score = 100.0;
        int scoreInterval = 10;
        int probability = 10;
        int probabilityInterval = 0;
        int daysBackFrom = 30;
        int daysBackTo = 1;
        String generatorContextIdPattern = "userId\\#[a-g]{1}[1-9]{1}";

        LinkedHashMap<List<AggregatedFeatureEventConf>, Pair<Double, Integer>> featuresGroupToScoreAndProbabilityMap = createFeaturesGroup(groupSize, score, scoreInterval, probability, probabilityInterval);

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
     *
     * @throws GeneratorException
     */
    @Test
    public void weightModelWithSameScoreAndDescendingProbabilityTest() throws GeneratorException {
        int groupSize = 6;
        int numOfSmarts = 50;
        double score = 60.0;
        int scoreInterval = 0;
        int probability = 25;
        int probabilityInterval = 2;
        int daysBackFrom = 30;
        int daysBackTo = 1;
        String generatorContextIdPattern = "userId\\#[a-g]{1}[1-9]{1}";

        LinkedHashMap<List<AggregatedFeatureEventConf>, Pair<Double, Integer>> featuresGroupToScoreAndProbabilityMap = createFeaturesGroup(groupSize, score, scoreInterval, probability, probabilityInterval);

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
     *
     * @throws GeneratorException
     */
    @Test
    public void weightModelWithSameFeatureForSmartsIDayAndDescendingScoreTest() throws GeneratorException {
        int groupSize = 4;
        double firstScore = 100.0;
        double secondScore = 55.0;
        int probability = 100;
        int numOfSmarts = 10;

        List<AggregatedFeatureEventConf> features = getAggregatedFeatureWithoutZeroWeightFeatures();
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
     * @throws GeneratorException
     */
    public List<String> GenerateAccumulatedSmartsWithFeaturesGroup(List<AggregatedFeatureEventConf> group, Double score, int probability, int numOfSmarts, String generatorContextIdPattern, int daysBackFrom, int daysBackTo) throws GeneratorException {
        LinkedHashMap<List<AggregatedFeatureEventConf>, Pair<Double, Integer>> featuresGroupToScoreAndProbabilityMap = new LinkedHashMap<>();
        featuresGroupToScoreAndProbabilityMap.put(group, new Pair<>(score, probability));
        GenerateAccumulatedSmarts(featuresGroupToScoreAndProbabilityMap, numOfSmarts, generatorContextIdPattern, daysBackFrom, daysBackTo);
        return group.stream().map(features -> features.getName()).collect(Collectors.toList());
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

        for (List<AggregatedFeatureEventConf> aggregatedFeatureEventConf : featuresGroupToScoreAndProbabilityMap.keySet()) {
            List<String> features = aggregatedFeatureEventConf.stream().map(feature -> feature.getName()).collect(Collectors.toList());

            //suppose that each cluster contains 1 feature
            List<ClusterConf> filteredClusterConf = clusterConfs.stream().filter(clusterConf -> features.contains(clusterConf.getAggregationRecordNames().get(0))).collect(Collectors.toList());
            Double avgFeaturesWeight = filteredClusterConf.stream().mapToDouble(conf -> conf.getWeight()).average().getAsDouble();

            Assert.assertTrue(oneBeforeCurrentAvgWeight < avgFeaturesWeight);
            oneBeforeCurrentAvgWeight = avgFeaturesWeight;
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
        firstClusterConfGroup.forEach(conf -> Assert.assertTrue(conf.getWeight().equals(firstGroupWeight)));

        Double secondGroupWeight = secondClusterConfGroup.get(0).getWeight();
        secondClusterConfGroup.forEach(conf -> Assert.assertTrue(conf.getWeight().equals(secondGroupWeight)));

        Assert.assertTrue(firstGroupWeight > secondGroupWeight);
    }


    /**
     * Generate AccumulatedSmarts with context generator, time generator and featuresGroupToScore map.
     *
     * @param featuresGroupToScoreAndProbabilityMap List<List<featureConf>, pair<score, probability>>
     * @throws GeneratorException
     */
    public void GenerateAccumulatedSmarts(LinkedHashMap<List<AggregatedFeatureEventConf>, Pair<Double, Integer>> featuresGroupToScoreAndProbabilityMap, int numOfSmarts, String generatorContextIdPattern, int daysBackFrom, int daysBackTo) throws GeneratorException {
        IStringGenerator contextIdGenerator = new StringRegexCyclicValuesGenerator(generatorContextIdPattern);
        TimeGenerator startInstantGenerator = new MinutesIncrementTimeGenerator(LocalTime.of(0, 0), LocalTime.of(0, 0), 1440, daysBackFrom, daysBackTo);
        AccumulatedSmartsDailyGenerator accumulatedSmartsGenerator = new AccumulatedSmartsDailyGenerator(contextIdGenerator, startInstantGenerator, featuresGroupToScoreAndProbabilityMap, numOfSmarts);
        List<AccumulatedSmartRecord> accumulatedSmartRecords = accumulatedSmartsGenerator.generate();
        smartAccumulationDataStore.store(accumulatedSmartRecords, "userId_hourly");
    }

    /**
     * Create features group to score map
     *
     * @param groupSize     group size
     * @param score         initial score
     * @param scoreInterval interval for score descending
     * @return features group to score and probability map
     */
    public LinkedHashMap<List<AggregatedFeatureEventConf>, Pair<Double, Integer>> createFeaturesGroup(int groupSize, Double score, int scoreInterval, int probability, int probabilityInterval) {
        List<AggregatedFeatureEventConf> features = getAggregatedFeatureWithoutZeroWeightFeatures();
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
     * Create features group
     *
     * @param groupSize   group size
     * @param firstScore
     * @param secondScore
     * @param probability
     * @return
     */
    public LinkedHashMap<List<AggregatedFeatureEventConf>, Pair<Double, Integer>> createFeaturesGroupPairs(int groupSize, Double firstScore, Double secondScore, int probability) {
        List<AggregatedFeatureEventConf> features = getAggregatedFeatureWithoutZeroWeightFeatures();
        List<List<AggregatedFeatureEventConf>> featureGroups = Lists.partition(features, groupSize);
        LinkedHashMap<List<AggregatedFeatureEventConf>, Pair<Double, Integer>> featuresGroupToScoreAndProbabilityMap = new LinkedHashMap<>();

        Iterator<List<AggregatedFeatureEventConf>> iterator = featureGroups.iterator();
        if (iterator.hasNext()) {
            List<AggregatedFeatureEventConf> firstGroup = iterator.next();

            while (iterator.hasNext()) {
                List<AggregatedFeatureEventConf> secondGroup = iterator.next();

                featuresGroupToScoreAndProbabilityMap.put(firstGroup, new Pair<>(firstScore, probability));
                featuresGroupToScoreAndProbabilityMap.put(secondGroup, new Pair<>(secondScore, probability));

                firstGroup = secondGroup;

            }
        }
        return featuresGroupToScoreAndProbabilityMap;
    }


    /**
     * Filter zeroWeightFeatures
     *
     * @return aggregatedFeature list without zeroWeightFeatures
     */
    private List<AggregatedFeatureEventConf> getAggregatedFeatureWithoutZeroWeightFeatures() {
        Collection<AslConfigurationPaths> modelConfigurationPathsCollection = Arrays.asList(
                new AslConfigurationPaths(groupName, smartRecordsBaseConfigurationPath));
        ModelConfServiceBuilder modelConfServiceBuilder = new ModelConfServiceBuilder(modelConfigurationPathsCollection, aslResourceFactory);

        ModelConfService modelConfService = modelConfServiceBuilder.buildModelConfService(groupName);
        List<ModelConf> modelConfs = modelConfService.getModelConfs();

        List<String> zeroWeightFeatures = new ArrayList<>();

        List<ModelConf> filteredWeightsModel = modelConfs.stream().filter(modelConf -> modelConf.getModelBuilderConf() instanceof WeightsModelBuilderConf).collect(Collectors.toList());

        for (ModelConf modelConf : filteredWeightsModel) {
            zeroWeightFeatures.addAll(((WeightsModelBuilderConf) modelConf.getModelBuilderConf()).getZeroWeightFeatures());
        }

        List<AggregatedFeatureEventConf> aggregatedFeatureEventConfList = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList();
        List<String> excludedAggregationRecords = smartRecordConfService.getSmartRecordConf("userId_hourly").getExcludedAggregationRecords();
        return aggregatedFeatureEventConfList.stream()
                .filter(aggregatedFeatureEventConf -> !zeroWeightFeatures.contains(aggregatedFeatureEventConf.getName()))
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
        public static TestPropertiesPlaceholderConfigurer continousModelingServiceConfigurationTestPropertiesPlaceholderConfigurer() {
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
