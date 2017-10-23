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

    /**
     * @param modelStore                           persistent db containing all models
     * @param modelConf                            identify the model configuration the cache stands for
     * @param retriever                            help to calculates the context id
     * @param maxDiffBetweenCachedModelAndEvent cached model can be older then eventTime by this diff. if bigger, cached model is deleted and the latest model is retrieved from db
     * @param lruModelCacheSize                    the cache size
     */
    public ModelCacheManagerInMemory(ModelStore modelStore, ModelConf modelConf, AbstractDataRetriever retriever, Duration maxDiffBetweenCachedModelAndEvent, int lruModelCacheSize) {
        this.modelStore = modelStore;
        this.modelConf = modelConf;
        this.retriever = retriever;
        this.maxDiffBetweenCachedModelAndEvent = maxDiffBetweenCachedModelAndEvent;
        this.lruModelsMap = new LRUMap(lruModelCacheSize);
    }

    @Override
    public Model getModel(Map<String, String> context, Instant eventTime) {
        String contextId = getContextId(context);
        return getModel(contextId, eventTime);
    }
    @Override
    public Model getModel(String contextId, Instant eventTime) {
        ModelDAO cachedModelDao;
        logger.debug("getting model for params: contextId={},eventTime={},modelConf={}", contextId,  eventTime,modelConf);
        Instant oldestAllowedModelTime = eventTime.minus(maxDiffBetweenCachedModelAndEvent);

        if (lruModelsMap.containsKey(contextId)) {
            Object cachedObject = lruModelsMap.get(contextId);
            cachedModelDao = (ModelDAO) cachedObject;
            // check if model time is valid
            if (isModelTimeValid(cachedModelDao, oldestAllowedModelTime)) {
                logger.debug("found cached model={}", cachedModelDao);
                return cachedModelDao.getModel();
            }
            // if the model in the cache is too old -> throw it away
            else {
                logger.debug("too old model={} found , deleting it from cache", cachedModelDao);
            }
        }
        else {
            logger.debug("no matching model found in cache. retrieving model from db");
            ModelDAO retrievedModelDAO =
                    modelStore.getLatestBeforeEventTimeAfterOldestAllowedModelDao(modelConf, contextId, eventTime, oldestAllowedModelTime);
            if (retrievedModelDAO != null) {
                if (isModelTimeValid(retrievedModelDAO, oldestAllowedModelTime)) {
                    logger.debug("found matching modelDAO={} in db, updating cache", retrievedModelDAO);
                    // insert the model into cache
                    lruModelsMap.put(contextId, retrievedModelDAO);
                    return retrievedModelDAO.getModel();
                } else {
                    logger.debug("retrieved model is too old");
                }
            }
            logger.debug("did not find matching model in db. caching empty model");
            lruModelsMap.put(contextId, new EmptyModelDao(eventTime));
        }

        return null;
    }

    public boolean isModelTimeValid(ModelDAO modelDAO, Instant latestModelEventTime) {
        return modelDAO.getEndTime().compareTo(latestModelEventTime) >= 0;
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
