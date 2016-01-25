package fortscale.streaming.service.model;

import fortscale.common.feature.Feature;
import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.cache.ModelCacheManager;
import fortscale.ml.model.cache.ModelsCacheInfo;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStore;
import fortscale.streaming.ConfigUtils;
import fortscale.streaming.common.SamzaContainerService;
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

	@Value("${fortscale.model.max.sec.diff.before.expired}")
	private long maxSecDiffBeforeExpired;
	@Value("${fortscale.model.wait.sec.between.last.usage.epochtime.updates}")
	private long waitSecBetweenLastUsageEpochtimeUpdates;

	@Autowired
	private FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;
	@Autowired
	private ModelStore modelStore;

	@Autowired
	private SamzaContainerService samzaContainerService;



	protected ModelConf modelConf;
	protected AbstractDataRetriever retriever;
	String levelDbStoreName;

	public ModelCacheManagerSamza(String levelDbStoreName, ModelConf modelConf) {
		Assert.notNull(modelConf);
		this.levelDbStoreName = levelDbStoreName;
		this.modelConf = modelConf;
		retriever = dataRetrieverFactoryService.getProduct(modelConf.getDataRetrieverConf());
		Assert.notNull(retriever);
	}

	protected KeyValueStore<String, ModelsCacheInfo> getStore() {
		KeyValueStore<String, ModelsCacheInfo> store = (KeyValueStore<String, ModelsCacheInfo>)samzaContainerService.getStore(levelDbStoreName);
		Assert.notNull(store);

		return store;
	}

	@Override
	public Model getModel(Feature feature, Map<String, Feature> context, long eventEpochtime) {
		ModelDAO returned = getModelDao(feature, context, eventEpochtime);
		return returned != null ? returned.getModel() : null;
	}

	protected ModelDAO getModelDao(Feature feature, Map<String, Feature> context, long eventEpochtime) {
		String contextId = getContextId(context);
		ModelDAO modelDao = getModelDaoWithLatestEndTimeLte(contextId, eventEpochtime);

		if (modelDao == null || isModelEndTimeExpired(modelDao.getEndTime(), eventEpochtime)) {
			return null;
		} else {
			setLastUsageEpochtime(contextId);
			return modelDao;
		}
	}

	protected ModelsCacheInfo getModelsCacheInfo(String contextId) {
		ModelsCacheInfo modelsCacheInfo = getStore().get(getStoreKey(modelConf, contextId));
		return modelsCacheInfo == null ? loadModelsCacheInfo(contextId) : modelsCacheInfo;
	}

	protected void setModelsCacheInfo(String contextId, ModelsCacheInfo modelsCacheInfo) {
		getStore().put(getStoreKey(modelConf, contextId), modelsCacheInfo);
	}

	private String getContextId(Map<String, Feature> fieldToFeature) {
		Assert.notEmpty(fieldToFeature);
		Map<String, String> fieldToFeatureValue = new HashMap<>();

		for (Map.Entry<String, Feature> entry : fieldToFeature.entrySet()) {
			fieldToFeatureValue.put(entry.getKey(), entry.getValue().getValue().toString());
		}

		return retriever.getContextId(fieldToFeatureValue);
	}

	protected ModelDAO getModelDaoWithLatestEndTimeLte(String contextId, long eventEpochtime) {
		ModelsCacheInfo modelsCacheInfo = getModelsCacheInfo(contextId);
		return modelsCacheInfo.getModelDaoWithLatestEndTimeLte(eventEpochtime);
	}

	protected static String getStoreKey(ModelConf modelConf, String contextId) {
		return StringUtils.join(modelConf.getName(), STORE_KEY_SEPARATOR, contextId);
	}

	protected ModelsCacheInfo loadModelsCacheInfo(String contextId) {
		List<ModelDAO> modelDaos = modelStore.getModelDaos(modelConf, contextId);
		ModelsCacheInfo modelsCacheInfo = new ModelsCacheInfo();
		modelDaos.forEach(modelsCacheInfo::setModelDao);
		setModelsCacheInfo(contextId, modelsCacheInfo);
		return modelsCacheInfo;
	}

	private boolean isModelEndTimeExpired(Date modelEndTime, long eventEpochtime) {
		return eventEpochtime - TimestampUtils.convertToSeconds(modelEndTime) > maxSecDiffBeforeExpired;
	}

	private void setLastUsageEpochtime(String contextId) {
		ModelsCacheInfo modelsCacheInfo = getModelsCacheInfo(contextId);
		long currentEpochtime = TimestampUtils.convertToSeconds(System.currentTimeMillis());

		if (currentEpochtime - modelsCacheInfo.getLastUsageEpochtime() >= waitSecBetweenLastUsageEpochtimeUpdates) {
			modelsCacheInfo.setLastUsageEpochtime(currentEpochtime);
			getStore().put(getStoreKey(modelConf, contextId), modelsCacheInfo);
		}
	}
}
