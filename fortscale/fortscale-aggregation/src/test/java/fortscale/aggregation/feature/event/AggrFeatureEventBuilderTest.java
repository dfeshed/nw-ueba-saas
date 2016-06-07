package fortscale.aggregation.feature.event;

import fortscale.aggregation.DataSourcesSyncTimer;
import fortscale.aggregation.DataSourcesSyncTimerListener;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsService;
import fortscale.aggregation.feature.bucket.strategy.*;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.utils.ConversionUtils;
import junitparams.JUnitParamsRunner;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class AggrFeatureEventBuilderTest {
    private static ClassPathXmlApplicationContext testContextManager;

    private DataSourcesSyncTimer dataSourcesSyncTimer;
    private FeatureBucketsService featureBucketsService;
    private AggrEventTopologyService aggrEventTopologyService;
    private AggrFeatureEventBuilderTestHelper aggrFeatureEventBuilderTestHelper;

    private DataSourcesSyncTimerListener dataSourcesSyncTimerListener;
    private JSONObject event;
    private Long startTime1 = 1436918400L; //Wed, 15 Jul 2015 00:00:00 GMT
    private Long endTime1 = 1437004799L; //Wed, 15 Jul 2015 23:59:59 GMT
    private Long day = 86400L;
    private Long registrationID = 1000L;
    FeatureBucketStrategy strategy;

    @BeforeClass
    public static void setUpClass() {
        testContextManager = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/aggr-feature-event-builder-context.xml");
    }

    @Before
    public void setUp() throws Exception {
        dataSourcesSyncTimer = testContextManager.getBean(DataSourcesSyncTimer.class);
        featureBucketsService = testContextManager.getBean(FeatureBucketsService.class);
        aggrEventTopologyService = testContextManager.getBean(AggrEventTopologyService.class);
        aggrFeatureEventBuilderTestHelper = testContextManager.getBean(AggrFeatureEventBuilderTestHelper.class);
    }

    private AggrFeatureEventBuilder createBuilder(int numberOfBuckets, int bucketLeap) throws Exception{
        // Creating AggregatedFeatureEventConf
        Map<String, List<String>> parameters2featuresListMap = new HashMap<>();
        List<String> aggrFeatureNames = new ArrayList<>();
        aggrFeatureNames.add("letters");
        parameters2featuresListMap.put("groupBy", aggrFeatureNames);
        JSONObject funcJSONObj = new JSONObject();
        funcJSONObj.put("type", "aggr_feature_distinct_values_counter_func");
        funcJSONObj.put("includeValues", true);

        AggregatedFeatureEventConf eventConf = new AggregatedFeatureEventConf("my_number_of_distinct_values", "F", "bc1", numberOfBuckets, bucketLeap, 0, "HighestScore", parameters2featuresListMap, funcJSONObj);
        FeatureBucketConf bucketConf = mock(FeatureBucketConf.class);
        List<String> dataSources = new ArrayList<>();
        dataSources.add("ssh");
        when(bucketConf.getDataSources()).thenReturn(dataSources);
        eventConf.setBucketConf(bucketConf);


        strategy = createFixedDurationStrategy();

        // Create AggrFeatureEventBuilder
        return new AggrFeatureEventBuilder(eventConf, strategy, featureBucketsService);
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
        featureBucket.setCreatedAt(new Date());

        return featureBucket;
    }

    private void assertEvent(JSONObject event, int startTimeDayNumber, int endTimeDayNumber, Long numberOfDistinctValues) {
        //{"start_time_unix":1436918400,"max_cout_object":"c","end_time":"2015-07-16 02:59:59","bucket_conf_name":null,"event_type":"aggregated_feature_event","context":{"username":"john","machine":"m1"},"start_time":"2015-07-15 03:00:00","end_time_unix":1437004799,"date_time":"2015-07-20 10:14:49","date_time_unix":1437376489}
        System.out.println(event.toString());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

        Long startTime = startTime1 + (startTimeDayNumber - 1) * day;
        Long endTime = endTime1 + (endTimeDayNumber - 1) * day;

        Assert.assertEquals("F", event.get(AggrEvent.EVENT_FIELD_FEATURE_TYPE));
        Assert.assertEquals("bc1", event.get(aggrFeatureEventBuilderTestHelper.getBucketConfNameFieldName()));
        String date_time = format.format(new Date(startTime * 1000));
        Assert.assertEquals(date_time, event.get(AggrEvent.EVENT_FIELD_START_TIME));
        date_time = format.format(new Date(endTime * 1000));
        Assert.assertEquals(date_time, event.get(AggrEvent.EVENT_FIELD_END_TIME));
        Assert.assertEquals("my_number_of_distinct_values", event.get(aggrFeatureEventBuilderTestHelper.getAggrFeatureNameFieldName()));
        Assert.assertEquals(ConversionUtils.convertToDouble(numberOfDistinctValues), event.get(aggrFeatureEventBuilderTestHelper.getAggrFeatureNameFieldValue()));
        Assert.assertEquals("john", ((HashMap<?, ?>)event.get(aggrFeatureEventBuilderTestHelper.getAggrFeatureContextFieldName())).get("username"));
        Assert.assertEquals("m1", ((HashMap<?, ?>)event.get(aggrFeatureEventBuilderTestHelper.getAggrFeatureContextFieldName())).get("machine"));
        Assert.assertEquals(startTime, event.get(AggrEvent.EVENT_FIELD_START_TIME_UNIX));
        Assert.assertEquals(endTime, event.get(AggrEvent.EVENT_FIELD_END_TIME_UNIX));
        Assert.assertEquals("ssh", ((JSONArray)event.get(AggrEvent.EVENT_FIELD_DATA_SOURCES)).get(0));
    }

    @Test
    public void testUpdateAggrFeatureEvent() throws Exception{
        AggrFeatureEventBuilder builder = createBuilder(1, 1);
        FeatureBucket bucket1 = createFeatureBucket(1);

        long epochtime1 = bucket1.getEndTime() + AggrFeatureEventBuilder.SECONDS_TO_ADD_TO_PASS_END_TIME;
        when(dataSourcesSyncTimer.notifyWhenDataSourcesReachTime(eq(bucket1.getDataSources()), eq(epochtime1), any(DataSourcesSyncTimerListener.class))).then(new Answer<Object>() {
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
                return registrationID++;
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

        event = null;
        builder.updateAggrFeatureEventData(bucket3.getBucketId(), bucket3.getStrategyId(), bucket3.getContextFieldNameToValueMap(), bucket3.getStartTime(), bucket3.getEndTime());
        dataSourcesSyncTimerListener.dataSourcesReachedTime();
        assertEvent(event, 1, 3, 6L);

        event = null;
        builder.updateAggrFeatureEventData(bucket4.getBucketId(), bucket4.getStrategyId(), bucket4.getContextFieldNameToValueMap(), bucket4.getStartTime(), bucket4.getEndTime());
        dataSourcesSyncTimerListener.dataSourcesReachedTime();
        assertEvent(event, 2, 4, 6L);

        event = null;
        AggrFeatureEventData.BucketTick bucketTick = (AggrFeatureEventData.BucketTick)dataSourcesSyncTimerListener;
        NextBucketEndTimeListener nextBucketEndTimeListener = (NextBucketEndTimeListener) bucketTick.getEventData();

        nextBucketEndTimeListener.nextBucketEndTimeUpdate(new FeatureBucketStrategyData("staretegyContextID", "strategyName", startTime1 + 4 * day, endTime1 + 4 * day));
        dataSourcesSyncTimerListener.dataSourcesReachedTime();
        assertEvent(event, 3, 5, 5L);

        event = null;
        bucketTick = (AggrFeatureEventData.BucketTick)dataSourcesSyncTimerListener;
        nextBucketEndTimeListener = (NextBucketEndTimeListener) bucketTick.getEventData();

        nextBucketEndTimeListener.nextBucketEndTimeUpdate(new FeatureBucketStrategyData("staretegyContextID", "strategyName", startTime1 + 5 * day, endTime1 + 5 * day));
        dataSourcesSyncTimerListener.dataSourcesReachedTime();
        assertEvent(event, 4, 6, 4L);

        event = null;
        bucketTick = (AggrFeatureEventData.BucketTick)dataSourcesSyncTimerListener;
        nextBucketEndTimeListener = (NextBucketEndTimeListener) bucketTick.getEventData();
        nextBucketEndTimeListener.nextBucketEndTimeUpdate(new FeatureBucketStrategyData("staretegyContextID", "strategyName", startTime1 + 6 * day, endTime1 + 6 * day));
        dataSourcesSyncTimerListener.dataSourcesReachedTime();
        Assert.assertNull(event);

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
                return registrationID++;
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
//    public void testUpdateFeatureBucketEndTime() {
//        //TODO
//        Assert.assertTrue(false);
//    }
}
