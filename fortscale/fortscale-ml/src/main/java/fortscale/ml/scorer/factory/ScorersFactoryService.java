package fortscale.ml.scorer.factory;

import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.scorer.Scorer;
import fortscale.utils.factory.FactoryService;

import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


public class ScorersFactoryService extends FactoryService<Scorer> implements ModelScorerFactory {

    ModelsCacheService modelsCacheService;
    List<ModelScorerFactory> modelScorerFactoryList = new ArrayList<>();

    public void register(String factoryName, ModelScorerFactory factory) {
        super.register(factoryName, factory);
        modelScorerFactoryList.add(factory);
        if(modelsCacheService!=null) {
            factory.setModelCacheService(modelsCacheService);
        }
    }

    @Override
    public void setModelCacheService(@NotNull ModelsCacheService modelCacheService) {
        Assert.notNull(modelCacheService);
        this.modelsCacheService = modelCacheService;
        for(ModelScorerFactory modelScorerFactory:modelScorerFactoryList) {
            modelScorerFactory.setModelCacheService(modelCacheService);
        }
    }
}
