package fortscale.ml.model.cache;

import fortscale.ml.model.Model;

import java.time.Instant;
import java.util.Map;

public interface ModelCacheManager {
	Model getModel(Map<String, String> context, Instant eventTime);
	public Model getModel(String contextId, Instant eventTime);

	/**
	 * deletes model from rocksdb cache by params
	 * @param modelConfName
	 * @param contextId
     */
	void deleteFromCache(String modelConfName, String contextId);

	/**
	 * Reset cache
	 */
	void resetCache();
}
