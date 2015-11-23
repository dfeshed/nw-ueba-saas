package fortscale.ml.model;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsReaderService;
import fortscale.aggregation.feature.util.GenericHistogram;
import fortscale.aggregation.util.MongoDbUtilService;
import fortscale.ml.model.listener.IModelBuildingListener;
import fortscale.ml.model.prevalance.field.ContinuousDataModel;
import fortscale.ml.model.store.ModelDAO;
import fortscale.utils.time.TimestampUtils;
import junitparams.JUnitParamsRunner;
import net.minidev.json.JSONObject;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(JUnitParamsRunner.class)
public class ModelServiceTest {
	private static ClassPathXmlApplicationContext testContextManager;

	private FeatureBucketsReaderService featureBucketsReaderService;
	private MongoTemplate mongoTemplate;

	private FeatureBucketConf selectorFeatureBucketConf;
	private FeatureBucketConf retrieverFeatureBucketConf;

	private ListModelBuildingListener listener;
	private ModelService modelService;

	@BeforeClass
	public static void setUpClass() {
		testContextManager = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/model-service-test-context.xml");
	}

	@Before
	public void setUp() {
		featureBucketsReaderService = testContextManager.getBean(FeatureBucketsReaderService.class);
		mongoTemplate = testContextManager.getBean(MongoTemplate.class);

		BucketConfigurationService bucketConfigurationService = testContextManager.getBean(BucketConfigurationService.class);
		selectorFeatureBucketConf = mock(FeatureBucketConf.class);
		retrieverFeatureBucketConf = mock(FeatureBucketConf.class);
		when(bucketConfigurationService.getBucketConf("selector_feature_bucket_conf")).thenReturn(selectorFeatureBucketConf);
		when(bucketConfigurationService.getBucketConf("retriever_feature_bucket_conf")).thenReturn(retrieverFeatureBucketConf);

		MongoDbUtilService mongoDbUtilService = testContextManager.getBean(MongoDbUtilService.class);
		when(mongoDbUtilService.collectionExists(any(String.class))).thenReturn(true);

		listener = new ListModelBuildingListener();
		modelService = new ModelService(listener);
	}

