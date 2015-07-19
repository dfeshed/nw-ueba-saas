package fortscale.streaming.service.aggregation.feature.event;

import fortscale.streaming.aggregation.feature.functions.IAggrFeatureEventFunctionsService;
import fortscale.streaming.aggregation.feature.util.GenericHistogram;
import fortscale.streaming.service.aggregation.*;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategy;
import net.minidev.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by amira on 15/07/2015.
 */

public class AggrFeatureEventBuilderTest {
    @Mock
    private DataSourcesSyncTimer dataSourcesSyncTimer;

    @Mock
    private FeatureBucketsService featureBucketsService;

    @Mock
    private IAggrFeatureEventFunctionsService aggrFeatureFuncService;

    @Mock
    AggrEventTopologyService aggrEventTopologyService;

    DataSourcesSyncTimerListener dataSourcesSyncTimerListener;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    public AggrFeatureEventBuilder createBuilder() {
        // Creating AggregatedFeatureEventConf
        Map<String, List<String>> paramters2featuresListMap = new HashMap<>();
        List<String> aggrFeatureNames = new ArrayList<>();
        aggrFeatureNames.add("aggrFeature1");
        paramters2featuresListMap.put("groupBy", aggrFeatureNames);
        JSONObject funcJSONObj = new JSONObject();
        funcJSONObj.put("type", "aggr_feature_histogram_func");

        AggregatedFeatureEventConf eventConf = new AggregatedFeatureEventConf("myFeatureEvent", "bucketConf1", 1, 1, 0, paramters2featuresListMap, funcJSONObj );
        FeatureBucketConf bucketConf = mock(FeatureBucketConf.class);
        List<String> dataSources = new ArrayList<>();
        dataSources.add("ssh");
        when(bucketConf.getDataSources()).thenReturn(dataSources);
        eventConf.setBucketConf(bucketConf);


        FeatureBucketStrategy strategy = mock(FeatureBucketStrategy.class);

        AggrFeatureEventService aggrFeatureEventService = mock(AggrFeatureEventService.class);

        // Create AggrFeatureEventBuilder
        AggrFeatureEventBuilder builder = new AggrFeatureEventBuilder(eventConf, strategy, aggrFeatureEventService, featureBucketsService);


        builder.setAggrEventTopologyService(aggrEventTopologyService);
        builder.setAggrFeatureFuncService(aggrFeatureFuncService);
        builder.setDataSourcesSyncTimer(dataSourcesSyncTimer);
        builder.setFeatureBucketsService(featureBucketsService);

        return builder;
    }

    FeatureBucket createFeatureBucket() {
        GenericHistogram histogram1 = new GenericHistogram();
        GenericHistogram histogram2 = new GenericHistogram();
        GenericHistogram histogram3 = new GenericHistogram();
        GenericHistogram histogram4 = new GenericHistogram();

        histogram1.add("a", 1.0);
        histogram1.add("b", 2.0);
        histogram1.add("c", 3.0);

        histogram2.add("b", 2.0);
        histogram2.add("c", 3.0);
        histogram2.add("d", 4.0);

        histogram3.add("a", 1.0);
        histogram3.add("b", 4.0);
        histogram3.add("c", 6.0);
        histogram3.add("d", 4.0);

        return null; //TODO
    }
    @Test
    public void testUpdateAggrFeatureEvent() {
        AggrFeatureEventBuilder builder = createBuilder();
        String bucketID = "bucketID1";
        Long startTime1 = 1436918400L; //Wed, 15 Jul 2015 00:00:00 GMT
        Long endTime1 = 1437004799L; //Wed, 15 Jul 2015 23:59:59 GMT
        Map<String, String> context = new HashMap<>();
        context.put("username", "john");
        context.put("machine", "m1");
        List<String> dataSources = new ArrayList<>();
        dataSources.add("ssh");


        when(dataSourcesSyncTimer.notifyWhenDataSourcesReachTime(eq(dataSources), eq(endTime1), any(DataSourcesSyncTimerListener.class))).then(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                Object mock = invocation.getMock();
                dataSourcesSyncTimerListener = (DataSourcesSyncTimerListener) args[2];
                return 1000L;
            }
        });

        FeatureBucket featureBucketMock = mock(FeatureBucket.class);
        when(featureBucketsService.getFeatureBucket(any(FeatureBucketConf.class), eq(bucketID))).thenReturn(featureBucketMock);

        builder.updateAggrFeatureEventData(bucketID, context, startTime1, endTime1);
        dataSourcesSyncTimerListener.dataSourcesReachedTime();

        //TODO
    }

    @Test
    public void testUpdateFeatureBacketEndTime() {
        //TODO
    }

    @Test
    public void testBuildEvent() {
        //TODO
    }

}
