package fortscale.ml.model.cache;

import fortscale.common.feature.Feature;
import fortscale.ml.model.Model;

public interface ModelCacheManager {
    Model getModel(Feature feature, String context, long eventEpochTimeInSeconds);
}
