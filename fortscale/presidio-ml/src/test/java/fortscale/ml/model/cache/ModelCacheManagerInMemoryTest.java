package fortscale.ml.model.cache;

import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.cache.metrics.ModelCacheMetricsContainer;
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
import java.util.Collections;
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
    @MockBean
    public ModelCacheMetricsContainer modelCacheMetricsContainer;
    private Duration maxDiffBetweenCachedModelAndEvent = Duration.ofDays(1);
    int cacheSize = 100;

    @Test
    public void shouldNotReloadModelToCacheOnceExpired()
    {
        ModelCacheManagerInMemory modelCacheManagerInMemory = new ModelCacheManagerInMemory(modelStore, modelConf, dataRetriever, maxDiffBetweenCachedModelAndEvent, cacheSize, 1, modelCacheMetricsContainer);
        Instant eventTime = Instant.now();
        HashMap<String, String> context = new HashMap<>();
        context.put("contextField","non_existing_contextId");
        String contextId = "non_existing_contextId";
        Mockito.when(dataRetriever.getContextId(context)).thenReturn(contextId);
        Instant oldestAllowedModelTime = eventTime.minus(maxDiffBetweenCachedModelAndEvent);
        Mockito.when(modelStore.getLatestBeforeEventTimeAfterOldestAllowedModelDaoSortedByEndTimeDesc(modelConf,contextId,eventTime, oldestAllowedModelTime, 1)).thenReturn(Collections.emptyList());

        Model model = modelCacheManagerInMemory.getLatestModelBeforeEventTime(context, eventTime);
        Assert.assertEquals(null,model);
        Mockito.when(modelStore.getLatestBeforeEventTimeAfterOldestAllowedModelDaoSortedByEndTimeDesc(modelConf,contextId,eventTime, oldestAllowedModelTime, 1)).thenReturn(null);

        Model returnedModel = () -> 0;
        Instant newEventEndTime = eventTime.plus(maxDiffBetweenCachedModelAndEvent).plusMillis(1);
        ModelDAO returnedModelDao = new ModelDAO("sessionId", contextId, returnedModel, eventTime, newEventEndTime);
        Mockito.when(modelStore.getLatestBeforeEventTimeAfterOldestAllowedModelDaoSortedByEndTimeDesc(modelConf,contextId,newEventEndTime, newEventEndTime.minus(maxDiffBetweenCachedModelAndEvent), 1)).thenReturn(Collections.singletonList(returnedModelDao));
        model = modelCacheManagerInMemory.getLatestModelBeforeEventTime(context, newEventEndTime);
        Assert.assertEquals(null,model);
    }

    @Test
    public void shouldReturnNullModelIfNonExistInStore() throws Exception {
        ModelCacheManagerInMemory modelCacheManagerInMemory = new ModelCacheManagerInMemory(modelStore, modelConf, dataRetriever, maxDiffBetweenCachedModelAndEvent, cacheSize, 1, modelCacheMetricsContainer);

        Instant eventTime = Instant.now();

        HashMap<String, String> context = new HashMap<>();
        context.put("contextField","non_existing_contextId");
        String contextId = "non_existing_contextId";
        Mockito.when(dataRetriever.getContextId(context)).thenReturn(contextId);
        Instant oldestAllowedModelTime = eventTime.minus(maxDiffBetweenCachedModelAndEvent);
        Mockito.when(modelStore.getLatestBeforeEventTimeAfterOldestAllowedModelDaoSortedByEndTimeDesc(modelConf,contextId,eventTime, oldestAllowedModelTime, 1)).thenReturn(Collections.emptyList());

        Model model = modelCacheManagerInMemory.getLatestModelBeforeEventTime(context, eventTime);

        // takes models from store
        Mockito.verify(modelStore,times(1)).getLatestBeforeEventTimeAfterOldestAllowedModelDaoSortedByEndTimeDesc(modelConf,contextId,eventTime, oldestAllowedModelTime, 1);
        Assert.assertEquals(null,model);

        // verify that null model is cached and no call is preformed to store if we already know that there is now model
        modelCacheManagerInMemory.getLatestModelBeforeEventTime(context, eventTime);
        modelCacheManagerInMemory.getLatestModelBeforeEventTime(context, eventTime);
        modelCacheManagerInMemory.getLatestModelBeforeEventTime(context, eventTime);
        Mockito.verifyNoMoreInteractions(modelStore);
    }

    @Test
    public void shouldReturnModelFromCache() throws Exception {
        ModelCacheManagerInMemory modelCacheManagerInMemory =
                new ModelCacheManagerInMemory(modelStore, modelConf, dataRetriever, maxDiffBetweenCachedModelAndEvent, cacheSize, 1, modelCacheMetricsContainer);

        Instant eventTime = Instant.now();
        Instant oldestAllowedModelTime = eventTime.minus(maxDiffBetweenCachedModelAndEvent);

        HashMap<String, String> context = new HashMap<>();
        context.put("contextField","existing_contextId");
        String contextId = "existing_contextId";
        Mockito.when(dataRetriever.getContextId(context)).thenReturn(contextId);
        Model returnedModel = () -> 0;
        ModelDAO returnedModelDao =
                new ModelDAO("sessionId", contextId, returnedModel, eventTime.minus(1, ChronoUnit.HOURS), eventTime);
        Mockito.when(modelStore.getLatestBeforeEventTimeAfterOldestAllowedModelDaoSortedByEndTimeDesc(modelConf,contextId,eventTime, oldestAllowedModelTime,1)).thenReturn(Collections.singletonList(returnedModelDao));

        Model model = modelCacheManagerInMemory.getLatestModelBeforeEventTime(context, eventTime);

        // takes models from store
        Mockito.verify(modelStore,times(1)).getLatestBeforeEventTimeAfterOldestAllowedModelDaoSortedByEndTimeDesc(modelConf,contextId,eventTime, oldestAllowedModelTime,1);
        Assert.assertEquals(returnedModel,model);

        // verify that null model is cached and no call is preformed to store if we already know that there is now model
        modelCacheManagerInMemory.getLatestModelBeforeEventTime(context, eventTime);
        modelCacheManagerInMemory.getLatestModelBeforeEventTime(context, eventTime);
        modelCacheManagerInMemory.getLatestModelBeforeEventTime(context, eventTime);
        Mockito.verifyNoMoreInteractions(modelStore);
    }

    @Test
    public void shouldCleanCacheFromNonUsedModels() throws Exception {
        int lruModelCacheSize = 2;
        ModelCacheManagerInMemory modelCacheManagerInMemory =
                new ModelCacheManagerInMemory(modelStore, modelConf, dataRetriever, maxDiffBetweenCachedModelAndEvent, lruModelCacheSize, 1, modelCacheMetricsContainer);

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

        modelCacheManagerInMemory.getLatestModelBeforeEventTime(context1, eventTime);
        Mockito.verify(modelStore,times(1)).getLatestBeforeEventTimeAfterOldestAllowedModelDaoSortedByEndTimeDesc(modelConf,contextId1,eventTime, oldestAllowedModelTime,1);
        modelCacheManagerInMemory.getLatestModelBeforeEventTime(context2, eventTime);
        Mockito.verify(modelStore,times(1)).getLatestBeforeEventTimeAfterOldestAllowedModelDaoSortedByEndTimeDesc(modelConf,contextId2,eventTime,oldestAllowedModelTime , 1);
        modelCacheManagerInMemory.getLatestModelBeforeEventTime(context3, eventTime);
        Mockito.verify(modelStore,times(1)).getLatestBeforeEventTimeAfterOldestAllowedModelDaoSortedByEndTimeDesc(modelConf,contextId3,eventTime, oldestAllowedModelTime, 1);
        modelCacheManagerInMemory.getLatestModelBeforeEventTime(context3, eventTime);
        Mockito.verifyNoMoreInteractions(modelStore);
        modelCacheManagerInMemory.getLatestModelBeforeEventTime(context3, eventTime);
        modelCacheManagerInMemory.getLatestModelBeforeEventTime(context3, eventTime);
        Mockito.verifyNoMoreInteractions(modelStore);
        LRUMap lruModelsMap = modelCacheManagerInMemory.getLruModelsMap();
        Assert.assertTrue(lruModelsMap.containsKey(contextId3));
        Assert.assertEquals(lruModelsMap.size(),2);
    }


}