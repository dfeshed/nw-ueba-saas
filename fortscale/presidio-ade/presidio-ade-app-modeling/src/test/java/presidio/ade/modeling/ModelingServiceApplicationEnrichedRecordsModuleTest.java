package presidio.ade.modeling;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketStore;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.DynamicModelConfServiceContainer;
import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.builder.CategoryRarityModelBuilderConf;
import fortscale.ml.model.builder.IModelBuilderConf;
import fortscale.ml.model.builder.TimeModelBuilderConf;
import fortscale.ml.model.builder.gaussian.ContinuousMaxHistogramModelBuilderConf;
import fortscale.ml.model.builder.gaussian.prior.GaussianPriorModelBuilderConf;
import fortscale.ml.model.retriever.*;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.test.category.ModuleTestCategory;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.Assert;
import presidio.ade.modeling.config.ModelingServiceApplicationModuleTestConfig;
import presidio.ade.test.utils.generators.feature_buckets.FeatureBucketEpochtimeMapGenerator;
import presidio.ade.test.utils.generators.feature_buckets.FeatureBucketEpochtimeToHighestIntegerMapGenerator;
import presidio.ade.test.utils.generators.feature_buckets.FeatureBucketGenerator;
import presidio.ade.test.utils.tests.BaseAppTest;
import presidio.data.generators.common.*;
import presidio.data.generators.common.time.FixedRangeTimeGenerator;
import presidio.data.generators.common.time.ITimeGenerator;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Lior Govrin
 */
@Category(ModuleTestCategory.class)
@ContextConfiguration
public class ModelingServiceApplicationEnrichedRecordsModuleTest extends BaseAppTest {
    private static final Instant endInstant = Instant.parse("2017-09-01T00:00:00Z");
    private static final String COMMAND = "process --group_name enriched-record-models --session_id test-run --end_date " + endInstant.toString();
    private static final Duration redundantDataTailLength = Duration.ofDays(3);
    private static final int NUM_OF_FEATURES_IN_INTERVAL = 10;
    private static final long COUNTS_PER_FEATURE_IN_INTERVAL = 10;
    private static final String UNSUPPORTED_MODEL_CONF_MESSAGE_FORMAT = "This module test does not support the model conf %s.";

    @Value("${presidio.ade.modeling.expected.path}")
    private String expectedPath;

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private BucketConfigurationService bucketConfigurationService;
    @Autowired
    private FeatureBucketStore featureBucketStore;
    @Autowired
    private ModelStore modelStore;

    private Model expectedModel1;
    private Model expectedModel2;
    private Model expectedModel3;
    private Model expectedModel4;
    private List<ModelConf> modelConfs;

    @Override
    protected String getContextTestExecutionCommand() {
        return COMMAND;
    }

    @Test
    public void testEnrichedRecordsModelingServiceApplication() throws IOException, GeneratorException {
        readExpected();
        arrangeData();
        executeAndAssertCommandSuccess(COMMAND);
        assertModels();
    }

    private void readExpected() throws IOException {
        // Read the expected models from the JSON files
        Stream<Resource> resources = Arrays.stream(applicationContext.getResources(expectedPath + "models/*.json"));
        Map<String, Resource> filenameToResourceMap = resources.collect(Collectors.toMap(
                Resource::getFilename,
                Function.identity()
        ));
        ObjectMapper om = new ObjectMapper();
        expectedModel1 = om.readValue(filenameToResourceMap.get("expected_model_1.json").getURL(), Model.class);
        expectedModel2 = om.readValue(filenameToResourceMap.get("expected_model_2.json").getURL(), Model.class);
        expectedModel3 = om.readValue(filenameToResourceMap.get("expected_model_3.json").getURL(), Model.class);
        expectedModel4 = om.readValue(filenameToResourceMap.get("expected_model_4.json").getURL(), Model.class);
    }

