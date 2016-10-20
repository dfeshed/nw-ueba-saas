package fortscale.streaming.service.model;

import fortscale.common.feature.Feature;
import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.cache.ModelCacheManager;
import fortscale.ml.model.cache.ModelsCacheInfo;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStore;
import fortscale.streaming.common.SamzaContainerService;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.samza.storage.kv.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Configurable(preConstruction = true)
public class ModelCacheManagerSamza implements ModelCacheManager {
	private static final Logger logger = Logger.getLogger(ModelCacheManagerSamza.class);

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

	@Autowired
	private StatsService statsService;

	protected ModelConf modelConf;
	protected AbstractDataRetriever retriever;
	String levelDbStoreName;
	protected ModelCacheManagerMetrics metrics;

	public ModelCacheManagerSamza(String levelDbStoreName, ModelConf modelConf) {
		Assert.notNull(modelConf);
		this.levelDbStoreName = levelDbStoreName;
		this.modelConf = modelConf;
		retriever = dataRetrieverFactoryService.getProduct(modelConf.getDataRetrieverConf());
		Assert.notNull(retriever);
	}

	public ModelCacheManagerMetrics getMetrics()
	{
		if(metrics==null)
		{
			metrics = new ModelCacheManagerMetrics(statsService,levelDbStoreName,modelConf.getName());
		}
		return metrics;
	}

	protected KeyValueStore<String, ModelsCacheInfo> getStore() {
		KeyValueStore<String, ModelsCacheInfo> store = (KeyValueStore<String, ModelsCacheInfo>)samzaContainerService.getStore(levelDbStoreName);
		Assert.notNull(store);

		return store;
	}

	@Override
	public Model getModel(Feature feature, Map<String, String> context, long eventEpochtime) {
		getMetrics().getModel++;
		ModelDAO returned = getModelDao(feature, context, eventEpochtime);
		return returned != null ? returned.getModel() : null;
	}

	@Override
	public void deleteFromCache(String modelConfName, String contextId) {
		getMetrics().deleteFromCache++;
		String storeKey = getStoreKey(modelConfName, contextId);
		logger.info("deleting key={} from store={}", storeKey, levelDbStoreName);
		getStore().delete(storeKey);
	}

	protected ModelDAO getModelDao(Feature feature, Map<String, String> context, long eventEpochtime) {
		String contextId = getContextId(context);
		ModelDAO modelDao = getModelDaoWithLatestEndTimeLte(contextId, eventEpochtime);

		if (modelDao == null)
		{
			getMetrics().modelNotFoundForContextId++;
			return null;
		}
		if (isModelEndTimeExpired(modelDao.getEndTime(), eventEpochtime)) {
			return null;
		} else {
			setLastUsageEpochtime(contextId);
			return modelDao;
		}
	}

	protected ModelsCacheInfo getModelsCacheInfo(String contextId) {
		ModelsCacheInfo modelsCacheInfo = getStore().get(getStoreKey(modelConf, contextId));
		if(modelsCacheInfo == null)
		{
			getMetrics().modelNotFoundInKeyValueStore++;
			modelsCacheInfo = loadModelsCacheInfo(contextId);
		}
		return modelsCacheInfo;
	}

	protected void setModelsCacheInfo(String contextId, ModelsCacheInfo modelsCacheInfo) {
		getStore().put(getStoreKey(modelConf, contextId), modelsCacheInfo);
	}

	private String getContextId(Map<String, String> contextFieldsToValueMap) {
		Assert.notNull(contextFieldsToValueMap);
		if (contextFieldsToValueMap.isEmpty()) {
			return null;
		}
		return retriever.getContextId(contextFieldsToValueMap);
	}

	protected ModelDAO getModelDaoWithLatestEndTimeLte(String contextId, long eventEpochtime) {
		ModelsCacheInfo modelsCacheInfo = getModelsCacheInfo(contextId);
		boolean doesModelExist = modelsCacheInfo.notEmptyValidation();
		if (!doesModelExist)
		{
			getMetrics().modelDoesNotExist++;
		}
		ModelDAO result = modelsCacheInfo.getModelDaoWithLatestEndTimeLte(eventEpochtime);

		if (result==null && doesModelExist)
		{
			getMetrics().modelNotFoundInTimePeriod++;
		}

		// If there is no suitable model in the cache, simply return the latest one
		return result == null ? modelsCacheInfo.getModelDaoWithLatestEndTime() : result;
	}

	protected static String getStoreKey(ModelConf modelConf, String contextId) {
		String modelConfName = modelConf.getName();
		if (contextId != null) {
			return getStoreKey(contextId, modelConfName);
		} else {
			return modelConfName;
		}
	}

	private static String getStoreKey(String contextId, String modelConfName) {
		return StringUtils.join(modelConfName, STORE_KEY_SEPARATOR, contextId);
	}

	protected ModelsCacheInfo loadModelsCacheInfo(String contextId) {
		List<ModelDAO> modelDaos = modelStore.getModelDaos(modelConf, contextId);
		ModelsCacheInfo modelsCacheInfo = new ModelsCacheInfo();
		modelDaos.forEach(modelsCacheInfo::setModelDao);
		setModelsCacheInfo(contextId, modelsCacheInfo);
		return modelsCacheInfo;
	}

	private boolean isModelEndTimeExpired(Date modelEndTime, long eventEpochtime) {
		if (eventEpochtime - TimestampUtils.convertToSeconds(modelEndTime) > maxSecDiffBeforeExpired) {
			getMetrics().modelEndTimeExpired++;
			return true;
		}
		else
		{
			return false;
		}
	}

	private void setLastUsageEpochtime(String contextId) {
		getMetrics().lastUsageTimeSet++;
		ModelsCacheInfo modelsCacheInfo = getModelsCacheInfo(contextId);
		long currentEpochtime = TimestampUtils.convertToSeconds(System.currentTimeMillis());

		if (currentEpochtime - modelsCacheInfo.getLastUsageEpochtime() >= waitSecBetweenLastUsageEpochtimeUpdates) {
			modelsCacheInfo.setLastUsageEpochtime(currentEpochtime);
			getStore().put(getStoreKey(modelConf, contextId), modelsCacheInfo);
		}
	}
}
