package fortscale.ml.scorer;

import fortscale.common.feature.CategoricalFeatureValue;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.builder.CategoryRarityModelBuilder;
import fortscale.ml.model.builder.CategoryRarityModelBuilderConf;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.model.metrics.CategoryRarityModelBuilderMetricsContainer;
import fortscale.ml.scorer.record.TestAdeRecord;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.domain.record.AdeRecordReader;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/model-scorer-tests-context.xml"})
public class CategoryRarityModelScorerTest {

    @MockBean
    ModelsCacheService modelsCacheService;
    private CategoryRarityModelBuilderMetricsContainer categoryRarityMetricsContainer = mock(CategoryRarityModelBuilderMetricsContainer.class);

    @Autowired
    EventModelsCacheService eventModelsCacheService;

    private static final double X_WITH_VALUE_HALF_FACTOR = 0.3333333333333333;
    private static final double MIN_PROBABILITY = 0.7;

    private void assertScorer(CategoryRarityModelScorer scorer, CategoryRarityModelScorerParams params) {

        Assert.assertEquals(params.getName(), scorer.getName());
        Assert.assertEquals(params.getModelName(), scorer.getModelName());
        Assert.assertEquals(params.getContextFieldNames(), scorer.getContextFieldNames());
        Assert.assertEquals(params.getFeatureName(), scorer.getFeatureName());
        Assert.assertEquals((long)params.getMaxRareCount(), scorer.getAlgorithm().getMaxRareCount());
        Assert.assertEquals((long)params.getMaxNumOfRarePartitions(), scorer.getAlgorithm().getMaxNumOfRarePartitions());
        Assert.assertEquals((long)params.getNumberOfPartitionsToInfluenceEnough(), scorer.getEnoughNumOfPartitionsToInfluence());
        Assert.assertEquals((long)params.getMinNumOfPartitionsToInfluence(), scorer.getMinNumOfPartitionsToInfluence());
        Assert.assertEquals(params.getUseCertaintyToCalculateScore(), scorer.isUseCertaintyToCalculateScore());

    }

    private CategoryRarityModelScorer createCategoryRarityModelScorer(CategoryRarityModelScorerParams params) {
        return new CategoryRarityModelScorer(params.getName(),
                params.getModelName(),
                Collections.emptyList(),
                params.getContextFieldNames(),
                Collections.emptyList(),
                params.getFeatureName(),
                params.getMinNumOfPartitionsToInfluence(),
                params.getNumberOfPartitionsToInfluenceEnough(),
                params.getUseCertaintyToCalculateScore(),
                params.getMaxRareCount(),
                params.getMaxNumOfRarePartitions(),
                X_WITH_VALUE_HALF_FACTOR,
                MIN_PROBABILITY,
                eventModelsCacheService);
    }

    //==================================================================================================================
    // CONSTRUCTOR TESTS
    //==================================================================================================================

