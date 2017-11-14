package presidio.ade.modeling;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketStore;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.builder.gaussian.ContinuousHistogramModelBuilderConf;
import fortscale.ml.model.retriever.ContextHistogramRetrieverConf;
import fortscale.ml.model.selector.FeatureBucketContextSelectorConf;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.shell.BootShim;
import fortscale.utils.test.category.ModuleTestCategory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.shell.core.CommandResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.modeling.config.ModelingServiceConfigurationTest;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static java.util.Collections.*;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

/**
 * @author Lior Govrin
 */
@RunWith(SpringRunner.class)
@Category(ModuleTestCategory.class)
@ContextConfiguration(classes = ModelingServiceConfigurationTest.class)
public class ModelingServiceApplicationTest {
	private static final String ENRICHED_RECORDS_LINE = "process --group_name enriched-record-models --session_id test-run --end_date 2017-01-01T00:00:00Z";


	@Autowired
	private BootShim bootShim;
	@Autowired
	private BucketConfigurationService featureBucketConfService;
	@Autowired
	private FeatureBucketStore featureBucketStore;
	@Autowired
	private ModelStore modelStore;
	@Autowired
	private MongoTemplate mongoTemplate;

	@Test
	public void continuous_data_model_test() {
		// Arrange
		arrangeFeatureBuckets();

		// Act
		CommandResult commandResult = bootShim.getShell().executeCommand(ENRICHED_RECORDS_LINE);

		// Assert command execution
		Assert.assertTrue(commandResult.isSuccess());

		// Assert number of models
		Collection<ModelDAO> modelDaoList = modelStore.getAllContextsModelDaosWithLatestEndTimeLte(getModelConf(), Instant.ofEpochSecond(1483228800));
		Assert.assertEquals(2, modelDaoList.size());

		// Assert models
		assertModels(modelDaoList);

		// Clean feature buckets and models
		mongoTemplate.dropCollection("aggr_continuousDataFeatureBucket");
		mongoTemplate.dropCollection("model_continuousData.userId.test");
	}

	private void arrangeFeatureBuckets() {
		// Get test feature bucket conf
		FeatureBucketConf featureBucketConf = featureBucketConfService.getBucketConf("continuousDataFeatureBucket");

		// Context ID #1, feature bucket #1
		GenericHistogram genericHistogram = new GenericHistogram();
		genericHistogram.add(100, 1d);
		genericHistogram.add(200, 2d);
		genericHistogram.add(300, 3d);
		Feature feature = new Feature("continuousDataFeature", genericHistogram);
		FeatureBucket featureBucket = new FeatureBucket();
		featureBucket.setStartTime(Instant.parse("2016-12-30T00:00:00Z"));
		featureBucket.setEndTime(Instant.parse("2016-12-31T00:00:00Z"));
		featureBucket.setFeatureBucketConfName("continuousDataFeatureBucket");
		featureBucket.setContextFieldNames(singletonList("userId"));
		featureBucket.setStrategyId("fixed_duration_daily_1483228800");
		featureBucket.setContextFieldNameToValueMap(singletonMap("userId", "test_user_1"));
		featureBucket.setContextId("userId###test_user_1");
		featureBucket.setBucketId("fixed_duration_daily_1483228800###userId###test_user_1###continuousDataFeatureBucket");
		featureBucket.setCreatedAt(new Date());
		featureBucket.setAggregatedFeatures(singletonMap("continuousDataFeature", feature));
		featureBucketStore.storeFeatureBucket(featureBucketConf, featureBucket);

		// Context ID #1, feature bucket #2
		genericHistogram = new GenericHistogram();
		genericHistogram.add(200, 20d);
		genericHistogram.add(300, 30d);
		genericHistogram.add(400, 40d);
		feature = new Feature("continuousDataFeature", genericHistogram);
		featureBucket = new FeatureBucket();
		featureBucket.setStartTime(Instant.parse("2016-12-31T00:00:00Z"));
		featureBucket.setEndTime(Instant.parse("2017-01-01T00:00:00Z"));
		featureBucket.setFeatureBucketConfName("continuousDataFeatureBucket");
		featureBucket.setContextFieldNames(singletonList("userId"));
		featureBucket.setStrategyId("fixed_duration_daily_1483315200");
		featureBucket.setContextFieldNameToValueMap(singletonMap("userId", "test_user_1"));
		featureBucket.setContextId("userId###test_user_1");
		featureBucket.setBucketId("fixed_duration_daily_1483315200###userId###test_user_1###continuousDataFeatureBucket");
		featureBucket.setCreatedAt(new Date());
		featureBucket.setAggregatedFeatures(singletonMap("continuousDataFeature", feature));
		featureBucketStore.storeFeatureBucket(featureBucketConf, featureBucket);

		// Context ID #2, feature bucket #1
		genericHistogram = new GenericHistogram();
		genericHistogram.add(3.14, 1d);
		genericHistogram.add(1.9, 42d);
		feature = new Feature("continuousDataFeature", genericHistogram);
		featureBucket = new FeatureBucket();
		featureBucket.setStartTime(Instant.parse("2016-12-30T00:00:00Z"));
		featureBucket.setEndTime(Instant.parse("2016-12-31T00:00:00Z"));
		featureBucket.setFeatureBucketConfName("continuousDataFeatureBucket");
		featureBucket.setContextFieldNames(singletonList("userId"));
		featureBucket.setStrategyId("fixed_duration_daily_1483228800");
		featureBucket.setContextFieldNameToValueMap(singletonMap("userId", "test_user_2"));
		featureBucket.setContextId("userId###test_user_2");
		featureBucket.setBucketId("fixed_duration_daily_1483228800###userId###test_user_2###continuousDataFeatureBucket");
		featureBucket.setCreatedAt(new Date());
		featureBucket.setAggregatedFeatures(singletonMap("continuousDataFeature", feature));
		featureBucketStore.storeFeatureBucket(featureBucketConf, featureBucket);

		// Context ID #2, feature bucket #2
		genericHistogram = new GenericHistogram();
		genericHistogram.add(11, 13d);
		genericHistogram.add(17, 19d);
		feature = new Feature("continuousDataFeature", genericHistogram);
		featureBucket = new FeatureBucket();
		featureBucket.setStartTime(Instant.parse("2016-12-31T00:00:00Z"));
		featureBucket.setEndTime(Instant.parse("2017-01-01T00:00:00Z"));
		featureBucket.setFeatureBucketConfName("continuousDataFeatureBucket");
		featureBucket.setContextFieldNames(singletonList("userId"));
		featureBucket.setStrategyId("fixed_duration_daily_1483315200");
		featureBucket.setContextFieldNameToValueMap(singletonMap("userId", "test_user_2"));
		featureBucket.setContextId("userId###test_user_2");
		featureBucket.setBucketId("fixed_duration_daily_1483315200###userId###test_user_2###continuousDataFeatureBucket");
		featureBucket.setCreatedAt(new Date());
		featureBucket.setAggregatedFeatures(singletonMap("continuousDataFeature", feature));
		featureBucketStore.storeFeatureBucket(featureBucketConf, featureBucket);
	}

