package fortscale.ml.model.retriever;

import com.google.common.collect.Sets;
import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.*;
import fortscale.ml.model.retriever.smart_data.SmartValueData;
import fortscale.ml.model.selector.AccumulatedSmartContextSelector;
import fortscale.ml.model.selector.AccumulatedSmartContextSelectorConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStore;
import fortscale.ml.scorer.algorithms.SmartWeightsScorerAlgorithm;
import fortscale.smart.record.conf.ClusterConf;
import fortscale.smart.record.conf.SmartRecordConf;
import fortscale.smart.record.conf.SmartRecordConfService;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import net.minidev.json.JSONObject;
import org.assertj.core.util.Maps;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import presidio.ade.domain.record.accumulator.AccumulatedSmartRecord;
import presidio.ade.domain.store.accumulator.smart.SmartAccumulationDataReader;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by barak_schuster on 29/08/2017.
 */
@RunWith(JUnit4.class)
public class AccumulatedSmartValueRetrieverTest {
    private SmartAccumulationDataReader smartAccumulationDataReader;
    private SmartRecordConfService smartRecordConfService;
    private FactoryService<IContextSelector> contextSelectorFactoryService;

    private ModelStore modelStore;
    private Duration oldestAllowedModelDurationDiff;
    private AccumulatedSmartValueRetriever retriever;
    private Instant startTime = Instant.now();
    private Instant endTime = Instant.now();
    private String contextId1 = "contextId1";
    private String contextId2 = "contextId2";
    private String contextId3 = "contextId3";

    @Before
    public void setup() {
        oldestAllowedModelDurationDiff = Duration.ofDays(2);
        modelStore = mock(ModelStore.class);
        contextSelectorFactoryService = mock(FactoryService.class);
        smartRecordConfService = mock(SmartRecordConfService.class);
        smartAccumulationDataReader = mock(SmartAccumulationDataReader.class);

        String weightsModelName = "weightsModelName";
        String smartRecordConfName = "smartRecordConfName";
        List<JSONObject> functions = Collections.singletonList(new JSONObject());
        long timeRangeInSeconds = Duration.ofDays(30).toMillis() / 1000;

        SmartRecordConf smartRecordConf = mock(SmartRecordConf.class);
        when(smartRecordConf.getName()).thenReturn(smartRecordConfName);
        when(smartRecordConf.getFixedDurationStrategy()).thenReturn(FixedDurationStrategy.HOURLY);
        AccumulatedSmartValueRetrieverConf smartValueRetrieverConf = new AccumulatedSmartValueRetrieverConf(timeRangeInSeconds, functions, smartRecordConfName, weightsModelName,86400);
        when(smartRecordConfService.getSmartRecordConf(smartValueRetrieverConf.getSmartRecordConfName())).thenReturn(smartRecordConf);

        String featureName = "featureName_hourly";
        double weight = 1.0;
        SmartWeightsModel smartWeightsModel = new SmartWeightsModel().setClusterConfs(Collections.singletonList(new ClusterConf(Collections.singletonList(featureName), weight)));

        ModelConf smartWeightsModelConf = mock(ModelConf.class);

        ModelConfService modelConfService = mock(ModelConfService.class);
        DynamicModelConfServiceContainer.setModelConfService(modelConfService);
        when(modelConfService.getModelConf(eq(weightsModelName)))
                .thenReturn(smartWeightsModelConf);

        String globalContextId = null;
        ModelDAO modelDAO = new ModelDAO("sessionId", globalContextId, smartWeightsModel, startTime, endTime);
        when(modelStore.getLatestBeforeEventTimeAfterOldestAllowedModelDaoSortedByEndTimeDesc(eq(smartWeightsModelConf), eq(globalContextId), any(), any(), eq(1))).thenReturn(Collections.singletonList(modelDAO));

        retriever = new AccumulatedSmartValueRetriever(smartValueRetrieverConf, smartAccumulationDataReader, smartRecordConfService, contextSelectorFactoryService, modelStore, oldestAllowedModelDurationDiff, new SmartWeightsScorerAlgorithm(0.5, 50));

        IContextSelector contextSelector = Mockito.mock(AccumulatedSmartContextSelector.class);
        when(contextSelector.getContexts(any())).thenReturn(Sets.newHashSet(contextId1, contextId2, contextId3));
        when(contextSelectorFactoryService.getProduct(any(AccumulatedSmartContextSelectorConf.class))).thenReturn(contextSelector);

        AccumulatedSmartRecord contextId1accumulatedSmartRecord = new AccumulatedSmartRecord(startTime,endTime,contextId1,featureName);
        int contextId1activityTime = 1;
        double contextId1featureValue = 95.0;
        contextId1accumulatedSmartRecord.getAggregatedFeatureEventsValuesMap().put(featureName, Maps.newHashMap(contextId1activityTime, contextId1featureValue));
        contextId1accumulatedSmartRecord.getActivityTime().add(contextId1activityTime);
        when(smartAccumulationDataReader.findAccumulatedEventsByContextIdAndStartTimeRange(eq(smartRecordConfName), eq(contextId1),any(),any())).thenReturn(Collections.singletonList(contextId1accumulatedSmartRecord));

        AccumulatedSmartRecord contextId2accumulatedSmartRecord = new AccumulatedSmartRecord(startTime,endTime,contextId2,featureName);
        int contextId2activityTime = 2;
        double contextId2featureValue = 92.0;
        contextId2accumulatedSmartRecord.getAggregatedFeatureEventsValuesMap().put(featureName, Maps.newHashMap(contextId2activityTime, contextId2featureValue));
        contextId2accumulatedSmartRecord.getActivityTime().add(contextId2activityTime);
        when(smartAccumulationDataReader.findAccumulatedEventsByContextIdAndStartTimeRange(eq(smartRecordConfName), eq(contextId2),any(),any())).thenReturn(Collections.singletonList(contextId2accumulatedSmartRecord));

        AccumulatedSmartRecord contextId3accumulatedSmartRecord = new AccumulatedSmartRecord(startTime,endTime,contextId3,featureName);
        int contextId3activityTime = 3;
        double contextId3featureValue = 92.0;
        contextId3accumulatedSmartRecord.getAggregatedFeatureEventsValuesMap().put(featureName, Maps.newHashMap(contextId3activityTime, contextId3featureValue));
        contextId3accumulatedSmartRecord.getActivityTime().add(contextId3activityTime);
        when(smartAccumulationDataReader.findAccumulatedEventsByContextIdAndStartTimeRange(eq(smartRecordConfName), eq(contextId3),any(),any())).thenReturn(Collections.singletonList(contextId3accumulatedSmartRecord));
    }