    @Test
    public void constructor_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_Null_name_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setName(null);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_Empty_name_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setName("");
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_Blank_name_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setName(" ");
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }


    @Test(expected = IllegalArgumentException.class)
    public void constructor_negative_maxRareCount_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setMaxRareCount(-1);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }


    @Test(expected = IllegalArgumentException.class)
    public void constructor_negative_maxNumOfRarePartitions_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setMaxNumOfRarePartitions(-1);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_zero_numberOfSamplesToInfluenceEnough_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setNumberOfSamplesToInfluenceEnough(0);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }


    @Test(expected = IllegalArgumentException.class)
    public void constructor_zero_minNumOfSamplesToInfluence_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setMinNumOfSamplesToInfluence(0);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }

    @Test
    public void constructor_true_useCertaintyToCalculateScore_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setUseCertaintyToCalculateScore(true);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }

    @Test
    public void constructor_false_useCertaintyToCalculateScore_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setUseCertaintyToCalculateScore(false);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_Null_modelName_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setModelName(null);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_empty_modelName_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setModelName("");
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_blank_modelName_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setModelName(" ");
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }

    //==================================================================================================================
    // CAN SCORE TESTS
    //==================================================================================================================
    @Test
    public void calculateScore_null_model_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        Assert.assertFalse(scorer.canScore(null, Collections.emptyList(), new Feature("source-machine", "host1")));
    }

    @Test
    public void calculateScore_on_null_main_model_and_additional_model_with_90_partitions_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        Assert.assertTrue(scorer.canScore(null, Collections.singletonList(new CategoryRarityModel()), new Feature("source-machine", "host1")));
        CategoryRarityModel additionalModel = new CategoryRarityModel();
        additionalModel.init(null, 0, 100, 0);
        double score = scorer.calculateScore(null, Collections.singletonList(additionalModel), new Feature("source-machine", "host1"));
        Assert.assertEquals(96.0, score, 0.0);
    }

    @Test
    public void calculateScore_null_feature_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        Feature feature = null;
        Assert.assertFalse(scorer.canScore(new CategoryRarityModel(), Collections.emptyList(), feature));
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateScore_feature_with_null_name_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        scorer.canScore(new CategoryRarityModel(), Collections.emptyList(), new Feature(null, "host1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateScore_empty__feature_name_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        scorer.canScore(new CategoryRarityModel(), Collections.emptyList(), new Feature("", "host1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateScore_blank__feature_name_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        scorer.canScore(new CategoryRarityModel(), Collections.emptyList(), new Feature("     ", "host1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateScore_null__feature_value_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        scorer.canScore(new CategoryRarityModel(), Collections.emptyList(), new Feature("source-machine", (String)null));
    }


    //==================================================================================================================
    // CALCULATE SCORE ILLEGAL VALUES TESTS
    //==================================================================================================================

    @Test
    public void calculateScore_empty__feature_value_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        double score = scorer.calculateScore(new CategoryRarityModel(), Collections.emptyList(), new Feature("source-machine", ""));
        Assert.assertEquals(0.0, score, 0.0);
    }

    @Test
    public void calculateScore_blank__feature_value_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        double score = scorer.calculateScore(new CategoryRarityModel(), Collections.emptyList(), new Feature("source-machine", "    "));
        Assert.assertEquals(0.0, score, 0.0);
    }

    //==================================================================================================================
    // CALCULATE SCORE SIMPLE TESTS (more advanced tests are at the CategoryRarityModelScorerAlgorithmTest)
    //==================================================================================================================

    @Test
    public void calculateScore_elementaryCheck_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setMaxRareCount(15).setMaxNumOfRarePartitions(5);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);

        long count = 100;
        Map<String, Long> featureValueToCountMap = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            featureValueToCountMap.put(String.format("test%d", i), count);
        }

        CategoricalFeatureValue categoricalFeatureValue = new CategoricalFeatureValue(FixedDurationStrategy.HOURLY);

        calcCategoricalFeatureValue(featureValueToCountMap, categoricalFeatureValue);
        CategoryRarityModel model = (CategoryRarityModel)new CategoryRarityModelBuilder(new CategoryRarityModelBuilderConf(100), categoryRarityMetricsContainer).build(categoricalFeatureValue);
        String featureWithCount100 = "feature-count-100";
        model.setFeatureCount(featureWithCount100, count);
        double score = scorer.calculateScore(model, Collections.emptyList(), new Feature("feature-with-count-100", featureWithCount100));
        Assert.assertEquals(0.0, score, 0.0);
        score = scorer.calculateScore(model, Collections.emptyList(), new Feature("feature-with-zero-count", "feature-zero-count")); // The scorer should handle it as if count=1
        Assert.assertEquals(96, score, 0.0);

    }

    @Test
    public void calculateScore_elementaryCheck2_test(){
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setMaxRareCount(15).setMaxNumOfRarePartitions(5);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);

        long count = 100;
        Map<String, Long> featureValueToCountMap = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            featureValueToCountMap.put(String.format("test%d", i), count);
        }
        CategoricalFeatureValue categoricalFeatureValue = new CategoricalFeatureValue(FixedDurationStrategy.HOURLY);

        calcCategoricalFeatureValue(featureValueToCountMap, categoricalFeatureValue);

        CategoryRarityModel model = (CategoryRarityModel)new CategoryRarityModelBuilder(new CategoryRarityModelBuilderConf(100), categoryRarityMetricsContainer).build(categoricalFeatureValue);
        String featureWithZeroCount = "feature-zero-count"; // The scorer should handle it as if count=1

        AdeRecordReader adeRecordReader = new TestAdeRecord().setUsername("someone").setSourceMachine("test99").getAdeRecordReader();
        when(modelsCacheService.getLatestModelBeforeEventTime(any(), any(Map.class), any(Instant.class))).thenReturn(model);
        FeatureScore featureScore = scorer.calculateScore(adeRecordReader);
        Assert.assertEquals(0.0, featureScore.getScore(), 0.0);
        Assert.assertEquals(params.getName(), featureScore.getName());

        adeRecordReader = new TestAdeRecord().setUsername("someone").setSourceMachine(featureWithZeroCount).getAdeRecordReader();
        when(modelsCacheService.getLatestModelBeforeEventTime(any(), any(Map.class), any(Instant.class))).thenReturn(model);
        featureScore = scorer.calculateScore(adeRecordReader);
        Assert.assertEquals(96, featureScore.getScore(), 0.0);
        Assert.assertEquals(params.getName(), featureScore.getName());
    }

    private void calcCategoricalFeatureValue(Map<String, Long> featureValueToCountMap, CategoricalFeatureValue categoricalFeatureValue) {
        for (Map.Entry<String, Long> entry : featureValueToCountMap.entrySet()) {
            Instant startTime = Instant.parse("2007-12-03T10:00:00.00Z");
            Long numOfOccurences = entry.getValue();
            while (numOfOccurences >0)
            {
                GenericHistogram histogram = new GenericHistogram();
                histogram.add(entry.getKey(),entry.getValue().doubleValue());
                categoricalFeatureValue.add(histogram,startTime);
                startTime = startTime.plus(1, ChronoUnit.DAYS);
                numOfOccurences--;
            }

        }
    }

    @Test
    public void calculateScore_testing_featureScore_name_with_model_and_no_certainty_test() throws Exception{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setMaxRareCount(15).setMaxNumOfRarePartitions(5)
                .setUseCertaintyToCalculateScore(false);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);

        long count = 100;
        Map<String, Long> featureValueToCountMap = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            featureValueToCountMap.put(String.format("test%d", i), count);
        }
        CategoricalFeatureValue categoricalFeatureValue = new CategoricalFeatureValue(FixedDurationStrategy.HOURLY);

        calcCategoricalFeatureValue(featureValueToCountMap,categoricalFeatureValue);
        CategoryRarityModel model = (CategoryRarityModel)new CategoryRarityModelBuilder(new CategoryRarityModelBuilderConf(100), categoryRarityMetricsContainer).build(categoricalFeatureValue);
        String featureWithCount100 = "feature-count-100";
        String featureWithZeroCount = "feature-zero-count"; // The scorer should handle it as if count=1
        model.setFeatureCount(featureWithCount100, count);

        AdeRecordReader adeRecordReader = new TestAdeRecord().setUsername("someone").setSourceMachine(featureWithCount100).getAdeRecordReader();
        when(modelsCacheService.getLatestModelBeforeEventTime(any(), any(Map.class), any(Instant.class))).thenReturn(model);
        FeatureScore featureScore = scorer.calculateScore(adeRecordReader);
        Assert.assertEquals(0.0, featureScore.getScore(), 0.0);
        Assert.assertEquals(params.getName(), featureScore.getName());

        adeRecordReader = new TestAdeRecord().setUsername("someone").setSourceMachine(featureWithZeroCount).getAdeRecordReader();
        when(modelsCacheService.getLatestModelBeforeEventTime(any(), any(Map.class), any(Instant.class))).thenReturn(model);
        featureScore = scorer.calculateScore(adeRecordReader);
        Assert.assertEquals(96, featureScore.getScore(), 0.0);
        Assert.assertEquals(params.getName(), featureScore.getName());
    }

    @Test
    public void calculateScore_testing_featureScore_name_with_null_model_test() throws Exception{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setMaxRareCount(15).setMaxNumOfRarePartitions(5);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);

        AdeRecordReader adeRecordReader = new TestAdeRecord().setUsername("someone").setSourceMachine("feature-count-100").getAdeRecordReader();
        when(modelsCacheService.getLatestModelBeforeEventTime(any(), any(Map.class), any(Instant.class))).thenReturn(null);
        FeatureScore featureScore = scorer.calculateScore(adeRecordReader);
        Assert.assertEquals(params.getName(), featureScore.getName());
    }

    @Test
    public void calculateScore_no_model_test() throws Exception{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setMaxRareCount(15).setMaxNumOfRarePartitions(5);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);

        AdeRecordReader adeRecordReader = new TestAdeRecord().setUsername("someone").setSourceMachine("feature-zero-count").getAdeRecordReader();
        when(modelsCacheService.getLatestModelBeforeEventTime(any(), any(Map.class), any(Instant.class))).thenReturn(null);
        FeatureScore featureScore = scorer.calculateScore(adeRecordReader);
        Assert.assertEquals(0.0, featureScore.getScore(), 0.0);
    }

    @Test
    public void calculateScore_no_feature_in_record_test() throws Exception{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setMaxRareCount(15).setMaxNumOfRarePartitions(5);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);

        long count = 100;
        Map<String, Long> featureValueToCountMap = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            featureValueToCountMap.put(String.format("test%d", i), count);
        }
        CategoricalFeatureValue categoricalFeatureValue = new CategoricalFeatureValue(FixedDurationStrategy.HOURLY);

        calcCategoricalFeatureValue(featureValueToCountMap,categoricalFeatureValue);
        CategoryRarityModel model = (CategoryRarityModel)new CategoryRarityModelBuilder(new CategoryRarityModelBuilderConf(100), categoryRarityMetricsContainer).build(categoricalFeatureValue);
        String featureWithCount100 = "feature-count-100";
        model.setFeatureCount(featureWithCount100, count);

        AdeRecordReader adeRecordReader = new TestAdeRecord().setUsername("someone").setSourceMachine(null).getAdeRecordReader();
        when(modelsCacheService.getLatestModelBeforeEventTime(any(), any(Map.class), any(Instant.class))).thenReturn(model);
        FeatureScore featureScore = scorer.calculateScore(adeRecordReader);
        Assert.assertEquals(0.0, featureScore.getScore(), 0.0);
    }

    @Test
    public void calculateScore_null_context_test() throws Exception{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setMaxRareCount(15).setMaxNumOfRarePartitions(5);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);

        long count = 100;
        String featureWithCount100 = "feature-count-100";
        String featureWithZeroCount = "feature-zero-count"; // The scorer should handle it as if count=1
        CategoryRarityModel model = createModel(100, count, featureWithCount100);

        AdeRecordReader adeRecordReader = new TestAdeRecord().setUsername(null).setSourceMachine(featureWithZeroCount).getAdeRecordReader();
        when(modelsCacheService.getLatestModelBeforeEventTime(any(), any(Map.class), any(Instant.class))).thenReturn(model);
        FeatureScore featureScore = scorer.calculateScore(adeRecordReader);
        Assert.assertEquals(0.0, featureScore.getScore(), 0.0);
    }

    private CategoryRarityModel createModel(int numOfDistinctValues, long count, String feature) {
        Map<String, Long> featureValueToCountMap = new HashMap<>();
        for (int i = 0; i < numOfDistinctValues; i++) {
            featureValueToCountMap.put(String.format("test%d", i), count);
        }

        CategoricalFeatureValue categoricalFeatureValue = new CategoricalFeatureValue(FixedDurationStrategy.HOURLY);

        calcCategoricalFeatureValue(featureValueToCountMap, categoricalFeatureValue);
        GenericHistogram histogram = new GenericHistogram();
        featureValueToCountMap.entrySet().forEach(entry -> histogram.add(entry.getKey(), entry.getValue().doubleValue()));
        CategoryRarityModelBuilderConf config = new CategoryRarityModelBuilderConf(100);
        config.setPartitionsResolutionInSeconds(86400);
        CategoryRarityModel model = (CategoryRarityModel)new CategoryRarityModelBuilder(config, categoryRarityMetricsContainer).build(categoricalFeatureValue);
        model.setFeatureCount(feature, count);
        model.setNumOfPartitions(count);
        return model;
    }






    /**
     * CategoryRarityModelScorer params to ease the testing.
     * The default parameters here are intentionally different from the defaults in the conf itself.
     * Use the setters to override the specific parameter you want to test.
     */
    static class CategoryRarityModelScorerParams {
        String name = "Scorer1";
        String featureName = "sourceMachine";
        Integer maxRareCount = 10;
        Integer maxNumOfRarePartitions = 6;
        Integer numberOfSamplesToInfluenceEnough = 10;
        Integer minNumOfSamplesToInfluence = 2;
        Boolean useCertaintyToCalculateScore = true;
        String modelName = "model1";
        List<String> contextFieldNames = new ArrayList<>();

        public CategoryRarityModelScorerParams() {
            contextFieldNames.add("username");
        }

        public String getFeatureName() {
            return featureName;
        }

        public List<String> getContextFieldNames() {
            return contextFieldNames;
        }

        public String getName() {
            return name;
        }

        public CategoryRarityModelScorerParams setName(String name) {
            this.name = name;
            return this;
        }

        public Integer getMaxRareCount() {
            return maxRareCount;
        }

        public CategoryRarityModelScorerParams setMaxRareCount(Integer maxRareCount) {
            this.maxRareCount = maxRareCount;
            return this;
        }

        public Integer getMaxNumOfRarePartitions() {
            return maxNumOfRarePartitions;
        }

        public CategoryRarityModelScorerParams setMaxNumOfRarePartitions(Integer maxNumOfRarePartitions) {
            this.maxNumOfRarePartitions = maxNumOfRarePartitions;
            return this;
        }

        public Integer getNumberOfPartitionsToInfluenceEnough() {
            return numberOfSamplesToInfluenceEnough;
        }

        public CategoryRarityModelScorerParams setNumberOfSamplesToInfluenceEnough(Integer numberOfSamplesToInfluenceEnough) {
            this.numberOfSamplesToInfluenceEnough = numberOfSamplesToInfluenceEnough;
            return this;
        }

        public Integer getMinNumOfPartitionsToInfluence() {
            return minNumOfSamplesToInfluence;
        }

        public CategoryRarityModelScorerParams setMinNumOfSamplesToInfluence(Integer minNumOfSamplesToInfluence) {
            this.minNumOfSamplesToInfluence = minNumOfSamplesToInfluence;
            return this;
        }

        public Boolean getUseCertaintyToCalculateScore() {
            return useCertaintyToCalculateScore;
        }

        public CategoryRarityModelScorerParams setUseCertaintyToCalculateScore(Boolean useCertaintyToCalculateScore) {
            this.useCertaintyToCalculateScore = useCertaintyToCalculateScore;
            return this;
        }

        public String getModelName() {
            return modelName;
        }

        public CategoryRarityModelScorerParams setModelName(String modelName) {
            this.modelName = modelName;
            return this;
        }
    }
}
