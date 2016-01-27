package fortscale.ml.scorer.factory;

import fortscale.ml.model.cache.ModelsCacheService;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;


public abstract class AbstractModelScorerFactory implements ModelScorerFactory {

    protected ModelsCacheService modelsCacheService;

    @Override
    public void setModelCacheService(@NotNull ModelsCacheService modelsCacheService) {
        Assert.notNull(modelsCacheService);
        this.modelsCacheService = modelsCacheService;
    }
}
