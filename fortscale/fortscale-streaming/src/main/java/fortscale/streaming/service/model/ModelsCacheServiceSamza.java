package fortscale.streaming.service.model;

import fortscale.common.feature.Feature;
import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.cache.ModelCacheManager;
import fortscale.ml.model.cache.ModelsCacheInfo;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.model.retriever.ContextHistogramRetrieverConf;
import fortscale.streaming.ConfigUtils;
import fortscale.streaming.common.SamzaContainerInitializedListener;
import fortscale.streaming.common.SamzaContainerService;
import fortscale.utils.time.TimestampUtils;
import org.apache.samza.storage.kv.Entry;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ModelsCacheServiceSamza implements ModelsCacheService, InitializingBean, SamzaContainerInitializedListener {
	public static final String STORE_NAME_PROPERTY = "fortscale.model.cache.managers.store.name";

	@Autowired
	private ModelConfService modelConfService;

	@Autowired
	private SamzaContainerService samzaContainerService;

	@Value("${fortscale.model.max.sec.diff.before.cleaning.cache}")
	private long maxSecDiffBeforeCleaningCache;

	private Map<String, ModelCacheManager> modelCacheManagers;


	private Map<String, ModelCacheManager> getModelCacheManagers(){
		if(modelCacheManagers == null) {
			loadCacheManagers();
		}
		return modelCacheManagers;
	}

	@Override
	public Model getModel(Feature feature, String modelConfName, Map<String, Feature> context, long eventEpochtime) {
		if (getModelCacheManagers().containsKey(modelConfName)) {
			return getModelCacheManagers().get(modelConfName).getModel(feature, context, eventEpochtime);
		} else {
			return null;
		}
	}

	@Override
	public void window() {
		KeyValueStore<String, ModelsCacheInfo> store = getStore();
		KeyValueIterator<String, ModelsCacheInfo> iterator = store.all();
		long currentEpochtime = TimestampUtils.convertToSeconds(System.currentTimeMillis());
		List<String> keysToClean = new ArrayList<>();

		while (iterator.hasNext()) {
			Entry<String, ModelsCacheInfo> entry = iterator.next();

			if (currentEpochtime - entry.getValue().getLastUsageEpochtime() > maxSecDiffBeforeCleaningCache) {
				keysToClean.add(entry.getKey());
			}
		}

		iterator.close();
		keysToClean.forEach(store::delete);
	}

	private KeyValueStore<String, ModelsCacheInfo> getStore(){
		String storeName = getStoreName();
		KeyValueStore<String, ModelsCacheInfo> store = (KeyValueStore<String, ModelsCacheInfo>)samzaContainerService.getStore(storeName);
		return store;
	}

	private String getStoreName(){
		return ConfigUtils.getConfigString(samzaContainerService.getConfig(), STORE_NAME_PROPERTY);
	}

	@Override
	public void close() {}

	private static boolean isDiscreteModelConf(ModelConf modelConf) {
		String factoryName = modelConf.getDataRetrieverConf().getFactoryName();
		return factoryName.equals(ContextHistogramRetrieverConf.CONTEXT_HISTOGRAM_RETRIEVER);
	}

	public void loadCacheManagers(){
		modelCacheManagers = new HashMap<>();

		for (ModelConf modelConf : modelConfService.getModelConfs()) {
			ModelCacheManager modelCacheManager = isDiscreteModelConf(modelConf) ?
					new DiscreteModelCacheManagerSamza(getStoreName(), modelConf) :
					new LazyModelCacheManagerSamza(getStoreName(), modelConf);
			modelCacheManagers.put(modelConf.getName(), modelCacheManager);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		samzaContainerService.registerSamzaContainerInitializedListener(this);
	}

	@Override
	public void afterSamzaContainerInitialized() {
		loadCacheManagers();
	}
}
