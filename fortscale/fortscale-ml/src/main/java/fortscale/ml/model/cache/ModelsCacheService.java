package fortscale.ml.model.cache;

import fortscale.common.feature.Feature;
import fortscale.ml.model.Model;
import java.util.Map;

public interface ModelsCacheService {
	Model getModel(Feature feature, String modelConfName, Map<String, String> context, long eventEpochtime);
	void window();
	void close();
}
