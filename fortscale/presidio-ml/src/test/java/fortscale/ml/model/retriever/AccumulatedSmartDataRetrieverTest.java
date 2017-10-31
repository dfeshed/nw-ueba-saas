package fortscale.ml.model.retriever;

import fortscale.ml.model.DynamicModelConfServiceContainer;
import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.retriever.smart_data.SmartAggregatedRecordDataContainer;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.smart.record.conf.SmartRecordConf;
import fortscale.smart.record.conf.SmartRecordConfService;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import presidio.ade.domain.store.accumulator.smart.SmartAccumulationDataReader;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by barak_schuster on 10/22/17.
 */
@RunWith(JUnit4.class)
public class AccumulatedSmartDataRetrieverTest {

    private AccumulatedSmartDataRetriever retriever;
    private SmartAccumulationDataReader smartAccumulationDataReader;
    private SmartRecordConfService smartRecordConfService;
    private FactoryService<IContextSelector> contextSelectorFactoryService;



    @Before
    public void setup()
    {
        contextSelectorFactoryService = mock(FactoryService.class);
        smartRecordConfService = mock(SmartRecordConfService.class);
        smartAccumulationDataReader = mock(SmartAccumulationDataReader.class);

        String smartRecordConfName = "smartRecordConfName";
        List<JSONObject> functions = Collections.singletonList(new JSONObject());
        long timeRangeInSeconds = Duration.ofDays(30).toMillis() / 1000;

        SmartRecordConf smartRecordConf = mock(SmartRecordConf.class);
        when(smartRecordConf.getName()).thenReturn(smartRecordConfName);
        when(smartRecordConf.getFixedDurationStrategy()).thenReturn(FixedDurationStrategy.HOURLY);
        AccumulatedSmartDataRetrieverConf smartValueRetrieverConf = new AccumulatedSmartDataRetrieverConf(timeRangeInSeconds, functions, smartRecordConfName, 86400);
        when(smartRecordConfService.getSmartRecordConf(smartValueRetrieverConf.getSmartRecordConfName())).thenReturn(smartRecordConf);

        ModelConfService modelConfService = mock(ModelConfService.class);
        DynamicModelConfServiceContainer.setModelConfService(modelConfService);

        retriever = new AccumulatedSmartDataRetriever(smartValueRetrieverConf, contextSelectorFactoryService,smartAccumulationDataReader, smartRecordConfService);
    }

    @Test
    public void calcNumOfPartitions() throws Exception {
        List<SmartAggregatedRecordDataContainer> data = new LinkedList<>();
        Instant startTime = Instant.EPOCH;
        for (int i=0; i<42 ; i++)
        {
            data.add(new SmartAggregatedRecordDataContainer(startTime,new HashMap<>()));
            startTime = startTime.plus(Duration.ofHours(1));
        }
        long numOfPartitions = retriever.calcNumOfPartitions(data);

        Assert.assertEquals(2,numOfPartitions);
    }

}