    @Test
    public void retrieveGlobalContext() throws Exception {
        ModelBuilderData modelBuilderData = retriever.retrieve(null, Date.from(endTime));
        assertEquals(null,modelBuilderData.getNoDataReason());
        GenericHistogram genericHistogram = ((SmartValueData) modelBuilderData.getData()).getGenericHistogram();

        assertEquals(genericHistogram.getTotalCount(),3,0);
        assertEquals(2.0,genericHistogram.get(0.92),0);
        assertEquals(1.0,genericHistogram.get(0.95),0);
    }

    @Test
    public void retrieveNonGlobalContext()
    {
        ModelBuilderData modelBuilderData = retriever.retrieve(contextId1, Date.from(endTime));
        assertEquals(null,modelBuilderData.getNoDataReason());
        GenericHistogram genericHistogram =((SmartValueData) modelBuilderData.getData()).getGenericHistogram();

        assertEquals(genericHistogram.getTotalCount(),1,0);
        assertEquals(1.0,genericHistogram.get(0.95),0);
    }

    @Test
    public void calcNumOfPartitions()
    {
        List<AccumulatedSmartRecord> data = new LinkedList<>();
        Instant startTime = Instant.EPOCH;
        for (int i=0; i<42 ; i++)
        {
            data.add(new AccumulatedSmartRecord(startTime,startTime.plus(Duration.ofHours(1)),"",""));
            startTime = startTime.plus(Duration.ofHours(1));
        }
        Set<Long> partitionIds = retriever.calcNumOfPartitions(data);
        Assert.assertEquals("dividing 42 hours into partitions in daily resolution should be 2",2,partitionIds.size());
    }

}