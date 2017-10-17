package fortscale.ml.model.cache;

import fortscale.ml.model.Model;

import java.time.Instant;
import java.util.Map;

public interface ModelsCacheService {

	Model getModel(String modelConfName, Map<String, String> context, Instant eventTime);
	Model getModel(String modelConfName, String contextId, Instant eventTime);
	/**
	 * refresh model (lazy cache) by deleting the model from the relevant cache manager
	 *
	 * @param modelConfName modelConf to delete
	 * @param contextId     i.e. username
	 */
	void deleteFromCache(String modelConfName, String contextId);

	/**
	 * Deleting all the models from the relevant cache manager
	 */
	void resetCache();
}
