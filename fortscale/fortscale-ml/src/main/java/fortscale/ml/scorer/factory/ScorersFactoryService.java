package fortscale.ml.scorer.factory;

import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.scorer.Scorer;
import fortscale.utils.factory.Factory;
import fortscale.utils.factory.FactoryConfig;
import fortscale.utils.factory.FactoryService;

import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ScorersFactoryService implements ModelScorerFactory {

    ModelsCacheService modelsCacheService;
    private List<ModelScorerFactory> modelScorerFactoryList = new ArrayList<>();
    private Map<String, Factory<Scorer>> factoryMap = new HashMap<>();


    public void register(String factoryName, Factory factory) {
        Assert.hasText(factoryName);
        Assert.notNull(factory);
        factoryMap.put(factoryName, factory);

        if(factory instanceof ModelScorerFactory) {
            ModelScorerFactory modelScorerFactory = (ModelScorerFactory) factory;
            modelScorerFactoryList.add(modelScorerFactory);
            if (modelsCacheService != null) {
                modelScorerFactory.setModelCacheService(modelsCacheService);
            }
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

    public Factory<Scorer> getFactory(String factoryName) {
        return factoryMap.get(factoryName);
    }

    public Scorer getProduct(FactoryConfig factoryConfig) {
        Assert.notNull(factoryConfig);
        Factory<Scorer> factory = getFactory(factoryConfig.getFactoryName());
        return factory == null ? null : factory.getProduct(factoryConfig);
    }
}
