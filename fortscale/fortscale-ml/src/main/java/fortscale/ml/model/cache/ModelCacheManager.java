package fortscale.ml.model.cache;

import fortscale.common.feature.Feature;
import fortscale.ml.model.Model;
import java.util.Map;

public interface ModelCacheManager {
	Model getModel(Feature feature, Map<String, String> context, long eventEpochtime);
}
