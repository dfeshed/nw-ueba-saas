package fortscale.streaming.service.model;

import fortscale.common.feature.Feature;
import fortscale.ml.model.Model;
import fortscale.ml.model.cache.ModelCacheInfo;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.streaming.ExtendedSamzaTaskContext;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

import static fortscale.streaming.ConfigUtils.getConfigString;

@Configurable(preConstruction=true)
public class ModelsCacheServiceSamza implements ModelsCacheService {
    private static final String STORE_NAME_PROPERTY = "fortscale.model.cache.managers.store.name";

    private KeyValueStore<String, List<ModelCacheInfo>> modelCacheStore;


    public ModelsCacheServiceSamza(ExtendedSamzaTaskContext context) {
        Assert.notNull(context);
        Config config = context.getConfig();
        String storeName = getConfigString(config, STORE_NAME_PROPERTY);
        modelCacheStore = (KeyValueStore<String, List<ModelCacheInfo>>)context.getStore(storeName);
        Assert.notNull(modelCacheStore);
        //TODO
    }


    @Override
    public Model getModel(Feature feature, Map<String, Feature> contextFieldNamesToValuesMap, String modelName, long eventEpochTime) {
        return null; //TODO
    }

    @Override
    public void window() {
        //TODO
    }

    @Override
    public void close() {
        //TODO
    }
}
