package fortscale.streaming.service.model;

import fortscale.common.feature.Feature;
import fortscale.ml.model.Model;
import fortscale.ml.model.cache.ModelCacheManager;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.cache.ModelCacheInfo;
import fortscale.streaming.ExtendedSamzaTaskContext;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.springframework.util.Assert;

import java.util.List;

import static fortscale.streaming.ConfigUtils.getConfigString;


public class AbstractModelCacheManagerSamza implements ModelCacheManager {

    private static final String STORE_NAME_PROPERTY = "fortscale.model.cache.managers.store.name";

    private KeyValueStore<String, List<ModelCacheInfo>> modelCacheStore;
    private ModelConf modelConf;



    public AbstractModelCacheManagerSamza(ExtendedSamzaTaskContext context, ModelConf modelConf) {
        Assert.notNull(context);
        Assert.notNull(modelConf);
        Config config = context.getConfig();
        String storeName = getConfigString(config, STORE_NAME_PROPERTY);
        modelCacheStore = (KeyValueStore<String, List<ModelCacheInfo>>)context.getStore(storeName);
        Assert.notNull(modelCacheStore);
        this.modelConf = modelConf;
        //TODO
    }

    @Override
    public Model getModel(Feature feature, String context, long eventEpochTimeInSeconds) {
        return null;
        //TODO
    }
}
