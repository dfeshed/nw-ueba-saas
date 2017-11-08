package fortscale.ml.model.cache;

import fortscale.ml.model.Model;
import fortscale.ml.model.store.ModelDAO;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface ModelCacheManager {
	Model getLatestModelBeforeEventTime(Map<String, String> context, Instant eventTime);
	Model getLatestModelBeforeEventTime(String contextId, Instant eventTime);
	List<ModelDAO> getModelDAOsSortedByEndTimeDesc(String contextId, Instant eventTime);

	/**
	 * deletes model from rocksdb cache by params
	 * @param modelConfName
	 * @param contextId
     */
	void deleteFromCache(String modelConfName, String contextId);

}
