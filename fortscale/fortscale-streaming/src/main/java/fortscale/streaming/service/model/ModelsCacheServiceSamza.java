package fortscale.streaming.service.model;

import fortscale.common.feature.Feature;
import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.builder.CategoryRarityModelBuilderConf;
import fortscale.ml.model.cache.ModelCacheManager;
import fortscale.ml.model.cache.ModelsCacheInfo;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.streaming.ConfigUtils;
import fortscale.streaming.common.SamzaContainerInitializedListener;
import fortscale.streaming.common.SamzaContainerService;
import fortscale.utils.logging.Logger;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fortscale.utils.time.TimestampUtils.convertToSeconds;

public class ModelsCacheServiceSamza implements ModelsCacheService, InitializingBean, SamzaContainerInitializedListener {
	private static final String NULL_VALUE_ERROR_MSG_FORMAT = String.format(
			"{} iterator indicates that the following key is present in the store, "
			.concat("but the getter returns a null value - skipping the key. ")
			.concat("Key = {}, expected value type = %s."), ModelsCacheInfo.class.getSimpleName());
	private static final Logger logger = Logger.getLogger(ModelsCacheServiceSamza.class);
	public static final String STORE_NAME_PROPERTY = "fortscale.model.cache.managers.store.name";

	@Autowired
	private ModelConfService modelConfService;

	@Autowired
	private SamzaContainerService samzaContainerService;

	/* If the last usage of a certain model in the cache was more than {@code maxSecDiffBeforeCleaningCache}
	 * seconds ago, the model will be deleted from the cache (i.e. delete unused models from the cache) */
	@Value("${fortscale.model.max.sec.diff.before.cleaning.cache}")
	private long maxSecDiffBeforeCleaningCache;

	/* clean the cache (delete unused models from it) every
	 * {@code secDiffBetweenCleaningCacheChecks} seconds (or more) */
	@Value("${fortscale.model.sec.diff.between.cleaning.cache.checks}")
	private long secDiffBetweenCleaningCacheChecks;

	private Map<String, ModelCacheManager> modelCacheManagers;
	private long lastCleaningCacheEpochtime = convertToSeconds(System.currentTimeMillis());

	private Map<String, ModelCacheManager> getModelCacheManagers() {
		if (modelCacheManagers == null) {
			loadCacheManagers();
		}

		return modelCacheManagers;
	}

	@Override
	public Model getModel(Feature feature, String modelConfName, Map<String, String> context, long eventEpochtime) {
		if (getModelCacheManagers().containsKey(modelConfName)) {
			return getModelCacheManagers().get(modelConfName).getModel(feature, context, eventEpochtime);
		} else {
			return null;
		}
	}

	@Override
	public void window() {
		long currentEpochtime = convertToSeconds(System.currentTimeMillis());

		// Check if it's time to clean the cache from unused models - return if not
		if (currentEpochtime - lastCleaningCacheEpochtime < secDiffBetweenCleaningCacheChecks) {
			logger.info("Not going to clean unused models from cache in this window. Set to clean every {} seconds.",
					secDiffBetweenCleaningCacheChecks);
			return;
		}

		logger.info("Going to clean unused models from cache. Set to clean every {} seconds.",
				secDiffBetweenCleaningCacheChecks);
		KeyValueStore<String, ModelsCacheInfo> store = getStore();
		KeyValueIterator<String, ModelsCacheInfo> iterator = null;

		try {
			iterator = store.all();
			List<String> keysToClean = new ArrayList<>();

			while (iterator.hasNext()) {
				String key = iterator.next().getKey();
				ModelsCacheInfo value = store.get(key);

				if (value == null) {
					logger.error(NULL_VALUE_ERROR_MSG_FORMAT, store.getClass().getSimpleName(), key);
				} else if (currentEpochtime - value.getLastUsageEpochtime() > maxSecDiffBeforeCleaningCache) {
					keysToClean.add(key);
				}
			}

			keysToClean.forEach(store::delete);
		} finally {
			if (iterator != null) {
				iterator.close();
			}

			// Update the last time models were cleaned from the cache, even if the iterator failed
			lastCleaningCacheEpochtime = currentEpochtime;
		}
	}

	@SuppressWarnings("unchecked")
	private KeyValueStore<String, ModelsCacheInfo> getStore() {
		String storeName = getStoreName();
		return (KeyValueStore<String, ModelsCacheInfo>)samzaContainerService.getStore(storeName);
	}

	private String getStoreName() {
		return ConfigUtils.getConfigString(samzaContainerService.getConfig(), STORE_NAME_PROPERTY);
	}

	@Override
	public void close() {}

	/**
	 * TODO: Following functionality should be implemented in a dedicated service.
	 * This function decides which type of model cache manager should be created.
	 * If the model builder builds category rarity models, the model cache manager type should be discrete;
	 * This means it can update restored models with missing features. If the model builder builds continuous
	 * histogram models or time models, the model cache manager should be a standard one; It doesn't need to
	 * update restored models with missing features, because all features should be present.
	 */
	private static boolean isDiscreteModelConf(ModelConf modelConf) {
		String factoryName = modelConf.getModelBuilderConf().getFactoryName();
		return factoryName.equals(CategoryRarityModelBuilderConf.CATEGORY_RARITY_MODEL_BUILDER);
	}

	public void loadCacheManagers() {
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
