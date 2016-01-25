package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.Scorer;
import fortscale.utils.factory.Factory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;


public class ScorersFactoryService implements Factory {



    private Map<String, Factory<Scorer>> factoryMap = new HashMap<>();


    public void register(String factoryName, Factory factory) {
        Assert.hasText(factoryName);
        Assert.notNull(factory);
        factoryMap.put(factoryName, factory);


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
