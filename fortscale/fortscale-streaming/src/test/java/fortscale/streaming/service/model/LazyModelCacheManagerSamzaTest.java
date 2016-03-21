package fortscale.streaming.service.model;

import fortscale.common.feature.Feature;
import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.cache.ModelCacheManager;
import fortscale.ml.model.cache.ModelsCacheInfo;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AbstractDataRetrieverConf;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStore;
import fortscale.streaming.common.SamzaContainerService;
import fortscale.streaming.task.KeyValueStoreMock;
import fortscale.utils.factory.FactoryService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.samza.storage.kv.Entry;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static fortscale.utils.time.TimestampUtils.convertToSeconds;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class LazyModelCacheManagerSamzaTest {
	private static final String DEFAULT_MODEL_CONF_NAME = "testModelConf";
	private static final String DEFAULT_SESSION_ID = "testSession";
	private static final String NORMALIZED_USERNAME_CONTEXT = "normalized_username";
	private static final String DEFAULT_NORMALIZED_USERNAME = "user@fortscale.com";
	private static final String DEFAULT_CONTEXT_ID = "normalized_username:user@fortscale.com";
	private static final Map<String, String> defaultStringContext =
			getStringContext(NORMALIZED_USERNAME_CONTEXT, DEFAULT_NORMALIZED_USERNAME);
	private static final Map<String, String> defaultFeatureContext =
			getFeatureContext(NORMALIZED_USERNAME_CONTEXT, DEFAULT_NORMALIZED_USERNAME);

	private static ClassPathXmlApplicationContext context;

	private ModelStore mongo;
	private KeyValueStoreMock<String, ModelsCacheInfo> cache;
	private ModelConf modelConf;
	private AbstractDataRetriever retriever;
	private ModelCacheManager modelCacheManager;

	@BeforeClass
	public static void setUpClass() {
		context = new ClassPathXmlApplicationContext(
				"classpath*:META-INF/spring/model-cache-manager-samza-test-context.xml");
	}

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		SamzaContainerService samzaContainerService = context.getBean(SamzaContainerService.class);
		FactoryService<AbstractDataRetriever> dataRetrieverFactoryService = context.getBean(FactoryService.class);
		mongo = context.getBean(ModelStore.class);
		reset(dataRetrieverFactoryService, mongo);
		cache = new KeyValueStoreMock<>();
		modelConf = mock(ModelConf.class);
		retriever = mock(AbstractDataRetriever.class);
		AbstractDataRetrieverConf retrieverConf = mock(AbstractDataRetrieverConf.class);
		String storeName = "the_store_name";
		when(samzaContainerService.getStore(storeName)).thenReturn(cache);
		when(dataRetrieverFactoryService.getProduct(eq(retrieverConf))).thenReturn(retriever);
		when(modelConf.getName()).thenReturn(DEFAULT_MODEL_CONF_NAME);
		when(modelConf.getDataRetrieverConf()).thenReturn(retrieverConf);
		modelCacheManager = new LazyModelCacheManagerSamza(storeName, modelConf);
	}

	@Test
	public void get_model_when_cache_is_empty_and_mongo_is_empty() {
		Date eventTime = new Date();
		populateForDefault(null, null, null, null, null);
		Model actualModel = getModelForDefault(eventTime);
		verifyForDefault(true, null, actualModel, null);
	}

	@Test
	public void get_model_when_cache_is_empty_and_mongo_has_an_expired_model() {
		Date eventTime = new Date();
		Date mongoEndTime = expired(eventTime);
		Date mongoStartTime = minusDay(mongoEndTime);
		Pair<ModelDAO, ModelDAO> pair = populateForDefault(mongoStartTime, mongoEndTime, null, null, null);
		Model actualModel = getModelForDefault(eventTime);
		verifyForDefault(true, null, actualModel, pair.getLeft());
	}

	@Test
	public void get_model_when_cache_is_empty_and_mongo_has_an_outdated_model() {
		Date eventTime = new Date();
		Date mongoEndTime = outdated(eventTime);
		Date mongoStartTime = minusDay(mongoEndTime);
		Pair<ModelDAO, ModelDAO> pair = populateForDefault(mongoStartTime, mongoEndTime, null, null, null);
		Model actualModel = getModelForDefault(eventTime);
		verifyForDefault(true, pair.getLeft().getModel(), actualModel, pair.getLeft());
	}

	@Test
	public void get_model_when_cache_is_empty_and_mongo_has_a_concurrent_model() {
		Date eventTime = new Date();
		Date mongoEndTime = minusDay(eventTime);
		Date mongoStartTime = minusDay(mongoEndTime);
		Pair<ModelDAO, ModelDAO> pair = populateForDefault(mongoStartTime, mongoEndTime, null, null, null);
		Model actualModel = getModelForDefault(eventTime);
		verifyForDefault(true, pair.getLeft().getModel(), actualModel, pair.getLeft());
	}

	@Test
	public void get_model_when_cache_has_an_expired_model_and_mongo_is_empty() {
		Date eventTime = new Date();
		Date cacheEndTime = expired(eventTime);
		Date cacheStartTime = minusDay(cacheEndTime);
		populateForDefault(null, null, cacheStartTime, cacheEndTime, nowMinusLoadWait());
		Model actualModel = getModelForDefault(eventTime);
		verifyForDefault(true, null, actualModel, null);
	}

	@Test
	public void get_model_when_cache_has_an_expired_model_and_mongo_has_an_expired_model() {
		Date eventTime = new Date();
		Date mongoEndTime = expired(eventTime);
		Date mongoStartTime = minusDay(mongoEndTime);
		Date cacheEndTime = minusDay(mongoEndTime);
		Date cacheStartTime = minusDay(cacheEndTime);
		Pair<ModelDAO, ModelDAO> pair = populateForDefault(mongoStartTime, mongoEndTime, cacheStartTime, cacheEndTime, nowMinusLoadWait());
		Model actualModel = getModelForDefault(eventTime);
		verifyForDefault(true, null, actualModel, pair.getLeft());
	}

	@Test
	public void get_model_when_cache_has_an_expired_model_and_mongo_has_an_outdated_model() {
		Date eventTime = new Date();
		Date mongoEndTime = outdated(eventTime);
		Date mongoStartTime = minusDay(mongoEndTime);
		Date cacheEndTime = expired(eventTime);
		Date cacheStartTime = minusDay(cacheEndTime);
		Pair<ModelDAO, ModelDAO> pair = populateForDefault(mongoStartTime, mongoEndTime, cacheStartTime, cacheEndTime, nowMinusLoadWait());
		Model actualModel = getModelForDefault(eventTime);
		verifyForDefault(true, pair.getLeft().getModel(), actualModel, pair.getLeft());
	}

	@Test
	public void get_model_when_cache_has_an_expired_model_and_mongo_has_a_concurrent_model() {
		Date eventTime = new Date();
		Date mongoEndTime = minusDay(eventTime);
		Date mongoStartTime = minusDay(mongoEndTime);
		Date cacheEndTime = expired(eventTime);
		Date cacheStartTime = minusDay(cacheEndTime);
		Pair<ModelDAO, ModelDAO> pair = populateForDefault(mongoStartTime, mongoEndTime, cacheStartTime, cacheEndTime, nowMinusLoadWait());
		Model actualModel = getModelForDefault(eventTime);
		verifyForDefault(true, pair.getLeft().getModel(), actualModel, pair.getLeft());
	}

	@Test
	public void get_model_when_cache_has_an_outdated_model_and_mongo_is_empty() {
		Date eventTime = new Date();
		Date cacheEndTime = outdated(eventTime);
		Date cacheStartTime = minusDay(cacheEndTime);
		populateForDefault(null, null, cacheStartTime, cacheEndTime, nowMinusLoadWait());
		Model actualModel = getModelForDefault(eventTime);
		verifyForDefault(true, null, actualModel, null);
	}

	@Test
	public void get_model_when_cache_has_an_outdated_model_and_mongo_has_an_expired_model() {
		Date eventTime = new Date();
		Date mongoEndTime = expired(eventTime);
		Date mongoStartTime = minusDay(mongoEndTime);
		Date cacheEndTime = outdated(eventTime);
		Date cacheStartTime = minusDay(cacheEndTime);
		Pair<ModelDAO, ModelDAO> pair = populateForDefault(mongoStartTime, mongoEndTime, cacheStartTime, cacheEndTime, nowMinusLoadWait());
		Model actualModel = getModelForDefault(eventTime);
		verifyForDefault(true, null, actualModel, pair.getLeft());
	}

	@Test
	public void get_model_when_cache_has_an_outdated_model_and_mongo_has_an_outdated_model() {
		Date eventTime = new Date();
		Date mongoEndTime = outdated(eventTime);
		Date mongoStartTime = minusDay(mongoEndTime);
		Date cacheEndTime = minusDay(mongoEndTime);
		Date cacheStartTime = minusDay(cacheEndTime);
		Pair<ModelDAO, ModelDAO> pair = populateForDefault(mongoStartTime, mongoEndTime, cacheStartTime, cacheEndTime, nowMinusLoadWait());
		Model actualModel = getModelForDefault(eventTime);
		verifyForDefault(true, pair.getLeft().getModel(), actualModel, pair.getLeft());
	}

	@Test
	public void get_model_when_cache_has_an_outdated_model_and_mongo_has_a_concurrent_model() {
		Date eventTime = new Date();
		Date mongoEndTime = minusDay(eventTime);
		Date mongoStartTime = minusDay(mongoEndTime);
		Date cacheEndTime = outdated(eventTime);
		Date cacheStartTime = minusDay(cacheEndTime);
		Pair<ModelDAO, ModelDAO> pair = populateForDefault(mongoStartTime, mongoEndTime, cacheStartTime, cacheEndTime, nowMinusLoadWait());
		Model actualModel = getModelForDefault(eventTime);
		verifyForDefault(true, pair.getLeft().getModel(), actualModel, pair.getLeft());
	}

	@Test
	public void get_model_when_cache_has_a_concurrent_model() {
		Date eventTime = new Date();
		Date cacheEndTime = minusDay(eventTime);
		Date cacheStartTime = minusDay(cacheEndTime);
		Pair<ModelDAO, ModelDAO> pair = populateForDefault(null, null, cacheStartTime, cacheEndTime, nowMinusLoadWait());
		Model actualModel = getModelForDefault(eventTime);
		verifyForDefault(false, pair.getRight().getModel(), actualModel, pair.getRight());
	}

	@Test
	public void model_cache_manager_should_not_overload_mongo() {
		Date eventTime = new Date();
		Date mongoEndTime = minusDay(eventTime);
		Date mongoStartTime = minusDay(mongoEndTime);
		Date cacheEndTime = outdated(eventTime);
		Date cacheStartTime = minusDay(cacheEndTime);
		Pair<ModelDAO, ModelDAO> pair = populateForDefault(mongoStartTime, mongoEndTime, cacheStartTime, cacheEndTime, null);
		Model actualModel = getModelForDefault(eventTime);
		verifyForDefault(false, pair.getRight().getModel(), actualModel, pair.getRight());

		// Last load time set to 50 seconds ago - should return cache model
		tryToLoadFromMongoScenarioForDefault(convertToSeconds(eventTime) - 50, eventTime, pair.getRight(), false);
		// Last load time set to 100 seconds ago - should return cache model
		tryToLoadFromMongoScenarioForDefault(convertToSeconds(eventTime) - 100, eventTime, pair.getRight(), false);
		// Last load time set to 150 seconds ago - should load and return mongo model
		tryToLoadFromMongoScenarioForDefault(convertToSeconds(eventTime) - 150, eventTime, pair.getLeft(), true);
	}

	private void tryToLoadFromMongoScenarioForDefault(long lastLoadEpochtime, Date eventTime, ModelDAO expectedModelDao, boolean mongoInteraction) {
		ModelsCacheInfo modelsCacheInfo = cache.get(ModelCacheManagerSamza.getStoreKey(modelConf, DEFAULT_CONTEXT_ID));
		modelsCacheInfo.setLastLoadEpochtime(lastLoadEpochtime);
		Model actualModel = getModelForDefault(eventTime);
		Assert.assertEquals(expectedModelDao.getModel(), actualModel);
		if (mongoInteraction) verify(mongo, times(1)).getModelDaos(eq(modelConf), eq(DEFAULT_CONTEXT_ID));
		verifyNoMoreInteractions(mongo);
		modelsCacheInfo = cache.get(ModelCacheManagerSamza.getStoreKey(modelConf, DEFAULT_CONTEXT_ID));
		Assert.assertEquals(1, modelsCacheInfo.getNumOfModelDaos());
		Assert.assertEquals(expectedModelDao, modelsCacheInfo.getModelDaoWithLatestEndTimeLte(convertToSeconds(eventTime)));
	}

	private Pair<ModelDAO, ModelDAO> populateForDefault(Date mongoStartTime, Date mongoEndTime, Date cacheStartTime, Date cacheEndTime, Date lastLoadTime) {
		ModelDAO mongoModelDao = null;
		ModelDAO cacheModelDao = null;

		if (mongoStartTime != null && mongoEndTime != null) {
			mongoModelDao = new ModelDAO(DEFAULT_SESSION_ID, DEFAULT_CONTEXT_ID, mock(Model.class), mongoStartTime, mongoEndTime);
			when(mongo.getModelDaos(eq(modelConf), eq(DEFAULT_CONTEXT_ID))).thenReturn(Collections.singletonList(mongoModelDao));
		}

		if (cacheStartTime != null && cacheEndTime != null) {
			cacheModelDao = new ModelDAO(DEFAULT_SESSION_ID, DEFAULT_CONTEXT_ID, mock(Model.class), cacheStartTime, cacheEndTime);
			ModelsCacheInfo modelsCacheInfo = new ModelsCacheInfo();
			modelsCacheInfo.setModelDao(cacheModelDao);
			if (lastLoadTime != null) modelsCacheInfo.setLastLoadEpochtime(convertToSeconds(lastLoadTime));
			cache.put(ModelCacheManagerSamza.getStoreKey(modelConf, DEFAULT_CONTEXT_ID), modelsCacheInfo);
		}

		when(retriever.getContextId(eq(defaultStringContext))).thenReturn(DEFAULT_CONTEXT_ID);
		return new ImmutablePair<>(mongoModelDao, cacheModelDao);
	}

	private Model getModelForDefault(Date eventTime) {
		return modelCacheManager.getModel(mock(Feature.class), defaultFeatureContext, convertToSeconds(eventTime));
	}

	private void verifyForDefault(boolean mongoInteraction, Model expectedModel, Model actualModel, ModelDAO expectedModelDaoInCache) {
		verify(retriever, times(1)).getContextId(eq(defaultStringContext));
		if (mongoInteraction) verify(mongo, times(1)).getModelDaos(eq(modelConf), eq(DEFAULT_CONTEXT_ID));
		verifyNoMoreInteractions(retriever, mongo);
		Assert.assertEquals(expectedModel, actualModel);
		Assert.assertEquals(1, cache.size());
		KeyValueIterator<String, ModelsCacheInfo> iterator = cache.all();

		while (iterator.hasNext()) {
			Entry<String, ModelsCacheInfo> next = iterator.next();
			Assert.assertEquals(expectedModelDaoInCache == null ? 0 : 1, next.getValue().getNumOfModelDaos());

			if (expectedModelDaoInCache != null) {
				long currentEpochtime = convertToSeconds(new Date());
				ModelDAO modelDaoWithLatestEndTime = next.getValue().getModelDaoWithLatestEndTimeLte(currentEpochtime);
				Assert.assertEquals(expectedModelDaoInCache, modelDaoWithLatestEndTime);
			}
		}
	}

	private static Map<String, String> getStringContext(String... args) {
		Map<String, String> context = new HashMap<>();
		for (int i = 0; i < args.length; i += 2)
			context.put(args[i], args[i + 1]);
		return context;
	}

	private static Map<String, String> getFeatureContext(String... args) {
		Map<String, String> context = new HashMap<>();
		for (int i = 0; i < args.length; i += 2)
			context.put(args[i], args[i + 1]);
		return context;
	}

	// Following 3 constants are consistent with the properties file
	private static final long LOAD_WAIT_MILLIS = 120000 + 60000;
	private static final long OUTDATED_MILLIS = 2592000000L + 3600000;
	private static final long EXPIRED_MILLIS = 15552000000L + 3600000;
	private static final long DAY_MILLIS = 86400000;

	private static Date nowMinusLoadWait() {
		return new Date(System.currentTimeMillis() - LOAD_WAIT_MILLIS);
	}

	private static Date outdated(Date time) {
		return new Date(time.getTime() - OUTDATED_MILLIS);
	}

	private static Date expired(Date time) {
		return new Date(time.getTime() - EXPIRED_MILLIS);
	}

	private static Date minusDay(Date time) {
		return new Date(time.getTime() - DAY_MILLIS);
	}
}
