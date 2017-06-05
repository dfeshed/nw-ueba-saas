package fortscale.ml.model.cache;

import fortscale.common.feature.Feature;
import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStore;
import org.apache.commons.collections.map.LRUMap;
import org.springframework.util.Assert;

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

    private ModelStore modelStore;
    private ModelConf modelConf;
    private AbstractDataRetriever retriever;
    private long futureDiffBetweenModelAndEvent;
    private LRUMap lruModelsMap;

    public ModelCacheManagerInMemory(ModelStore modelStore, ModelConf modelConf, AbstractDataRetriever retriever, long futureDiffBetweenModelAndEvent, int lruModelCacheSize) {
        this.modelStore = modelStore;
        this.modelConf = modelConf;
        this.retriever = retriever;
        this.futureDiffBetweenModelAndEvent = futureDiffBetweenModelAndEvent;
        this.lruModelsMap = new LRUMap (lruModelCacheSize);
    }

    @Override
    public Model getModel(Feature feature, Map<String, String> context, long eventEpochtime) {
        String contextId = getContextId(context);

        ModelDAO modelDAO = null;
        Object cachedModel = lruModelsMap.get(contextId);
        // there is a model in cache
        if(cachedModel!=null)
        {
            modelDAO = (ModelDAO) cachedModel;
            // check if model time is valid
            if (modelDAO.getEndTime().getTime()<=eventEpochtime+futureDiffBetweenModelAndEvent)
            {
                return modelDAO.getModel();
            }
            // if the model in the cache is too old -> throw it away
            else
            {
                deleteFromCache(modelConf.getName(),contextId);
            }
        }
        // get models from db
        List<ModelDAO> modelDaos = modelStore.getModelDaos(modelConf, contextId);
        if(modelDaos!=null && !modelDaos.isEmpty()) {
            Collections.sort(modelDaos, new ModelsCacheInfo.DescModelDaoEndTimeComp());

            // take the latest model
            modelDAO = modelDaos.stream().findFirst().get();
            if (modelDAO != null) {

                if (Instant.ofEpochMilli(modelDAO.getEndTime().getTime()).isBefore(Instant.ofEpochSecond(eventEpochtime + futureDiffBetweenModelAndEvent))) {
                    // insert the model into cache
                    lruModelsMap.put(contextId, modelDAO);
                    return modelDAO.getModel();
                }
            }
        }
        return null;

    }

    @Override
    public void deleteFromCache(String modelConfName, String contextId) {
        lruModelsMap.remove(contextId);
    }

    private String getContextId(Map<String, String> contextFieldsToValueMap) {
        Assert.notNull(contextFieldsToValueMap,"context fields should not be null");
        if (contextFieldsToValueMap.isEmpty()) {
            return null;
        }
        return retriever.getContextId(contextFieldsToValueMap);
    }
}
