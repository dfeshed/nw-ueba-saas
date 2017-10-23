package fortscale.ml.model.cache;

import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStore;
import org.apache.commons.collections.map.LRUMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

import static org.mockito.Mockito.times;

/**
 * Created by barak_schuster on 6/6/17.
 */
@RunWith(SpringRunner.class)
public class ModelCacheManagerInMemoryTest {
    @MockBean
    public ModelStore modelStore;
    @MockBean
    public ModelConf modelConf;
    @MockBean
    public AbstractDataRetriever dataRetriever;
    private Duration maxDiffBetweenCachedModelAndEvent = Duration.ofDays(1);
    int cacheSize = 100;

    @Test
    public void shouldNotReloadModelToCacheOnceExpired()
    {
        ModelCacheManagerInMemory modelCacheManagerInMemory = new ModelCacheManagerInMemory(modelStore, modelConf, dataRetriever, maxDiffBetweenCachedModelAndEvent, cacheSize);
        Instant eventTime = Instant.now();
        HashMap<String, String> context = new HashMap<>();
        context.put("contextField","non_existing_contextId");
        String contextId = "non_existing_contextId";
        Mockito.when(dataRetriever.getContextId(context)).thenReturn(contextId);
        Instant oldestAllowedModelTime = eventTime.minus(maxDiffBetweenCachedModelAndEvent);
        Mockito.when(modelStore.getLatestBeforeEventTimeAfterOldestAllowedModelDao(modelConf,contextId,eventTime, oldestAllowedModelTime)).thenReturn(null);

        Model model = modelCacheManagerInMemory.getModel(context, eventTime);
        Assert.assertEquals(null,model);
        Mockito.when(modelStore.getLatestBeforeEventTimeAfterOldestAllowedModelDao(modelConf,contextId,eventTime, oldestAllowedModelTime)).thenReturn(null);
        Instant newEventEndTime = eventTime.plus(maxDiffBetweenCachedModelAndEvent).plusMillis(1);
        Mockito.when(modelStore.getLatestBeforeEventTimeAfterOldestAllowedModelDao(modelConf,contextId,newEventEndTime, newEventEndTime.minus(maxDiffBetweenCachedModelAndEvent))).thenReturn(null);
        model = modelCacheManagerInMemory.getModel(context, newEventEndTime);
        Assert.assertEquals(null,model);
    }

    @Test
    public void shouldReturnNullModelIfNonExistInStore() throws Exception {
        ModelCacheManagerInMemory modelCacheManagerInMemory = new ModelCacheManagerInMemory(modelStore, modelConf, dataRetriever, maxDiffBetweenCachedModelAndEvent, cacheSize);

        Instant eventTime = Instant.now();

        HashMap<String, String> context = new HashMap<>();
        context.put("contextField","non_existing_contextId");
        String contextId = "non_existing_contextId";
        Mockito.when(dataRetriever.getContextId(context)).thenReturn(contextId);
        Instant oldestAllowedModelTime = eventTime.minus(maxDiffBetweenCachedModelAndEvent);
        Mockito.when(modelStore.getLatestBeforeEventTimeAfterOldestAllowedModelDao(modelConf,contextId,eventTime, oldestAllowedModelTime)).thenReturn(null);

        Model model = modelCacheManagerInMemory.getModel(context, eventTime);

        // takes models from store
        Mockito.verify(modelStore,times(1)).getLatestBeforeEventTimeAfterOldestAllowedModelDao(modelConf,contextId,eventTime, oldestAllowedModelTime);
        Assert.assertEquals(null,model);

        // verify that null model is cached and no call is preformed to store if we already know that there is now model
        modelCacheManagerInMemory.getModel(context, eventTime);
        modelCacheManagerInMemory.getModel(context, eventTime);
        modelCacheManagerInMemory.getModel(context, eventTime);
        Mockito.verifyNoMoreInteractions(modelStore);
    }