	private ModelConf getModelConf() {
		return new ModelConf(
				"continuousData.userId.test",
				new FeatureBucketContextSelectorConf("continuousDataFeatureBucket"),
				new ContextHistogramRetrieverConf(7776000, emptyList(), "continuousDataFeatureBucket", "continuousDataFeature", 86400),
				new ContinuousHistogramModelBuilderConf());
	}

	private void assertModels(Collection<ModelDAO> modelDAOs) {
		List<ModelDAO> modelDaoList = modelDAOs.stream().sorted(comparing(ModelDAO::getContextId)).collect(toList());

		// Assert context ID #1 model
		ModelDAO modelDao = modelDaoList.iterator().next();
		Assert.assertEquals("test-run", modelDao.getSessionId());
		Assert.assertEquals("userId###test_user_1", modelDao.getContextId());
		ContinuousDataModel continuousDataModel = (ContinuousDataModel)modelDao.getModel();
		Assert.assertEquals(96, continuousDataModel.getN());
		Assert.assertEquals(316.666667, continuousDataModel.getMean(), 0.000001);
		Assert.assertEquals(81.223286, continuousDataModel.getSd(), 0.000001);
		Assert.assertEquals(400, continuousDataModel.getMaxValue(), 0);
		Assert.assertEquals(Instant.parse("2016-10-03T00:00:00Z"), modelDao.getStartTime());
		Assert.assertEquals(Instant.parse("2017-01-01T00:00:00Z"), modelDao.getEndTime());

		// Assert context ID #2 model
		modelDao = modelDaoList.get(1);
		Assert.assertEquals("test-run", modelDao.getSessionId());
		Assert.assertEquals("userId###test_user_2", modelDao.getContextId());
		continuousDataModel = (ContinuousDataModel)modelDao.getModel();
		Assert.assertEquals(75, continuousDataModel.getN());
		Assert.assertEquals(7.319200, continuousDataModel.getMean(), 0.000001);
		Assert.assertEquals(6.539804, continuousDataModel.getSd(), 0.000001);
		Assert.assertEquals(17, continuousDataModel.getMaxValue(), 0);
		Assert.assertEquals(Instant.parse("2016-10-03T00:00:00Z"), modelDao.getStartTime());
		Assert.assertEquals(Instant.parse("2017-01-01T00:00:00Z"), modelDao.getEndTime());
	}
}
