package fortscale.streaming.service.model;

import fortscale.ml.model.ModelConf;
import fortscale.ml.model.cache.ModelsCacheInfo;
import fortscale.ml.model.message.ModelBuildingStatusMessage;
import fortscale.ml.model.store.ModelDAO;
import fortscale.utils.logging.Logger;

/**
 * models are loaded to cache only if requested by the method {@link this#getModelDaoWithLatestEndTimeLte(String, long)}
 * - meaning, models are loaded only for contexts that requires scoring and NOT for all the contexts with existing models.
 * models are cleaned from cache at {@link ModelsCacheServiceSamza#deleteFromCache(String, String)}
 * - when model update notification is received at {@link fortscale.streaming.service.scorer.ScoringTaskService#refreshModelCache(ModelBuildingStatusMessage)} }
 */
public class LazyTriggeredModelCacheManagerSamza extends ModelCacheManagerSamza {

	private static final Logger logger = Logger.getLogger(LazyTriggeredModelCacheManagerSamza.class);

	public LazyTriggeredModelCacheManagerSamza(String levelDbStoreName, ModelConf modelConf) {
		super(levelDbStoreName, modelConf);
	}

	@Override
	protected ModelDAO getModelDaoWithLatestEndTimeLte(String contextId, long eventEpochtime) {
		ModelsCacheInfo modelsCacheInfo = getStore().get(getStoreKey(modelConf, contextId));

		ModelDAO modelDao = null;

		// loads model by demand if none exists in cache
		if (modelsCacheInfo == null) {
			metrics.loadModelFromStore++;
			// todo change log to debug
			logger.info("model={} for context={} does not exists in cache... loading from store",modelConf.getName(),contextId);
			modelsCacheInfo = loadModelsCacheInfo(contextId);
			modelDao = modelsCacheInfo.getModelDaoWithLatestEndTimeLte(eventEpochtime);
			logger.info("loaded modelDao={} fort context={} eventTime={}",modelDao,contextId,eventEpochtime);
		}

		// If there is no suitable model in the cache or in the DB, simply return the latest one
		return modelDao == null ? modelsCacheInfo.getModelDaoWithLatestEndTime() : modelDao;
	}

}
