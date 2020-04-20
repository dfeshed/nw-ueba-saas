package fortscale.ml.model.cache;

import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.cache.metrics.ModelCacheMetricsContainer;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.factory.FactoryService;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * manages caches initiation and interaction for models of contexts
 * Created by barak_schuster on 6/6/17.
 */
public class ModelCacheServiceInMemory implements ModelsCacheService {
    private final ModelCacheMetricsContainer modelCacheMetricsContainer;
    private Map<String, ModelCacheManager> modelCacheManagers;
    private ModelConfService modelConfService;
    private ModelStore modelStore;
    private FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;
    private Duration maxDiffBetweenCachedModelAndEvent;
    private int cacheSize;
    private int numOfModelsPerContextId;

    /**
     * @param modelConfService
     * @param modelStore
     * @param dataRetrieverFactoryService
     * @param maxDiffBetweenCachedModelAndEvent see {@link ModelCacheManagerInMemory}
     * @param cacheSize                            the size of each of the model caches
     * @param modelCacheMetricsContainer
     */
    public ModelCacheServiceInMemory(ModelConfService modelConfService, ModelStore modelStore,
                                     FactoryService<AbstractDataRetriever> dataRetrieverFactoryService,
                                     Duration maxDiffBetweenCachedModelAndEvent, int cacheSize, int numOfModelsPerContextId, ModelCacheMetricsContainer modelCacheMetricsContainer) {
        this.modelConfService = modelConfService;
        this.modelStore = modelStore;
        this.dataRetrieverFactoryService = dataRetrieverFactoryService;
        this.maxDiffBetweenCachedModelAndEvent = maxDiffBetweenCachedModelAndEvent;
        this.cacheSize = cacheSize;
        this.modelCacheManagers = new HashMap<>();
        this.numOfModelsPerContextId = numOfModelsPerContextId;
        this.modelCacheMetricsContainer = modelCacheMetricsContainer;
    }

    @Override
    public Model getLatestModelBeforeEventTime(String modelConfName, Map<String, String> context, Instant eventTime) {
        ModelCacheManager modelCacheManager = createModelCacheManagerIfNotExist(modelConfName);

        return modelCacheManager.getLatestModelBeforeEventTime(context, eventTime);
    }

    @Override
    public Model getLatestModelBeforeEventTime(String modelConfName, String contextId, Instant eventTime) {
        ModelCacheManager modelCacheManager = createModelCacheManagerIfNotExist(modelConfName);

        return modelCacheManager.getLatestModelBeforeEventTime(contextId, eventTime);
    }

    @Override
    public List<ModelDAO> getModelDAOsSortedByEndTimeDesc(String modelConfName, String contextId, Instant eventTime) {
        ModelCacheManager modelCacheManager = createModelCacheManagerIfNotExist(modelConfName);

        return modelCacheManager.getModelDAOsSortedByEndTimeDesc(contextId, eventTime);
    }

    private ModelCacheManager createModelCacheManagerIfNotExist(String modelConfName){
        ModelCacheManager modelCacheManager = modelCacheManagers.get(modelConfName);
        if (modelCacheManager == null) {
            ModelConf modelConf = modelConfService.getModelConf(modelConfName);
            modelCacheManager = new ModelCacheManagerInMemory(modelStore, modelConf, dataRetrieverFactoryService.getProduct(modelConf.getDataRetrieverConf()), maxDiffBetweenCachedModelAndEvent, cacheSize, numOfModelsPerContextId,modelCacheMetricsContainer);
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

    @Override
    public void resetCache() {
        modelCacheManagers = new HashMap<>();
    }
}
