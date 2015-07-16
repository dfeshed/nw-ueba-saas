package fortscale.streaming.service.aggregation.feature.event;

import fortscale.streaming.aggregation.feature.functions.AggrFeatureEventFunction;
import fortscale.streaming.aggregation.feature.functions.IAggrFeatureEventFunctionsService;
import fortscale.streaming.service.aggregation.AggrEventTopologyService;
import fortscale.streaming.service.aggregation.DataSourcesSyncTimer;
import fortscale.streaming.service.aggregation.FeatureBucketConf;
import fortscale.streaming.service.aggregation.FeatureBucketsService;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategy;
import net.minidev.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
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
        AggrFeatureEventBuilder builder = new AggrFeatureEventBuilder(eventConf, strategy, aggrFeatureEventService);

        builder.setAggrEventTopologyService(aggrEventTopologyService);
        builder.setAggrFeatureFuncService(aggrFeatureFuncService);
        builder.setDataSourcesSyncTimer(dataSourcesSyncTimer);
        builder.setFeatureBucketsService(featureBucketsService);

        return builder;
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

        builder.updateAggrFeatureEvent(bucketID, context, startTime1, endTime1);

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
