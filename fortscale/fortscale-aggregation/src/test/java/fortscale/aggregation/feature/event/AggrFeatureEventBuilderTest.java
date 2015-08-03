package fortscale.aggregation.feature.event;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import fortscale.aggregation.feature.bucket.strategy.*;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fortscale.aggregation.DataSourcesSyncTimer;
import fortscale.aggregation.DataSourcesSyncTimerListener;
import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsService;
import fortscale.aggregation.feature.util.GenericHistogram;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/bucketconf-context-test.xml" })
public class AggrFeatureEventBuilderTest {
    @Mock
    private DataSourcesSyncTimer dataSourcesSyncTimer;

    @Mock
    private FeatureBucketsService featureBucketsService;

    @Mock
    private AggrEventTopologyService aggrEventTopologyService;

    private DataSourcesSyncTimerListener dataSourcesSyncTimerListener;
    private JSONObject event;
    private Long startTime1 = 1436918400L; //Wed, 15 Jul 2015 00:00:00 GMT
    private Long endTime1 = 1437004799L; //Wed, 15 Jul 2015 23:59:59 GMT
    private Long day = 86400L;
    private Long registartionID = 1000L;
    FeatureBucketStrategy strategy;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    private AggrFeatureEventBuilder createBuilder(int numberOfBuckets, int bucketLeap) throws Exception{
        // Creating AggregatedFeatureEventConf
        Map<String, List<String>> paramters2featuresListMap = new HashMap<>();
        List<String> aggrFeatureNames = new ArrayList<>();
        aggrFeatureNames.add("letters");
        paramters2featuresListMap.put("groupBy", aggrFeatureNames);
        JSONObject funcJSONObj = new JSONObject();
        funcJSONObj.put("type", "aggr_feature_number_of_distinct_values_func");
        funcJSONObj.put("includeValues", true);

        AggregatedFeatureEventConf eventConf = new AggregatedFeatureEventConf("my_number_of_distinct_values", "F", "bc1", numberOfBuckets , bucketLeap, 0, paramters2featuresListMap, funcJSONObj );
        FeatureBucketConf bucketConf = mock(FeatureBucketConf.class);
        List<String> dataSources = new ArrayList<>();
        dataSources.add("ssh");
        when(bucketConf.getDataSources()).thenReturn(dataSources);
        eventConf.setBucketConf(bucketConf);


        strategy = createFixedDurationStrategy();
        AggrFeatureEventService aggrFeatureEventService = mock(AggrFeatureEventService.class);

        // Create AggrFeatureEventBuilder
        AggrFeatureEventBuilder builder = new AggrFeatureEventBuilder(eventConf, strategy, featureBucketsService);


        builder.setAggrEventTopologyService(aggrEventTopologyService);
        builder.setDataSourcesSyncTimer(dataSourcesSyncTimer);
        builder.setFeatureBucketsService(featureBucketsService);

        return builder;
    }

    private FeatureBucketStrategy createFixedDurationStrategy() throws Exception{
        JSONObject strategyJson = new JSONObject();
        strategyJson.put("name", "fixed_time_daily");
        strategyJson.put("type", "fixed_time");
        JSONObject params = new JSONObject();
        params.put("durationInSeconds", 60*60*24);
        strategyJson.put("params", params);

        return new FixedDurationFeatureBucketStrategyFactory().createFeatureBucketStrategy(new StrategyJson(strategyJson));
    }

    private FeatureBucket createFeatureBucket(int bucketNumber) {
        GenericHistogram histogram1 = new GenericHistogram();

        histogram1.add("a", 1.0);
        histogram1.add("b", 2.0);
        histogram1.add("c", 3.0);
        histogram1.add("defghijklmnopqrstuvwxyz".substring(bucketNumber-1,bucketNumber), 4.0);

        Map<String, Feature> aggregatedFeatures = new HashMap<>();
        Feature feature = new Feature("letters", histogram1);
        aggregatedFeatures.put(feature.getName(), feature);

        List<String> contextFieldNames = new ArrayList<>();
        contextFieldNames.add("username");
        contextFieldNames.add("machine");

        Map<String, String> contextFieldNameToValueMap = new HashMap<>();
        contextFieldNameToValueMap.put("username", "john");
        contextFieldNameToValueMap.put("machine", "m1");

        List<String> dataSources = new ArrayList<>();
        dataSources.add("ssh");



        Long startTime = startTime1 + (bucketNumber-1)*day;
        Long endTime = endTime1 + (bucketNumber-1)*day;

        FeatureBucket featureBucket = new FeatureBucket();
        featureBucket.setAggregatedFeatures(aggregatedFeatures);
        featureBucket.setBucketId(String.format("bucketId_%d", bucketNumber));
        featureBucket.setContextFieldNames(contextFieldNames);
        featureBucket.setContextFieldNameToValueMap(contextFieldNameToValueMap);
        featureBucket.setDataSources(dataSources);
        featureBucket.setEndTime(endTime);
        featureBucket.setStartTime(startTime);
        featureBucket.setFeatureBucketConfName("bc1");
        featureBucket.setStrategyId("strategyId");

        return featureBucket;
    }


