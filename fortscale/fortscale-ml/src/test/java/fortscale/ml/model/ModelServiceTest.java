package fortscale.ml.model;

import fortscale.aggregation.feature.bucket.*;
import fortscale.aggregation.util.MongoDbUtilService;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.listener.IModelBuildingListener;
import fortscale.ml.model.listener.ModelBuildingStatus;
import fortscale.ml.model.store.ModelDAO;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class ModelServiceTest {
	@Configuration
	@ImportResource("classpath*:META-INF/spring/model-service-test-context.xml")
	static class ContextConfiguration {
		@Bean
		public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
			Properties properties = new Properties();
			properties.put("fortscale.model.configurations.location.path", "classpath:model-service-test/*.json");
			properties.put("fortscale.model.configurations.overriding.location.path", "file:home/cloudera/fortscale/config/asl/models/overriding/*.json");
			properties.put("fortscale.model.configurations.additional.location.path", "file:home/cloudera/fortscale/config/asl/models/additional/*.json");
			properties.put("fortscale.model.build.retention.time.in.days", 180);
			properties.put("fortscale.model.build.selector.delta.in.seconds", 604800);
			PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
			configurer.setProperties(properties);
			configurer.setOrder(Ordered.HIGHEST_PRECEDENCE);
			configurer.setIgnoreUnresolvablePlaceholders(true);
			configurer.setLocalOverride(true);
			return configurer;
		}
	}

	@Autowired
	private BucketConfigurationService bucketConfigurationService;
	@Autowired
	private MongoDbUtilService mongoDbUtilService;
	@Autowired
	private ModelService modelService;
	@Autowired
	private FeatureBucketsReaderService featureBucketsReaderService;
	@Autowired
	private MongoTemplate mongoTemplate;

	private FeatureBucketConf selectorFeatureBucketConf;
	private FeatureBucketConf retrieverFeatureBucketConf;
	private ListModelBuildingListener listener;

	@Before
	public void setUp() {
		selectorFeatureBucketConf = mock(FeatureBucketConf.class);
		retrieverFeatureBucketConf = mock(FeatureBucketConf.class);
		AggregatedFeatureConf aggregatedFeatureConf = new AggregatedFeatureConf("retriever_feature", new HashMap<>(), new JSONObject());
		when(retrieverFeatureBucketConf.getAggrFeatureConfs()).thenReturn(Collections.singletonList(aggregatedFeatureConf));
		when(bucketConfigurationService.getBucketConf("selector_feature_bucket_conf")).thenReturn(selectorFeatureBucketConf);
		when(bucketConfigurationService.getBucketConf("retriever_feature_bucket_conf")).thenReturn(retrieverFeatureBucketConf);
		when(mongoDbUtilService.collectionExists(any(String.class))).thenReturn(true);
		listener = new ListModelBuildingListener();
		modelService.init();
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
				eq(retrieverFeatureBucketConf), eq("id1"), eq(previousEndTimeInSeconds), eq(currentEndTimeInSeconds), any(String.class))).thenReturn(featureBuckets_1);

		List<FeatureBucket> featureBuckets_2 = new ArrayList<>();
		featureBuckets_2.add(createFeatureBucketWithGenericHistogram(featureName, genericHistogram_2_1));
		featureBuckets_2.add(createFeatureBucketWithGenericHistogram(featureName, genericHistogram_2_2));
		when(featureBucketsReaderService.getFeatureBucketsByContextIdAndTimeRange(
				eq(retrieverFeatureBucketConf), eq("id2"), eq(previousEndTimeInSeconds), eq(currentEndTimeInSeconds), any(String.class))).thenReturn(featureBuckets_2);

		String sessionId = "test_session_id";
		// Consistent with the name in the configuration
		String modelConfName = "first_test_model_conf";
		Date currentEndTime = new Date(currentEndTimeInMillis);
		modelService.process(listener, sessionId, modelConfName, null, currentEndTime);

		// Assert listener
		JSONObject expectedStatusForId1 = buildStatus(modelConfName, "id1", currentEndTime, true);
		JSONObject expectedStatusForId2 = buildStatus(modelConfName, "id2", currentEndTime, true);
		List<JSONObject> expectedStatuses = Arrays.asList(expectedStatusForId1, expectedStatusForId2);
		Assert.assertEquals(expectedStatuses, listener.getStatuses());

		// Assert built models
		Query expectedId1Query = new Query();
		expectedId1Query.addCriteria(Criteria.where(ModelDAO.SESSION_ID_FIELD).is(sessionId));
		expectedId1Query.addCriteria(Criteria.where(ModelDAO.CONTEXT_ID_FIELD).is("id1"));
		expectedId1Query.fields().include("_id");

		Query expectedId2Query = new Query();
		expectedId2Query.addCriteria(Criteria.where(ModelDAO.SESSION_ID_FIELD).is(sessionId));
		expectedId2Query.addCriteria(Criteria.where(ModelDAO.CONTEXT_ID_FIELD).is("id2"));
		expectedId2Query.fields().include("_id");

		String expectedCollectionName = String.format("model_%s", modelConfName);
		verify(mongoTemplate, times(1)).findOne(eq(expectedId1Query), eq(ModelDAO.class), eq(expectedCollectionName));
		verify(mongoTemplate, times(1)).findOne(eq(expectedId2Query), eq(ModelDAO.class), eq(expectedCollectionName));
		ArgumentCaptor<ModelDAO> modelDaoArgCaptor = ArgumentCaptor.forClass(ModelDAO.class);
		verify(mongoTemplate, times(2)).insert(modelDaoArgCaptor.capture(), eq(expectedCollectionName));
		verifyNoMoreInteractions(mongoTemplate);

		ContinuousDataModel expectedId1Model = new ContinuousDataModel();
		expectedId1Model.setParameters(96, 316.667, 81.223);
		ContinuousDataModel expectedId2Model = new ContinuousDataModel();
		expectedId2Model.setParameters(75, 7.319, 6.540);

		ModelDAO actualModelDao = modelDaoArgCaptor.getAllValues().get(0);
		Assert.assertEquals(sessionId, actualModelDao.getSessionId());
		Assert.assertEquals("id1", actualModelDao.getContextId());
		Assert.assertEquals(expectedId1Model, actualModelDao.getModel());
		Assert.assertEquals(currentEndTime, actualModelDao.getEndTime());

		actualModelDao = modelDaoArgCaptor.getAllValues().get(1);
		Assert.assertEquals(sessionId, actualModelDao.getSessionId());
		Assert.assertEquals("id2", actualModelDao.getContextId());
		Assert.assertEquals(expectedId2Model, actualModelDao.getModel());
		Assert.assertEquals(currentEndTime, actualModelDao.getEndTime());
	}

	private static FeatureBucket createFeatureBucketWithGenericHistogram(String featureName, GenericHistogram genericHistogram) {
		FeatureBucket featureBucket = new FeatureBucket();
		Map<String, Feature> aggregatedFeatures = featureBucket.getAggregatedFeatures();
		aggregatedFeatures.put(featureName, new Feature(featureName, genericHistogram));
		return featureBucket;
	}

	private static JSONObject buildStatus(String modelConfName, String contextId, Date endTime, boolean success) {
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
		public void modelBuildingStatus(String modelConfName, String sessionId, String contextId, Date endTime, ModelBuildingStatus status) {
			statuses.add(buildStatus(modelConfName, contextId, endTime, status.equals(ModelBuildingStatus.SUCCESS)));
		}

		@Override
		public void modelBuildingSummary(String modelConfName, String sessionId, Date endTime, long numOfSuccesses, long numOfFailures) {}

		public List<JSONObject> getStatuses() {
			return statuses;
		}
	}
}
