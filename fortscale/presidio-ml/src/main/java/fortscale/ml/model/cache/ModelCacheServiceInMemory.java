package fortscale.ml.model.cache;

import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.factory.FactoryService;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * manages caches initiation and interaction for models of contexts
 * Created by barak_schuster on 6/6/17.
 */
public class ModelCacheServiceInMemory implements ModelsCacheService {
    private Map<String, ModelCacheManager> modelCacheManagers;
    private ModelConfService modelConfService;
    private ModelStore modelStore;
    private FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;
    private Duration maxDiffBetweenCachedModelAndEvent;
    private int cacheSize;

    /**
     * @param modelConfService
     * @param modelStore
     * @param dataRetrieverFactoryService
     * @param maxDiffBetweenCachedModelAndEvent see {@link ModelCacheManagerInMemory}
     * @param cacheSize                            the size of each of the model caches
     */
    public ModelCacheServiceInMemory(ModelConfService modelConfService, ModelStore modelStore,
                                     FactoryService<AbstractDataRetriever> dataRetrieverFactoryService,
                                     Duration maxDiffBetweenCachedModelAndEvent, int cacheSize) {
        this.modelConfService = modelConfService;
        this.modelStore = modelStore;
        this.dataRetrieverFactoryService = dataRetrieverFactoryService;
        this.maxDiffBetweenCachedModelAndEvent = maxDiffBetweenCachedModelAndEvent;
        this.cacheSize = cacheSize;
        this.modelCacheManagers = new HashMap<>();
    }

    @Override
    public Model getModel(String modelConfName, Map<String, String> context, Instant eventTime) {
        ModelCacheManager modelCacheManager = createModelCacheManagerIfNotExist(modelConfName);

        return modelCacheManager.getModel(context, eventTime);
    }

    @Override
    public Model getModel(String modelConfName, String contextId, Instant eventTime) {
        ModelCacheManager modelCacheManager = createModelCacheManagerIfNotExist(modelConfName);

        return modelCacheManager.getModel(contextId, eventTime);
    }

    private ModelCacheManager createModelCacheManagerIfNotExist(String modelConfName){
        ModelCacheManager modelCacheManager = modelCacheManagers.get(modelConfName);
        if (modelCacheManager == null) {
            ModelConf modelConf = modelConfService.getModelConf(modelConfName);
            modelCacheManager = new ModelCacheManagerInMemory(modelStore, modelConf, dataRetrieverFactoryService.getProduct(modelConf.getDataRetrieverConf()), maxDiffBetweenCachedModelAndEvent, cacheSize);
            modelCacheManagers.put(modelConfName, modelCacheManager);
        }

        return modelCacheManager;
    }

    @Override
    public void deleteFromCache(String modelConfName, String contextId) {
        ModelCacheManager modelCacheManager = modelCacheManagers.get(modelConfName);
        if (modelCacheManager != null) {
            modelCacheManager.deleteFromCache(modelConfName, contextId);
        }
    }
}
