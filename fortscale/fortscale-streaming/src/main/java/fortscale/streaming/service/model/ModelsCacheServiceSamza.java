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
import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.utils.time.TimestampUtils;
import org.apache.samza.storage.kv.Entry;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configurable(preConstruction = true)
public class ModelsCacheServiceSamza implements ModelsCacheService {
	private static final String STORE_NAME_PROPERTY = "fortscale.model.cache.managers.store.name";

	@Autowired
	private ModelConfService modelConfService;

	@Value("${fortscale.model.max.sec.diff.before.cleaning.cache}")
	private long maxSecDiffBeforeCleaningCache;

	private KeyValueStore<String, ModelsCacheInfo> store;
	private Map<String, ModelCacheManager> modelCacheManagers;

	@SuppressWarnings("unchecked")
	public ModelsCacheServiceSamza(ExtendedSamzaTaskContext context) {
		String storeName = ConfigUtils.getConfigString(context.getConfig(), STORE_NAME_PROPERTY);
		store = (KeyValueStore<String, ModelsCacheInfo>)context.getStore(storeName);
		Assert.notNull(store);
		modelCacheManagers = new HashMap<>();

		for (ModelConf modelConf : modelConfService.getModelConfs()) {
			ModelCacheManager modelCacheManager = isDiscreteModelConf(modelConf) ?
					new DiscreteModelCacheManagerSamza(store, modelConf) :
					new LazyModelCacheManagerSamza(store, modelConf);
			modelCacheManagers.put(modelConf.getName(), modelCacheManager);
		}
	}

	@Override
	public Model getModel(Feature feature, String modelConfName, Map<String, Feature> context, long eventEpochtime) {
		if (modelCacheManagers.containsKey(modelConfName)) {
			return modelCacheManagers.get(modelConfName).getModel(feature, context, eventEpochtime);
		} else {
			return null;
		}
	}

	@Override
	public void window() {
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

	@Override
	public void close() {}

	private static boolean isDiscreteModelConf(ModelConf modelConf) {
		String factoryName = modelConf.getDataRetrieverConf().getFactoryName();
		return factoryName.equals(ContextHistogramRetrieverConf.CONTEXT_HISTOGRAM_RETRIEVER);
	}
}
