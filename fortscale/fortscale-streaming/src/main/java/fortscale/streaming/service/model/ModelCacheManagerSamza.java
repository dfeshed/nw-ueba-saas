package fortscale.streaming.service.model;

import fortscale.common.feature.Feature;
import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.cache.ModelCacheManager;
import fortscale.ml.model.cache.ModelsCacheInfo;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.samza.storage.kv.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configurable(preConstruction = true)
public class ModelCacheManagerSamza implements ModelCacheManager {
	private static final String STORE_KEY_SEPARATOR = ".";

	@Value("${fortscale.model.wait.sec.between.loads}")
	private long waitSecBetweenLoads;
	@Value("${fortscale.model.max.sec.diff.before.outdated}")
	private long maxSecDiffBeforeOutdated;
	@Value("${fortscale.model.max.sec.diff.before.expired}")
	private long maxSecDiffBeforeExpired;
	@Value("${fortscale.model.wait.sec.between.last.usage.epochtime.updates}")
	private long waitSecBetweenLastUsageEpochtimeUpdates;

	@Autowired
	private FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;
	@Autowired
	private ModelStore modelStore;

	private KeyValueStore<String, ModelsCacheInfo> store;
	private ModelConf modelConf;
	protected AbstractDataRetriever retriever;

	public ModelCacheManagerSamza(KeyValueStore<String, ModelsCacheInfo> store, ModelConf modelConf) {
		Assert.notNull(store);
		Assert.notNull(modelConf);
		this.store = store;
		this.modelConf = modelConf;
		retriever = dataRetrieverFactoryService.getProduct(modelConf.getDataRetrieverConf());
		Assert.notNull(retriever);
	}

	@Override
	public Model getModel(Feature feature, Map<String, Feature> context, long eventEpochtime) {
		String contextId = getContextId(context);
		ModelDAO modelDao = getModelDaoWithLatestEndTimeLt(contextId, eventEpochtime);

		if (modelDao == null || isModelEndTimeExpired(modelDao.getEndTime(), eventEpochtime)) {
			return null;
		} else {
			setLastUsageEpochtime(contextId);
			updateModelDao(modelDao, feature);
			return modelDao.getModel();
		}
	}

	protected ModelsCacheInfo getModelsCacheInfo(String contextId) {
		ModelsCacheInfo modelsCacheInfo = store.get(getStoreKey(contextId));
		return modelsCacheInfo == null ? loadModelsCacheInfo(contextId) : modelsCacheInfo;
	}

	protected void setModelsCacheInfo(String contextId, ModelsCacheInfo modelsCacheInfo) {
		store.put(getStoreKey(contextId), modelsCacheInfo);
	}

	protected void updateModelDao(ModelDAO modelDao, Feature feature) {
		// No update needed
	}

	private String getContextId(Map<String, Feature> stringToFeature) {
		Assert.notEmpty(stringToFeature);
		Map<String, String> stringToString = new HashMap<>();

		for (Map.Entry<String, Feature> entry : stringToFeature.entrySet()) {
			stringToString.put(entry.getKey(), entry.getValue().getValue().toString());
		}

		return retriever.getContextId(stringToString);
	}

	private ModelDAO getModelDaoWithLatestEndTimeLt(String contextId, long eventEpochtime) {
		ModelsCacheInfo modelsCacheInfo = getModelsCacheInfo(contextId);
		ModelDAO modelDao = modelsCacheInfo.getModelDaoWithLatestEndTimeLt(eventEpochtime);

		if (modelDao == null && canLoadModelsCacheInfo(modelsCacheInfo)) {
			modelsCacheInfo = loadModelsCacheInfo(contextId);
			modelDao = modelsCacheInfo.getModelDaoWithLatestEndTimeLt(eventEpochtime);
		}

		if (modelDao != null &&
				isModelEndTimeOutdated(modelDao.getEndTime(), eventEpochtime) &&
				canLoadModelsCacheInfo(modelsCacheInfo)) {
			modelsCacheInfo = loadModelsCacheInfo(contextId);
			modelDao = modelsCacheInfo.getModelDaoWithLatestEndTimeLt(eventEpochtime);
		}

		return modelDao;
	}

	private String getStoreKey(String contextId) {
		return StringUtils.join(modelConf.getName(), STORE_KEY_SEPARATOR, contextId);
	}

	private ModelsCacheInfo loadModelsCacheInfo(String contextId) {
		List<ModelDAO> modelDaos = modelStore.getModelDaos(modelConf, contextId);
		ModelsCacheInfo modelsCacheInfo = new ModelsCacheInfo();
		modelDaos.forEach(modelsCacheInfo::setModelDao);
		setModelsCacheInfo(contextId, modelsCacheInfo);
		return modelsCacheInfo;
	}

	private boolean canLoadModelsCacheInfo(ModelsCacheInfo modelsCacheInfo) {
		long currentEpochtime = TimestampUtils.convertToSeconds(System.currentTimeMillis());
		return currentEpochtime - modelsCacheInfo.getLastLoadEpochtime() >= waitSecBetweenLoads;
	}

	private boolean isModelEndTimeOutdated(Date modelEndTime, long eventEpochtime) {
		return eventEpochtime - TimestampUtils.convertToSeconds(modelEndTime) > maxSecDiffBeforeOutdated;
	}

	private boolean isModelEndTimeExpired(Date modelEndTime, long eventEpochtime) {
		return eventEpochtime - TimestampUtils.convertToSeconds(modelEndTime) > maxSecDiffBeforeExpired;
	}

	private void setLastUsageEpochtime(String contextId) {
		ModelsCacheInfo modelsCacheInfo = getModelsCacheInfo(contextId);
		long currentEpochtime = TimestampUtils.convertToSeconds(System.currentTimeMillis());

		if (currentEpochtime - modelsCacheInfo.getLastUsageEpochtime() >= waitSecBetweenLastUsageEpochtimeUpdates) {
			modelsCacheInfo.setLastUsageEpochtime(currentEpochtime);
			store.put(getStoreKey(contextId), modelsCacheInfo);
		}
	}
}