	private void assertEvent(JSONObject event, int startTimeDayNumber, int endTimeDayNumber, Long numberOfDistinctValues) {
        //{"start_time_unix":1436918400,"max_cout_object":"c","end_time":"2015-07-16 02:59:59","bucket_conf_name":null,"event_type":"aggregated_feature_event","context":{"username":"john","machine":"m1"},"start_time":"2015-07-15 03:00:00","end_time_unix":1437004799,"date_time":"2015-07-20 10:14:49","date_time_unix":1437376489}
        System.out.println(event.toString());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

        Long startTime = startTime1 + (startTimeDayNumber-1)*day;
        Long endTime = endTime1 + (endTimeDayNumber-1)*day;

        Assert.assertEquals("F", event.get("aggregated_feature_type"));
        Assert.assertEquals("bc1", event.get("bucket_conf_name"));
        String date_time = format.format(new Date(startTime * 1000));
        Assert.assertEquals(date_time, event.get("start_time"));
        date_time = format.format(new Date(endTime * 1000));
        Assert.assertEquals(date_time, event.get("end_time"));
        Assert.assertEquals("my_number_of_distinct_values", event.get("aggregated_feature_name"));
        Assert.assertEquals(numberOfDistinctValues, event.get("aggregated_feature_value"));
        Assert.assertEquals("john", ((HashMap<?, ?>) event.get("context")).get("username"));
        Assert.assertEquals("m1", ((HashMap<?, ?>) event.get("context")).get("machine"));
        Assert.assertEquals(startTime, event.get("start_time_unix"));
        Assert.assertEquals(endTime, event.get("end_time_unix"));
        Assert.assertEquals("ssh", ((JSONArray) event.get("data_sources")).get(0));

    }

