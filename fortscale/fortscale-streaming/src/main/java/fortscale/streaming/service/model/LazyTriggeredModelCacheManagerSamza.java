package fortscale.streaming.service.model;

import fortscale.ml.model.ModelConf;
import fortscale.ml.model.cache.ModelsCacheInfo;
import fortscale.ml.model.store.ModelDAO;

public class LazyTriggeredModelCacheManagerSamza extends ModelCacheManagerSamza {

	public LazyTriggeredModelCacheManagerSamza(String levelDbStoreName, ModelConf modelConf) {
		super(levelDbStoreName, modelConf);
	}

	@Override
	protected ModelDAO getModelDaoWithLatestEndTimeLte(String contextId, long eventEpochtime) {
		ModelsCacheInfo modelsCacheInfo = getStore().get(getStoreKey(modelConf, contextId));

		ModelDAO modelDao = null;

		// loads model by demand if none exists in cache
		if (modelsCacheInfo == null) {
			modelsCacheInfo = loadModelsCacheInfo(contextId);
			modelDao = modelsCacheInfo.getModelDaoWithLatestEndTimeLte(eventEpochtime);
		}

		// If there is no suitable model in the cache or in the DB, simply return the latest one
		return modelDao == null ? modelsCacheInfo.getModelDaoWithLatestEndTime() : modelDao;
	}

}
