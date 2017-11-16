package fortscale.ml.model.cache;

import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.store.EmptyModelDao;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.logging.Logger;
import org.apache.commons.collections.map.LRUMap;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * models cache that is in memory.
 * using LRUMap
 * Created by barak_schuster on 2/1/17.
 */
public class ModelCacheManagerInMemory implements ModelCacheManager {
    private static final Logger logger = Logger.getLogger(ModelCacheManagerInMemory.class);

    private ModelStore modelStore;
    private ModelConf modelConf;
    private AbstractDataRetriever retriever;
    private Duration maxDiffBetweenCachedModelAndEvent;
    private LRUMap lruModelsMap;
    private int numOfModelsPerContextId;

    /**
     * @param modelStore                           persistent db containing all models
     * @param modelConf                            identify the model configuration the cache stands for
     * @param retriever                            help to calculates the context id
     * @param maxDiffBetweenCachedModelAndEvent cached model can be older then eventTime by this diff. if bigger, cached model is deleted and the latest model is retrieved from db
     * @param lruModelCacheSize                    the cache size
     */
    public ModelCacheManagerInMemory(ModelStore modelStore, ModelConf modelConf, AbstractDataRetriever retriever, Duration maxDiffBetweenCachedModelAndEvent, int lruModelCacheSize, int numOfModelsPerContextId) {
        this.modelStore = modelStore;
        this.modelConf = modelConf;
        this.retriever = retriever;
        this.maxDiffBetweenCachedModelAndEvent = maxDiffBetweenCachedModelAndEvent;
        this.lruModelsMap = new LRUMap(lruModelCacheSize);
        this.numOfModelsPerContextId = numOfModelsPerContextId;
    }

    @Override
    public Model getLatestModelBeforeEventTime(Map<String, String> context, Instant eventTime) {
        String contextId = getContextId(context);
        return getLatestModelBeforeEventTime(contextId, eventTime);
    }
    @Override
    public Model getLatestModelBeforeEventTime(String contextId, Instant eventTime) {
        logger.debug("getting model for params: contextId={},eventTime={},modelConf={}", contextId,  eventTime,modelConf);
        ModelDAO cachedModelDao = getLatestBeforeEventTimeAfterOldestAllowedModelDao(contextId, eventTime);
        return cachedModelDao.getModel();
    }

    private ModelDAO getLatestBeforeEventTimeAfterOldestAllowedModelDao(String contextId, Instant eventTime) {
        List<ModelDAO> retrievedModelDAOs = getModelDAOsSortedByEndTimeDesc(contextId,eventTime);
        return retrievedModelDAOs.get(0);
    }

    @Override
    public List<ModelDAO> getModelDAOsSortedByEndTimeDesc(String contextId, Instant eventTime){
        Instant oldestAllowedModelTime = eventTime.minus(maxDiffBetweenCachedModelAndEvent);
        if (lruModelsMap.containsKey(contextId)) {
            Object cachedObject = lruModelsMap.get(contextId);
            List<ModelDAO> ret = (List<ModelDAO>) cachedObject;
            logger.debug("found cached models={}", ret);
            return ret;

        } else{
            logger.debug("no matching model found in cache. retrieving model from db");
            List<ModelDAO> retrievedModelDAOs =
                    modelStore.getLatestBeforeEventTimeAfterOldestAllowedModelDaoSortedByEndTimeDesc(modelConf, contextId, eventTime, oldestAllowedModelTime, numOfModelsPerContextId);
            if (retrievedModelDAOs == null || !retrievedModelDAOs.isEmpty()) {
                logger.debug("found matching modelDAO={} in db", retrievedModelDAOs);
            } else {
                logger.debug("did not find matching model in db. caching empty model");
                retrievedModelDAOs = Collections.singletonList(new EmptyModelDao(eventTime));
            }
            // insert the model into cache
            lruModelsMap.put(contextId, retrievedModelDAOs);
            return retrievedModelDAOs;
        }
    }

    @Override
    public void deleteFromCache(String modelConfName, String contextId) {
        lruModelsMap.remove(contextId);
    }

    private String getContextId(Map<String, String> contextFieldsToValueMap) {
        Assert.notNull(contextFieldsToValueMap, "context fields should not be null");
        if (contextFieldsToValueMap.isEmpty()) {
            return null;
        }
        return retriever.getContextId(contextFieldsToValueMap);
    }

    public LRUMap getLruModelsMap() {
        return lruModelsMap;
    }
}