    private void arrangeData() throws GeneratorException {
        // In order to create a model conf service with the relevant model confs, a process
        // command must be executed first (with an arbitrary end date and without any data)
        executeAndAssertCommandSuccess(COMMAND);
        modelConfs = DynamicModelConfServiceContainer.getModelConfService().getModelConfs();
        Assert.notEmpty(modelConfs, "No enriched record model confs were loaded.");

        // Generate data that will be retrieved and passed to the builders
        for (ModelConf modelConf : modelConfs) {
            AbstractDataRetrieverConf retrieverConf = modelConf.getDataRetrieverConf();
            IModelBuilderConf builderConf = modelConf.getModelBuilderConf();

            if (retrieverConf instanceof ContextHistogramRetrieverConf && builderConf instanceof TimeModelBuilderConf) {
                generateDataForContextHistogramRetrieverAndTimeModelBuilder((ContextHistogramRetrieverConf)retrieverConf);
            } else if (retrieverConf instanceof CategoricalFeatureValueRetrieverConf && builderConf instanceof CategoryRarityModelBuilderConf) {
                generateDataForCategoricalFeatureValueRetrieverAndCategoryRarityModelBuilder((CategoricalFeatureValueRetrieverConf)retrieverConf);
            } else if (retrieverConf instanceof EpochtimeToHighestIntegerMapRetrieverConf && builderConf instanceof ContinuousMaxHistogramModelBuilderConf) {
                generateDataForEpochtimeToHighestIntegerMapRetrieverAndContinuousMaxHistogramModelBuilder((EpochtimeToHighestIntegerMapRetrieverConf)retrieverConf);
            } else if (retrieverConf instanceof ModelRetrieverConf && builderConf instanceof GaussianPriorModelBuilderConf) {
                // No need to generate data, since the model retriever takes the contextual models built by the ContinuousMaxHistogramModelBuilder
            } else {
                String s = String.format(UNSUPPORTED_MODEL_CONF_MESSAGE_FORMAT, modelConf.getName());
                throw new IllegalArgumentException(s);
            }
        }
    }

    private void assertModels() {
        for (ModelConf modelConf : modelConfs) {
            String modelConfName = modelConf.getName();
            String unexpectedNumMessage = String.format("Unexpected num of models for model conf %s.", modelConfName);
            String unexpectedModelMessage = String.format("Unexpected model for model conf %s.", modelConfName);
            List<Model> models = modelStore.getAllContextsModelDaosWithLatestEndTimeLte(modelConf, endInstant).stream()
                    .map(ModelDAO::getModel)
                    .collect(Collectors.toList());
            AbstractDataRetrieverConf retrieverConf = modelConf.getDataRetrieverConf();
            IModelBuilderConf builderConf = modelConf.getModelBuilderConf();

            if (retrieverConf instanceof ContextHistogramRetrieverConf && builderConf instanceof TimeModelBuilderConf) {
                Assert.isTrue(models.size() == 50, unexpectedNumMessage);
                models.forEach(model -> Assert.isTrue(model.equals(expectedModel1), unexpectedModelMessage));
            } else if (retrieverConf instanceof CategoricalFeatureValueRetrieverConf && builderConf instanceof CategoryRarityModelBuilderConf) {
                Assert.isTrue(models.size() == 50, unexpectedNumMessage);
                models.forEach(model -> Assert.isTrue(model.equals(expectedModel2), unexpectedModelMessage));
            } else if (retrieverConf instanceof EpochtimeToHighestIntegerMapRetrieverConf && builderConf instanceof ContinuousMaxHistogramModelBuilderConf) {
                Assert.isTrue(models.size() == 50, unexpectedNumMessage);
                models.forEach(model -> Assert.isTrue(model.equals(expectedModel3), unexpectedModelMessage));
            } else if (retrieverConf instanceof ModelRetrieverConf && builderConf instanceof GaussianPriorModelBuilderConf) {
                Assert.isTrue(models.size() == 1, unexpectedNumMessage);
                models.forEach(model -> Assert.isTrue(model.equals(expectedModel4), unexpectedModelMessage));
            } else {
                String s = String.format(UNSUPPORTED_MODEL_CONF_MESSAGE_FORMAT, modelConfName);
                throw new IllegalArgumentException(s);
            }
        }
    }

    private void generateFeatureBuckets(long timeRangeInSeconds, FeatureBucketConf featureBucketConf, IMapGenerator<String, Feature> aggregatedFeaturesGenerator) throws GeneratorException {
        // Create a start time generator
        Instant startInstant = endInstant.minusSeconds(timeRangeInSeconds).minus(redundantDataTailLength);
        Duration interval = FixedDurationStrategy.fromStrategyName(featureBucketConf.getStrategyName()).toDuration();
        ITimeGenerator startTimeGenerator = new FixedRangeTimeGenerator(startInstant, endInstant, interval);

        // Create a context field values generator
        List<StringRegexCyclicValuesGenerator> stringRegexCyclicValuesGenerators = featureBucketConf.getContextFieldNames().stream()
                .map(contextFieldName -> {
                    /*
                     * Note:
                     * =====
                     * This returns a generator that creates 50 values for the context
                     * field. This affects the number of models created per model conf.
                     */
                    return new StringRegexCyclicValuesGenerator(String.format("%s[0-4][0-9]", contextFieldName));
                })
                .collect(Collectors.toList());
        RegexStringListGenerator contextFieldValuesGenerator = new RegexStringListGenerator(stringRegexCyclicValuesGenerators);

        // Create a feature bucket generator
        FeatureBucketGenerator featureBucketGenerator = new FeatureBucketGenerator(featureBucketConf, startTimeGenerator, contextFieldValuesGenerator, aggregatedFeaturesGenerator);

        // Generate and store the feature buckets
        List<FeatureBucket> featureBuckets = featureBucketGenerator.generate();
        featureBucketStore.storeFeatureBucket(featureBucketConf, featureBuckets);
    }