    @Test
    public void shouldReturnModelFromCache() throws Exception {
        ModelCacheManagerInMemory modelCacheManagerInMemory =
                new ModelCacheManagerInMemory(modelStore, modelConf, dataRetriever, maxDiffBetweenCachedModelAndEvent, cacheSize);

        Instant eventTime = Instant.now();
        Instant oldestAllowedModelTime = eventTime.minus(maxDiffBetweenCachedModelAndEvent);

        HashMap<String, String> context = new HashMap<>();
        context.put("contextField","existing_contextId");
        String contextId = "existing_contextId";
        Mockito.when(dataRetriever.getContextId(context)).thenReturn(contextId);
        Model returnedModel = () -> 0;
        ModelDAO returnedModelDao =
                new ModelDAO("sessionId", contextId, returnedModel, eventTime.minus(1, ChronoUnit.HOURS), eventTime);
        Mockito.when(modelStore.getLatestBeforeEventTimeAfterOldestAllowedModelDao(modelConf,contextId,eventTime, oldestAllowedModelTime)).thenReturn(returnedModelDao );

        Model model = modelCacheManagerInMemory.getModel(context, eventTime);

        // takes models from store
        Mockito.verify(modelStore,times(1)).getLatestBeforeEventTimeAfterOldestAllowedModelDao(modelConf,contextId,eventTime, oldestAllowedModelTime);
        Assert.assertEquals(returnedModel,model);

        // verify that null model is cached and no call is preformed to store if we already know that there is now model
        modelCacheManagerInMemory.getModel(context, eventTime);
        modelCacheManagerInMemory.getModel(context, eventTime);
        modelCacheManagerInMemory.getModel(context, eventTime);
        Mockito.verifyNoMoreInteractions(modelStore);
    }

    @Test
    public void shouldCleanCacheFromNonUsedModels() throws Exception {
        int lruModelCacheSize = 2;
        ModelCacheManagerInMemory modelCacheManagerInMemory =
                new ModelCacheManagerInMemory(modelStore, modelConf, dataRetriever, maxDiffBetweenCachedModelAndEvent, lruModelCacheSize);

        Instant eventTime = Instant.now();
        Instant oldestAllowedModelTime = eventTime.minus(maxDiffBetweenCachedModelAndEvent);

        HashMap<String, String> context1 = new HashMap<>();
        context1.put("contextField","existing_contextId1");
        String contextId1 = "existing_contextId1";
        Mockito.when(dataRetriever.getContextId(context1)).thenReturn(contextId1);
        HashMap<String, String> context2 = new HashMap<>();
        context2.put("contextField","existing_contextId2");
        String contextId2 = "existing_contextId2";
        Mockito.when(dataRetriever.getContextId(context2)).thenReturn(contextId2);
        HashMap<String, String> context3 = new HashMap<>();
        context3.put("contextField","existing_contextId3");
        String contextId3 = "existing_contextId3";
        Mockito.when(dataRetriever.getContextId(context3)).thenReturn(contextId3);

        modelCacheManagerInMemory.getModel(context1, eventTime);
        Mockito.verify(modelStore,times(1)).getLatestBeforeEventTimeAfterOldestAllowedModelDao(modelConf,contextId1,eventTime, oldestAllowedModelTime);
        modelCacheManagerInMemory.getModel(context2, eventTime);
        Mockito.verify(modelStore,times(1)).getLatestBeforeEventTimeAfterOldestAllowedModelDao(modelConf,contextId2,eventTime,oldestAllowedModelTime );
        modelCacheManagerInMemory.getModel(context3, eventTime);
        Mockito.verify(modelStore,times(1)).getLatestBeforeEventTimeAfterOldestAllowedModelDao(modelConf,contextId3,eventTime, oldestAllowedModelTime);
        modelCacheManagerInMemory.getModel(context3, eventTime);
        Mockito.verifyNoMoreInteractions(modelStore);
        modelCacheManagerInMemory.getModel(context3, eventTime);
        modelCacheManagerInMemory.getModel(context3, eventTime);
        Mockito.verifyNoMoreInteractions(modelStore);
        LRUMap lruModelsMap = modelCacheManagerInMemory.getLruModelsMap();
        Assert.assertTrue(lruModelsMap.containsKey(contextId3));
        Assert.assertEquals(lruModelsMap.size(),2);
    }


}