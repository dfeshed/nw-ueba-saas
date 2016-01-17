package fortscale.ml.scorer.factory;

import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.utils.factory.Factory;


public interface ModelScorerFactory extends Factory {
    void setModelCacheService(ModelsCacheService modelsCacheService);
}
