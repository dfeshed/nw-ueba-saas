package fortscale.streaming.service.model;

import fortscale.ml.model.ModelConf;
import fortscale.ml.model.cache.ModelsCacheInfo;
import fortscale.ml.model.store.ModelDAO;
import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public class LazyModelCacheManagerSamza extends ModelCacheManagerSamza {
	@Value("${fortscale.model.wait.sec.between.loads}")
	private long waitSecBetweenLoads;
	@Value("${fortscale.model.max.sec.diff.before.outdated}")
	private long maxSecDiffBeforeOutdated;

	public LazyModelCacheManagerSamza(String levelDbStoreName, ModelConf modelConf) {
		super(levelDbStoreName, modelConf);
	}

	@Override
	protected ModelDAO getModelDaoWithLatestEndTimeLte(String contextId, long eventEpochtime) {
		ModelsCacheInfo modelsCacheInfo = getStore().get(getStoreKey(modelConf, contextId));

		ModelDAO modelDao = null;
		boolean isLoadModel = false;
		if (modelsCacheInfo != null) {
			modelDao = modelsCacheInfo.getModelDaoWithLatestEndTimeLte(eventEpochtime);
			if ((modelDao == null || isModelEndTimeOutdated(modelDao.getEndTime(), eventEpochtime)) && canLoadModelsCacheInfo(modelsCacheInfo)) {
				isLoadModel = true;
			}
		} else {
			isLoadModel = true;
		}

		if (isLoadModel) {
			modelsCacheInfo = loadModelsCacheInfo(contextId);
			modelDao = modelsCacheInfo.getModelDaoWithLatestEndTimeLte(eventEpochtime);
		}

		// If there is no suitable model in the cache or in the DB, simply return the latest one
		return modelDao == null ? modelsCacheInfo.getModelDaoWithLatestEndTime() : modelDao;
	}

	private boolean canLoadModelsCacheInfo(ModelsCacheInfo modelsCacheInfo) {
		long currentEpochtime = TimestampUtils.convertToSeconds(System.currentTimeMillis());
		if(currentEpochtime - modelsCacheInfo.getLastLoadEpochtime() >= waitSecBetweenLoads)
		{
			return true;
		}
		getMetrics().lazyCacheCanNotLoadModelsCacheInfo++;
		return false;
	}

	private boolean isModelEndTimeOutdated(Date modelEndTime, long eventEpochtime) {
		if(eventEpochtime - TimestampUtils.convertToSeconds(modelEndTime) > maxSecDiffBeforeOutdated)
		{
			getMetrics().lazyCacheModelEndTimeOutDated++;
			return true;
		}
		return false;
	}
}
