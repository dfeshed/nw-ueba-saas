package fortscale.ml.model;

import fortscale.common.feature.Feature;

public interface ModelCacheManager {
    Model getModel(Feature feature, String context, long eventEpochTimeInSeconds);
}