	@Test
	public void should_process_correctly_a_build_continuous_data_model_event() throws Exception {
		long previousEndTimeInSeconds = 1420070400;
		long previousEndTimeInMillis = TimestampUtils.convertToMilliSeconds(previousEndTimeInSeconds);
		long currentEndTimeInSeconds = 1420070410;
		long currentEndTimeInMillis = TimestampUtils.convertToMilliSeconds(currentEndTimeInSeconds);

		List<String> contextIds = Arrays.asList("id1", "id2");
		when(featureBucketsReaderService.findDistinctContextByTimeRange(
				selectorFeatureBucketConf, previousEndTimeInMillis, currentEndTimeInMillis)).thenReturn(contextIds);

		// ID #1, first bucket
		GenericHistogram genericHistogram_1_1 = new GenericHistogram();
		genericHistogram_1_1.add(100, 1d);
		genericHistogram_1_1.add(200, 2d);
		genericHistogram_1_1.add(300, 3d);

		// ID #1, second bucket
		GenericHistogram genericHistogram_1_2 = new GenericHistogram();
		genericHistogram_1_2.add(200, 20d);
		genericHistogram_1_2.add(300, 30d);
		genericHistogram_1_2.add(400, 40d);

		// ID #2, first bucket
		GenericHistogram genericHistogram_2_1 = new GenericHistogram();
		genericHistogram_2_1.add(3.14, 1d);
		genericHistogram_2_1.add(1.9, 42d);

		// ID #2, second bucket
		GenericHistogram genericHistogram_2_2 = new GenericHistogram();
		genericHistogram_2_2.add(11, 13d);
		genericHistogram_2_2.add(17, 19d);

		// Consistent with the name in the configuration
		String featureName = "retriever_feature";

		List<FeatureBucket> featureBuckets_1 = new ArrayList<>();
		featureBuckets_1.add(createFeatureBucketWithGenericHistogram(featureName, genericHistogram_1_1));
		featureBuckets_1.add(createFeatureBucketWithGenericHistogram(featureName, genericHistogram_1_2));
		when(featureBucketsReaderService.getFeatureBucketsByContextIdAndTimeRange(
				eq(retrieverFeatureBucketConf), eq("id1"), eq(previousEndTimeInSeconds), eq(currentEndTimeInSeconds))).thenReturn(featureBuckets_1);

		List<FeatureBucket> featureBuckets_2 = new ArrayList<>();
		featureBuckets_2.add(createFeatureBucketWithGenericHistogram(featureName, genericHistogram_2_1));
		featureBuckets_2.add(createFeatureBucketWithGenericHistogram(featureName, genericHistogram_2_2));
		when(featureBucketsReaderService.getFeatureBucketsByContextIdAndTimeRange(
				eq(retrieverFeatureBucketConf), eq("id2"), eq(previousEndTimeInSeconds), eq(currentEndTimeInSeconds))).thenReturn(featureBuckets_2);

		// Consistent with the name in the configuration
		String modelConfName = "first_test_model_conf";

		JSONObject event = new JSONObject();
		event.put("sessionId", "test_session_id");
		event.put("modelConfName", modelConfName);
		event.put("endTimeInSeconds", currentEndTimeInSeconds);
		modelService.process(event);

		// Assert listener
		DateTime currentEndTime = new DateTime(currentEndTimeInMillis);
		JSONObject expectedStatusForId1 = buildStatus(modelConfName, "id1", currentEndTime, true);
		JSONObject expectedStatusForId2 = buildStatus(modelConfName, "id2", currentEndTime, true);
		List<JSONObject> expectedStatuses = Arrays.asList(expectedStatusForId1, expectedStatusForId2);
		Assert.assertEquals(expectedStatuses, listener.getStatuses());

		// Assert built models
		ArgumentCaptor<ModelDAO> modelDaoArgumentCaptor = ArgumentCaptor.forClass(ModelDAO.class);
		ArgumentCaptor<String> collectionNameArgumentCaptor = ArgumentCaptor.forClass(String.class);
		verify(mongoTemplate, times(2)).save(modelDaoArgumentCaptor.capture(), collectionNameArgumentCaptor.capture());

		ModelDAO actualId1ModelDao = modelDaoArgumentCaptor.getAllValues().get(0);
		Assert.assertEquals("id1", actualId1ModelDao.getContextId());
		ContinuousDataModel expectedId1Model = new ContinuousDataModel();
		expectedId1Model.setParameters(96, 316.6666666666667, 81.22328620674138);
		Assert.assertEquals(expectedId1Model, actualId1ModelDao.getModel());

		ModelDAO actualId2ModelDao = modelDaoArgumentCaptor.getAllValues().get(1);
		Assert.assertEquals("id2", actualId2ModelDao.getContextId());
		ContinuousDataModel expectedId2Model = new ContinuousDataModel();
		expectedId2Model.setParameters(75, 7.3192, 6.539804229485773);
		Assert.assertEquals(expectedId2Model, actualId2ModelDao.getModel());

		for (String collectionName : collectionNameArgumentCaptor.getAllValues()) {
			Assert.assertEquals(String.format("model_%s", modelConfName), collectionName);
		}
	}

	private static FeatureBucket createFeatureBucketWithGenericHistogram(String featureName, GenericHistogram genericHistogram) {
		FeatureBucket featureBucket = new FeatureBucket();
		Map<String, Feature> aggregatedFeatures = featureBucket.getAggregatedFeatures();
		aggregatedFeatures.put(featureName, new Feature(featureName, genericHistogram));
		return featureBucket;
	}

	private static JSONObject buildStatus(String modelConfName, String contextId, DateTime endTime, boolean success) {
		JSONObject statusJson = new JSONObject();
		statusJson.put("modelConfName", modelConfName);
		statusJson.put("contextId", contextId);
		statusJson.put("endTime", endTime.toString());
		statusJson.put("success", success);
		return statusJson;
	}

	private static final class ListModelBuildingListener implements IModelBuildingListener {
		private List<JSONObject> statuses = new ArrayList<>();

		@Override
		public void modelBuildingStatus(String modelConfName, String contextId, DateTime endTime, boolean success) {
			statuses.add(buildStatus(modelConfName, contextId, endTime, success));
		}

		public List<JSONObject> getStatuses() {
			return statuses;
		}
	}
}
