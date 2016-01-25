package fortscale.streaming.service.model;

import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.CategoryRarityModelWithFeatureOccurrencesData;
import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.cache.ModelsCacheInfo;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AbstractDataRetrieverConf;
import fortscale.ml.model.retriever.ContextHistogramRetrieverConf;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStore;
import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.task.KeyValueStoreMock;
import fortscale.utils.factory.FactoryService;
import org.apache.samza.config.Config;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.*;
import java.util.stream.Collectors;

import static fortscale.utils.time.TimestampUtils.convertToSeconds;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ModelsCacheServiceSamzaTest {
	private static final String STORE_NAME_PROPERTY = "fortscale.model.cache.managers.store.name";
	private static final String STORE_NAME = "unit-test-store";
	private static final String DEFAULT_SESSION_ID = "testSession";
	private static final String NORMALIZED_USERNAME_CONTEXT = "normalized_username";
	private static final String DEFAULT_NORMALIZED_USERNAME = "user@fortscale.com";
	private static final String DEFAULT_CONTEXT_ID = "normalized_username:user@fortscale.com";

	private static ClassPathXmlApplicationContext context;

	private FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;
	private ModelStore mongo;
	private ModelConfService modelConfService;
	private KeyValueStoreMock<String, ModelsCacheInfo> cache;
	private ExtendedSamzaTaskContext extendedSamzaTaskContext;

	@BeforeClass
	public static void setUpClass() {
		context = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/model-cache-manager-samza-test-context.xml");
	}

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		dataRetrieverFactoryService = context.getBean(FactoryService.class);
		mongo = context.getBean(ModelStore.class);
		modelConfService = context.getBean(ModelConfService.class);
		reset(dataRetrieverFactoryService, mongo, modelConfService);

		cache = new KeyValueStoreMock<>();
		extendedSamzaTaskContext = mock(ExtendedSamzaTaskContext.class);
		Config config = mock(Config.class);
		when(extendedSamzaTaskContext.getConfig()).thenReturn(config);
		when(extendedSamzaTaskContext.getStore(eq(STORE_NAME))).thenReturn(cache);
		when(config.get(eq(STORE_NAME_PROPERTY))).thenReturn(STORE_NAME);
	}

	@Test
	public void models_cache_service_should_get_the_correct_models() {
		String modelConfName1 = "testModelConf1";
		String modelConfName2 = "testModelConf2";
		String modelConfName3 = "testModelConf3";
		String factoryName1 = "testFactory1";
		String factoryName2 = "testFactory2";
		String factoryName3 = ContextHistogramRetrieverConf.CONTEXT_HISTOGRAM_RETRIEVER;
		List<MocksContainer> containers = createMocks(
				new String[]{modelConfName1, modelConfName2, modelConfName3},
				new String[]{factoryName1, factoryName2, factoryName3});
		ModelsCacheService modelsCacheService = new ModelsCacheServiceSamza(extendedSamzaTaskContext);

		// 1st case - regular, outdated in mongo
		Date endTime1 = new Date();
		ModelDAO modelDao1 = new ModelDAO(DEFAULT_SESSION_ID, DEFAULT_CONTEXT_ID, mock(Model.class), minusDay(endTime1), endTime1);
		when(containers.get(0).getRetriever().getContextId(eq(getDefaultStringContext()))).thenReturn(DEFAULT_CONTEXT_ID);
		when(mongo.getModelDaos(eq(containers.get(0).getModelConf()), eq(DEFAULT_CONTEXT_ID))).thenReturn(Collections.singletonList(modelDao1));
		Model model1 = modelsCacheService.getModel(mock(Feature.class), modelConfName1, getDefaultFeatureContext(), convertToSeconds(plusOutdated(endTime1)));
		Assert.assertEquals(modelDao1.getModel(), model1);

		// 2nd case - regular, outdated in cache
		Date endTime2 = new Date();
		ModelDAO modelDao2 = new ModelDAO(DEFAULT_SESSION_ID, DEFAULT_CONTEXT_ID, mock(Model.class), minusDay(endTime2), endTime2);
		ModelsCacheInfo modelsCacheInfo2 = new ModelsCacheInfo();
		modelsCacheInfo2.setModelDao(modelDao2);
		when(containers.get(1).getRetriever().getContextId(eq(getDefaultStringContext()))).thenReturn(DEFAULT_CONTEXT_ID);
		cache.put(ModelCacheManagerSamza.getStoreKey(containers.get(1).getModelConf(), DEFAULT_CONTEXT_ID), modelsCacheInfo2);
		Model model2 = modelsCacheService.getModel(mock(Feature.class), modelConfName2, getDefaultFeatureContext(), convertToSeconds(plusOutdated(endTime2)));
		Assert.assertEquals(modelDao2.getModel(), model2);

		// 3rd case - discrete, concurrent in cache
		CategoryRarityModelWithFeatureOccurrencesData expectedModel3 = new CategoryRarityModelWithFeatureOccurrencesData(new HashMap<>());
		Date endTime3 = new Date();
		ModelDAO modelDao3 = new ModelDAO(DEFAULT_SESSION_ID, DEFAULT_CONTEXT_ID, expectedModel3, minusDay(endTime3), endTime3);
		ModelsCacheInfo modelsCacheInfo3 = new ModelsCacheInfo();
		modelsCacheInfo3.setModelDao(modelDao3);
		when(containers.get(2).getRetriever().getContextId(eq(getDefaultStringContext()))).thenReturn(DEFAULT_CONTEXT_ID);
		cache.put(ModelCacheManagerSamza.getStoreKey(containers.get(2).getModelConf(), DEFAULT_CONTEXT_ID), modelsCacheInfo3);

		Feature feature3 = new Feature("country", "Israel");
		GenericHistogram histogram3 = new GenericHistogram();
		histogram3.add(feature3.getValue().toString(), 5d);
		when(containers.get(2).getRetriever().retrieve(eq(DEFAULT_CONTEXT_ID), eq(endTime3), eq(feature3))).thenReturn(histogram3);
		Model actualModel3 = modelsCacheService.getModel(feature3, modelConfName3, getDefaultFeatureContext(), convertToSeconds(plusDay(endTime3)));
		Assert.assertEquals(expectedModel3, actualModel3);

		// Check cache
		Assert.assertEquals(3, cache.size());
		ModelsCacheInfo modelsCacheInfo = cache.get(ModelCacheManagerSamza.getStoreKey(containers.get(0).getModelConf(), DEFAULT_CONTEXT_ID));
		Assert.assertEquals(1, modelsCacheInfo.getNumOfModelDaos());
		modelsCacheInfo = cache.get(ModelCacheManagerSamza.getStoreKey(containers.get(1).getModelConf(), DEFAULT_CONTEXT_ID));
		Assert.assertEquals(1, modelsCacheInfo.getNumOfModelDaos());
		modelsCacheInfo = cache.get(ModelCacheManagerSamza.getStoreKey(containers.get(2).getModelConf(), DEFAULT_CONTEXT_ID));
		Assert.assertEquals(1, modelsCacheInfo.getNumOfModelDaos());
		ModelDAO modelDao = modelsCacheInfo.getModelDaoWithLatestEndTimeLte(convertToSeconds(new Date()));
		Assert.assertEquals(modelDao3, modelDao);
		Assert.assertEquals(5, expectedModel3.getFeatureCount(feature3), 0);
	}

	@Test
	public void models_cache_service_should_clean_the_cache_during_the_window() {
		Assert.assertEquals(0, cache.size());
		long currentTimeSeconds = convertToSeconds(new Date());

		ModelsCacheInfo modelsCacheInfo1 = new ModelsCacheInfo();
		modelsCacheInfo1.setLastUsageEpochtime(currentTimeSeconds - 864000); // minus 10 days
		cache.put("key1", modelsCacheInfo1);
		ModelsCacheInfo modelsCacheInfo2 = new ModelsCacheInfo();
		modelsCacheInfo2.setLastUsageEpochtime(currentTimeSeconds - 777600); // minus 9 days
		cache.put("key2", modelsCacheInfo2);
		ModelsCacheInfo modelsCacheInfo3 = new ModelsCacheInfo();
		modelsCacheInfo3.setLastUsageEpochtime(currentTimeSeconds - 691200); // minus 8 days
		cache.put("key3", modelsCacheInfo3);
		ModelsCacheInfo modelsCacheInfo4 = new ModelsCacheInfo();
		modelsCacheInfo4.setLastUsageEpochtime(currentTimeSeconds - 518400); // minus 6 days
		cache.put("key4", modelsCacheInfo4);
		ModelsCacheInfo modelsCacheInfo5 = new ModelsCacheInfo();
		modelsCacheInfo5.setLastUsageEpochtime(currentTimeSeconds - 432000); // minus 5 days
		cache.put("key5", modelsCacheInfo5);

		// Time difference to clean cache is set in properties file to 7 days
		new ModelsCacheServiceSamza(extendedSamzaTaskContext).window();

		Assert.assertEquals(2, cache.size());
		Assert.assertEquals(modelsCacheInfo4, cache.get("key4"));
		Assert.assertEquals(modelsCacheInfo5, cache.get("key5"));
	}

	private List<MocksContainer> createMocks(String[] modelConfNames, String[] factoryNames) {
		List<MocksContainer> containers = new ArrayList<>();

		for (int i = 0; i < modelConfNames.length; i++) {
			ModelConf modelConf = mock(ModelConf.class);
			AbstractDataRetrieverConf retrieverConf = mock(AbstractDataRetrieverConf.class);
			AbstractDataRetriever retriever = mock(AbstractDataRetriever.class);
			when(modelConf.getName()).thenReturn(modelConfNames[i]);
			when(modelConf.getDataRetrieverConf()).thenReturn(retrieverConf);
			when(retrieverConf.getFactoryName()).thenReturn(factoryNames[i]);
			when(dataRetrieverFactoryService.getProduct(eq(retrieverConf))).thenReturn(retriever);

			containers.add(new MocksContainer(modelConf, retriever));
		}

		when(modelConfService.getModelConfs()).thenReturn(containers.stream().map(MocksContainer::getModelConf).collect(Collectors.toList()));
		return containers;
	}

	private static Map<String, String> getDefaultStringContext() {
		Map<String, String> context = new HashMap<>();
		context.put(NORMALIZED_USERNAME_CONTEXT, DEFAULT_NORMALIZED_USERNAME);
		return context;
	}

	private static Map<String, Feature> getDefaultFeatureContext() {
		Map<String, Feature> context = new HashMap<>();
		context.put(NORMALIZED_USERNAME_CONTEXT, new Feature("contextField", DEFAULT_NORMALIZED_USERNAME));
		return context;
	}

	private static Date minusDay(Date time) {
		// 86400000 = 24 hours in milliseconds
		return new Date(time.getTime() - 86400000);
	}

	private static Date plusDay(Date time) {
		// 86400000 = 24 hours in milliseconds
		return new Date(time.getTime() + 86400000);
	}

	private static Date plusOutdated(Date time) {
		// 2592000000 = outdated model time difference in milliseconds (consistent with the properties file)
		return new Date(time.getTime() + 2592000000L + 3600000);
	}

	private static final class MocksContainer {
		private ModelConf modelConf;
		private AbstractDataRetriever retriever;

		public MocksContainer(ModelConf modelConf, AbstractDataRetriever retriever) {
			this.modelConf = modelConf;
			this.retriever = retriever;
		}

		public ModelConf getModelConf() {
			return modelConf;
		}

		public AbstractDataRetriever getRetriever() {
			return retriever;
		}
	}
}