    private void generateDataForContextHistogramRetrieverAndTimeModelBuilder(ContextHistogramRetrieverConf retrieverConf) throws GeneratorException {
        FeatureBucketConf featureBucketConf = bucketConfigurationService.getBucketConf(retrieverConf.getFeatureBucketConfName());
        Duration interval = FixedDurationStrategy.fromStrategyName(featureBucketConf.getStrategyName()).toDuration();
        Duration diffBetweenDeltas = interval.dividedBy(NUM_OF_FEATURES_IN_INTERVAL);
        Map<Duration, Long> deltaToCountMap = new HashMap<>();
        for (int i = 0; i < NUM_OF_FEATURES_IN_INTERVAL; i++)
            deltaToCountMap.put(diffBetweenDeltas.multipliedBy(i), COUNTS_PER_FEATURE_IN_INTERVAL);
        long timeRangeInSeconds = retrieverConf.getTimeRangeInSeconds();
        FeatureBucketEpochtimeMapGenerator featureBucketEpochtimeMapGenerator = new FeatureBucketEpochtimeMapGenerator(endInstant.minusSeconds(timeRangeInSeconds), interval, deltaToCountMap, retrieverConf.getFeatureName());
        generateFeatureBuckets(timeRangeInSeconds, featureBucketConf, featureBucketEpochtimeMapGenerator);
    }

    private void generateDataForCategoricalFeatureValueRetrieverAndCategoryRarityModelBuilder(CategoricalFeatureValueRetrieverConf retrieverConf) throws GeneratorException {
        FeatureBucketConf featureBucketConf = bucketConfigurationService.getBucketConf(retrieverConf.getFeatureBucketConfName());
        String featureName = retrieverConf.getFeatureName();
        GenericHistogram genericHistogram = new GenericHistogram();
        for (int i = 0; i < NUM_OF_FEATURES_IN_INTERVAL; i++)
            genericHistogram.add("value" + i, new Double(COUNTS_PER_FEATURE_IN_INTERVAL));
        Map<String, Feature> fixedMap = new HashMap<>();
        fixedMap.put(featureName, new Feature(featureName, genericHistogram));
        IMapGenerator<String, Feature> aggregatedFeaturesGenerator = new CyclicMapGenerator<>(Collections.singletonList(fixedMap));
        generateFeatureBuckets(retrieverConf.getTimeRangeInSeconds(), featureBucketConf, aggregatedFeaturesGenerator);
    }

    private void generateDataForEpochtimeToHighestIntegerMapRetrieverAndContinuousMaxHistogramModelBuilder(EpochtimeToHighestIntegerMapRetrieverConf retrieverConf) throws GeneratorException {
        FeatureBucketConf featureBucketConf = bucketConfigurationService.getBucketConf(retrieverConf.getFeatureBucketConfName());
        Duration interval = FixedDurationStrategy.fromStrategyName(featureBucketConf.getStrategyName()).toDuration();
        Duration diffBetweenDeltas = interval.dividedBy(NUM_OF_FEATURES_IN_INTERVAL);
        Map<Duration, Long> deltaToCountMap = new HashMap<>();
        for (int i = 0; i < NUM_OF_FEATURES_IN_INTERVAL; i++)
            deltaToCountMap.put(diffBetweenDeltas.multipliedBy(i), COUNTS_PER_FEATURE_IN_INTERVAL);
        long timeRangeInSeconds = retrieverConf.getTimeRangeInSeconds();
        FeatureBucketEpochtimeToHighestIntegerMapGenerator featureBucketEpochtimeToHighestIntegerMapGenerator = new FeatureBucketEpochtimeToHighestIntegerMapGenerator(endInstant.minusSeconds(timeRangeInSeconds), interval, deltaToCountMap, retrieverConf.getFeatureName());
        generateFeatureBuckets(timeRangeInSeconds, featureBucketConf, featureBucketEpochtimeToHighestIntegerMapGenerator);
    }

    @Configuration
    @Import({ModelingServiceApplicationModuleTestConfig.class, BaseAppTest.springConfig.class})
    public static class ModelingServiceApplicationEnrichedRecordsModuleTestConfiguration {
    }
}