    @Test
    public void testUpdateAggrFeatureEvent() throws Exception{
        AggrFeatureEventBuilder builder = createBuilder(1, 1);
        FeatureBucket bucket1 = createFeatureBucket(1);

        when(dataSourcesSyncTimer.notifyWhenDataSourcesReachTime(eq(bucket1.getDataSources()), eq(bucket1.getEndTime()), any(DataSourcesSyncTimerListener.class))).then(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                dataSourcesSyncTimerListener = (DataSourcesSyncTimerListener) args[2];
                return 1000L;
            }
        });

        when(featureBucketsService.getFeatureBucket(any(FeatureBucketConf.class), eq(bucket1.getBucketId()))).thenReturn(bucket1);
        when(aggrEventTopologyService.sendEvent(any(JSONObject.class))).then(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                event = (JSONObject) args[0];
                return true;
            }
        });

        builder.updateAggrFeatureEventData(bucket1.getBucketId(), bucket1.getStrategyId(), bucket1.getContextFieldNameToValueMap(), bucket1.getStartTime(), bucket1.getEndTime());
        dataSourcesSyncTimerListener.dataSourcesReachedTime();

        assertEvent(event, 1, 1, 4L);
    }

    @SuppressWarnings("unchecked")
	@Test
    public void testUpdateAggrFeatureEvent_3buckets() throws Exception {
        AggrFeatureEventBuilder builder = createBuilder(3, 1);
        FeatureBucket bucket1 = createFeatureBucket(1);
        FeatureBucket bucket2 = createFeatureBucket(2);
        FeatureBucket bucket3 = createFeatureBucket(3);
        FeatureBucket bucket4 = createFeatureBucket(4);

        when(dataSourcesSyncTimer.notifyWhenDataSourcesReachTime(any(List.class), any(Long.class), any(DataSourcesSyncTimerListener.class))).then(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                dataSourcesSyncTimerListener = (DataSourcesSyncTimerListener) args[2];
                return registartionID++;
            }
        });

        when(featureBucketsService.getFeatureBucket(any(FeatureBucketConf.class), eq(bucket1.getBucketId()))).thenReturn(bucket1);
        when(featureBucketsService.getFeatureBucket(any(FeatureBucketConf.class), eq(bucket2.getBucketId()))).thenReturn(bucket2);
        when(featureBucketsService.getFeatureBucket(any(FeatureBucketConf.class), eq(bucket3.getBucketId()))).thenReturn(bucket3);
        when(featureBucketsService.getFeatureBucket(any(FeatureBucketConf.class), eq(bucket4.getBucketId()))).thenReturn(bucket4);
        when(aggrEventTopologyService.sendEvent(any(JSONObject.class))).then(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                event = (JSONObject) args[0];
                return true;
            }
        });

        event = null;

        builder.updateAggrFeatureEventData(bucket1.getBucketId(), bucket1.getStrategyId(), bucket1.getContextFieldNameToValueMap(), bucket1.getStartTime(), bucket1.getEndTime());
        dataSourcesSyncTimerListener.dataSourcesReachedTime();
        Assert.assertNull(event);

        builder.updateAggrFeatureEventData(bucket2.getBucketId(), bucket2.getStrategyId(), bucket2.getContextFieldNameToValueMap(), bucket2.getStartTime(), bucket2.getEndTime());
        dataSourcesSyncTimerListener.dataSourcesReachedTime();
        Assert.assertNull(event);

        builder.updateAggrFeatureEventData(bucket3.getBucketId(), bucket3.getStrategyId(), bucket3.getContextFieldNameToValueMap(), bucket3.getStartTime(), bucket3.getEndTime());
        dataSourcesSyncTimerListener.dataSourcesReachedTime();
        assertEvent(event, 1, 3, 6L);

        builder.updateAggrFeatureEventData(bucket4.getBucketId(), bucket4.getStrategyId(), bucket4.getContextFieldNameToValueMap(), bucket4.getStartTime(), bucket4.getEndTime());
        dataSourcesSyncTimerListener.dataSourcesReachedTime();
        assertEvent(event, 2, 4, 6L);

        AggrFeatureEventData.BucketTick bucketTick = (AggrFeatureEventData.BucketTick)dataSourcesSyncTimerListener;
        NextBucketEndTimeListener nextBucketEndTimeListener = (NextBucketEndTimeListener) bucketTick.getEventData();

        nextBucketEndTimeListener.nextBucketEndTimeUpdate(new FeatureBucketStrategyData("staretegyContextID", "strategyName", startTime1 + 4 * day, endTime1 + 4 * day));
        dataSourcesSyncTimerListener.dataSourcesReachedTime();
        assertEvent(event, 3, 5, 5L);

        bucketTick = (AggrFeatureEventData.BucketTick)dataSourcesSyncTimerListener;
        nextBucketEndTimeListener = (NextBucketEndTimeListener) bucketTick.getEventData();

        nextBucketEndTimeListener.nextBucketEndTimeUpdate(new FeatureBucketStrategyData("staretegyContextID", "strategyName", startTime1 + 5 * day, endTime1 + 5 * day));
        dataSourcesSyncTimerListener.dataSourcesReachedTime();
        assertEvent(event, 4, 6, 4L);

        bucketTick = (AggrFeatureEventData.BucketTick)dataSourcesSyncTimerListener;
        nextBucketEndTimeListener = (NextBucketEndTimeListener) bucketTick.getEventData();
        nextBucketEndTimeListener.nextBucketEndTimeUpdate(new FeatureBucketStrategyData("staretegyContextID", "strategyName", startTime1 + 6 * day, endTime1 + 6 * day));
        dataSourcesSyncTimerListener.dataSourcesReachedTime();
        assertEvent(event, 5, 7, 0L);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateAggrFeatureEvent_empty_bucket_ticks() throws Exception {
        AggrFeatureEventBuilder builder = createBuilder(3, 1);
        FeatureBucket bucket1 = createFeatureBucket(1);
        FeatureBucket bucket4 = createFeatureBucket(4);

        when(dataSourcesSyncTimer.notifyWhenDataSourcesReachTime(any(List.class), any(Long.class), any(DataSourcesSyncTimerListener.class))).then(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                dataSourcesSyncTimerListener = (DataSourcesSyncTimerListener) args[2];
                return registartionID++;
            }
        });

        when(featureBucketsService.getFeatureBucket(any(FeatureBucketConf.class), eq(bucket1.getBucketId()))).thenReturn(bucket1);
        when(featureBucketsService.getFeatureBucket(any(FeatureBucketConf.class), eq(bucket4.getBucketId()))).thenReturn(bucket4);
        when(aggrEventTopologyService.sendEvent(any(JSONObject.class))).then(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                event = (JSONObject) args[0];
                return true;
            }
        });

        event = null;

        builder.updateAggrFeatureEventData(bucket1.getBucketId(), bucket1.getStrategyId(), bucket1.getContextFieldNameToValueMap(), bucket1.getStartTime(), bucket1.getEndTime());
        dataSourcesSyncTimerListener.dataSourcesReachedTime();
        Assert.assertNull(event);


        builder.updateAggrFeatureEventData(bucket4.getBucketId(), bucket4.getStrategyId(), bucket4.getContextFieldNameToValueMap(), bucket4.getStartTime(), bucket4.getEndTime());
        dataSourcesSyncTimerListener.dataSourcesReachedTime();
        assertEvent(event, 2, 4, 4L);

    }

//    @Test
//    public void testUpdateFeatureBacketEndTime() {
//        //TODO
//        Assert.assertTrue(false);
//    }


}
